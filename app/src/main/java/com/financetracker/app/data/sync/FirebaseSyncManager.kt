package com.financetracker.app.data.sync

import com.financetracker.app.data.TransactionRepository
import com.financetracker.app.model.Transaction
import com.financetracker.app.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Synced(val lastSyncTime: Long, val count: Int) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class FirebaseSyncManager(
    private val repository: TransactionRepository
) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private suspend fun ensureAuthenticated(): String? = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                return@withContext currentUser.uid
            }
            val result = auth.signInAnonymously().await()
            return@withContext result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

    suspend fun syncAll(): Result<Int> = withContext(Dispatchers.IO) {
        _syncStatus.value = SyncStatus.Syncing
        try {
            val userId = ensureAuthenticated()
                ?: return@withContext Result.failure(Exception("Could not authenticate with cloud"))

            val userTransactionsRef = firestore.collection("users")
                .document(userId)
                .collection("transactions")

            // 1. Fetch remote transactions
            val remoteSnapshot = userTransactionsRef.get().await()
            val remoteTransactions = remoteSnapshot.documents.mapNotNull { doc ->
                val amount = doc.getDouble("amount") ?: return@mapNotNull null
                val typeStr = doc.getString("type") ?: "EXPENSE"
                val type = try {
                    TransactionType.valueOf(typeStr)
                } catch (e: Exception) {
                    TransactionType.EXPENSE
                }
                val note = doc.getString("note") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                Transaction(
                    id = 0, // Let Room auto-generate local id or match by timestamp+amount
                    amount = amount,
                    type = type,
                    note = note,
                    timestamp = timestamp
                )
            }

            // 2. Fetch local transactions
            val localTransactions = repository.allTransactions.first()

            // 3. Sync Remote -> Local (insert missing remote items locally)
            var newlyAddedLocally = 0
            for (remote in remoteTransactions) {
                val existsLocally = localTransactions.any {
                    it.timestamp == remote.timestamp && it.amount == remote.amount && it.type == remote.type
                }
                if (!existsLocally) {
                    repository.insertTransaction(remote)
                    newlyAddedLocally++
                }
            }

            // 4. Sync Local -> Remote (upload local items missing remotely)
            for (local in localTransactions) {
                val existsRemotely = remoteTransactions.any {
                    it.timestamp == local.timestamp && it.amount == local.amount && it.type == local.type
                }
                if (!existsRemotely) {
                    val docId = "${local.timestamp}_${local.id}"
                    val data = hashMapOf(
                        "amount" to local.amount,
                        "type" to local.type.name,
                        "note" to local.note,
                        "timestamp" to local.timestamp
                    )
                    userTransactionsRef.document(docId).set(data, SetOptions.merge()).await()
                }
            }

            val totalSynced = maxOf(localTransactions.size, remoteTransactions.size)
            _syncStatus.value = SyncStatus.Synced(System.currentTimeMillis(), totalSynced)
            Result.success(totalSynced)
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Sync error occurred"
            _syncStatus.value = SyncStatus.Error(errorMsg)
            Result.failure(e)
        }
    }

    suspend fun uploadTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        try {
            val userId = ensureAuthenticated() ?: return@withContext
            val docId = "${transaction.timestamp}_${transaction.id}"
            val data = hashMapOf(
                "amount" to transaction.amount,
                "type" to transaction.type.name,
                "note" to transaction.note,
                "timestamp" to transaction.timestamp
            )
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(docId)
                .set(data, SetOptions.merge())
                .await()
        } catch (_: Exception) {
            // Fails silently if offline; full sync will push it when internet is available
        }
    }

    suspend fun deleteTransactionFromCloud(transaction: Transaction) = withContext(Dispatchers.IO) {
        try {
            val userId = ensureAuthenticated() ?: return@withContext
            val docId = "${transaction.timestamp}_${transaction.id}"
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(docId)
                .delete()
                .await()
        } catch (_: Exception) {
            // Offline deletion silently ignored
        }
    }
}
