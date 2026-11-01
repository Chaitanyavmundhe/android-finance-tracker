package com.financetracker.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financetracker.app.data.TransactionRepository
import com.financetracker.app.model.TimeFilter
import com.financetracker.app.model.Transaction
import com.financetracker.app.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

import com.financetracker.app.data.sync.FirebaseSyncManager
import com.financetracker.app.data.sync.SyncStatus
import com.google.firebase.auth.FirebaseUser

data class FinanceSummary(
    val totalIncome: Double = 0.0,
    val totalInvestment: Double = 0.0,
    val totalExpense: Double = 0.0,
    val idleCash: Double = 0.0
)

enum class BottomNavTab {
    DASHBOARD,
    LEDGER,
    BUDGETS,
    ANALYTICS
}

class FinanceViewModel(
    private val repository: TransactionRepository,
    private val syncManager: FirebaseSyncManager? = null
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTH)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomNavTab.DASHBOARD)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    private val _updateStatus = MutableStateFlow<com.financetracker.app.updater.UpdateStatus>(com.financetracker.app.updater.UpdateStatus.Idle)
    val updateStatus: StateFlow<com.financetracker.app.updater.UpdateStatus> = _updateStatus.asStateFlow()

    val syncStatus: StateFlow<SyncStatus> = syncManager?.syncStatus
        ?: MutableStateFlow(SyncStatus.Idle).asStateFlow()

    val currentUser: StateFlow<FirebaseUser?> = syncManager?.currentUser
        ?: MutableStateFlow(null).asStateFlow()

    init {
        checkForUpdates(silent = true)
        triggerSync()
    }

    fun getGoogleSignInIntent(context: android.content.Context): android.content.Intent? {
        return syncManager?.getGoogleSignInIntent(context)
    }

    fun handleGoogleSignInResult(idToken: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = syncManager?.signInWithGoogle(idToken)
            onComplete(result?.isSuccess == true)
        }
    }

    fun signOut(context: android.content.Context) {
        syncManager?.signOut(context)
    }

    fun checkForUpdates(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Checking
            val result = com.financetracker.app.updater.AppUpdater.checkForUpdates()
            result.onSuccess { info ->
                if (info.isUpdateAvailable) {
                    _updateStatus.value = com.financetracker.app.updater.UpdateStatus.UpdateAvailable(info)
                } else {
                    if (!silent) _updateStatus.value = com.financetracker.app.updater.UpdateStatus.UpToDate
                }
            }.onFailure { error ->
                if (!silent) {
                    _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Error(
                        error.localizedMessage ?: "Failed to check for updates"
                    )
                }
            }
        }
    }

    fun downloadAndInstallUpdate(context: android.content.Context, info: com.financetracker.app.updater.UpdateInfo) {
        viewModelScope.launch {
            _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Downloading(0f)
            val result = com.financetracker.app.updater.AppUpdater.downloadApk(context, info.downloadUrl) { progress ->
                _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Downloading(progress)
            }
            result.onSuccess { apkFile ->
                _updateStatus.value = com.financetracker.app.updater.UpdateStatus.ReadyToInstall(apkFile)
                com.financetracker.app.updater.AppUpdater.installApk(context, apkFile)
            }.onFailure { error ->
                _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Error(
                    error.localizedMessage ?: "Download failed"
                )
            }
        }
    }

    fun dismissUpdateDialog() {
        _updateStatus.value = com.financetracker.app.updater.UpdateStatus.Idle
    }

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<Transaction>> = _selectedFilter
        .flatMapLatest { filter ->
            val startTime = calculateStartTime(filter)
            repository.getTransactionsSince(startTime)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val summary: StateFlow<FinanceSummary> = transactions
        .map { list ->
            var income = 0.0
            var invested = 0.0
            var spent = 0.0
            for (item in list) {
                when (item.type) {
                    TransactionType.INCOME -> income += item.amount
                    TransactionType.INVESTMENT -> invested += item.amount
                    TransactionType.EXPENSE -> spent += item.amount
                }
            }
            val idle = (income - spent - invested).coerceAtLeast(0.0)
            FinanceSummary(
                totalIncome = income,
                totalInvestment = invested,
                totalExpense = spent,
                idleCash = idle
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceSummary()
        )

    fun setFilter(filter: TimeFilter) {
        _selectedFilter.value = filter
    }

    fun setTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun addTransaction(amount: Double, type: TransactionType, note: String) {
        if (amount <= 0) return
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                note = note.trim(),
                timestamp = System.currentTimeMillis()
            )
            val generatedId = repository.insertTransaction(transaction)
            syncManager?.uploadTransaction(transaction.copy(id = generatedId))
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            syncManager?.deleteTransactionFromCloud(transaction)
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            syncManager?.syncAll()
        }
    }

    private fun calculateStartTime(filter: TimeFilter): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (filter) {
            TimeFilter.WEEK -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            TimeFilter.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
            TimeFilter.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }
}

class FinanceViewModelFactory(
    private val repository: TransactionRepository,
    private val syncManager: FirebaseSyncManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository, syncManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
