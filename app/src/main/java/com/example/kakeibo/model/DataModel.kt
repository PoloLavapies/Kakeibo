package com.example.kakeibo.model

import android.content.Context
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending

class DataModel(context: Context) {

    val db = KakeiboDatabase.getInstance(context)


    fun getCategoryName(id: Int): String {
        return db.categoryDao().getCategoryName(id)
    }

    fun getAllCategories(): List<Category> {
        return db.categoryDao().getAll()
    }

    fun isSpending(id: Int): Boolean {
        return db.categoryDao().isSpending(id)
    }

    fun getSpendingData(date: String): List<Spending> {
        return db.spendingDao().getByDate(date)
    }

    fun addSpendingData(spending: Spending) {
        db.spendingDao().insert(spending)
    }

    fun deleteSpendingData(id: Int) {
        return db.spendingDao().deleteById(id)
    }
}