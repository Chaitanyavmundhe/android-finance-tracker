package com.financetracker.app

import android.app.Application
import com.financetracker.app.data.AppDatabase
import com.financetracker.app.data.TransactionRepository

class FinanceApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}
