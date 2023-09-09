package com.example.kakeibo.model

import android.content.Context
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending

class DataModel(context: Context) {

    val db = KakeiboDatabase.getInstance(context)

    fun getSpendingData(date: String): List<Spending> {
        return db.spendingDao().getByDate(date)
    }

    fun getCategoryName(id: Int): String {
        return db.categoryDao().getCategoryName(id)
    }

    fun isSpending(id: Int): Boolean {
        return db.categoryDao().isSpending(id)
    }

    fun deleteSpendingData(id: Int) {
        return db.spendingDao().deleteById(id)
    }
}