package com.financetracker.app.model

enum class TransactionType(val displayName: String) {
    INCOME("Received"),
    INVESTMENT("Invested"),
    EXPENSE("Spent")
}

enum class TimeFilter(val label: String) {
    WEEK("This Week"),
    MONTH("This Month"),
    YEAR("This Year"),
    ALL("All Time")
}
