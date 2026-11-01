package com.financetracker.app.data

import com.financetracker.app.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    fun getTransactionsSince(startTime: Long): Flow<List<Transaction>> =
        dao.getTransactionsSince(startTime)

    suspend fun insertTransaction(transaction: Transaction): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}
