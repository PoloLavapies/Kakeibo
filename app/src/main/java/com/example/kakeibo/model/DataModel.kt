package com.example.kakeibo.model

import android.content.Context
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    fun getSpendingData(year: Int, month: Int, day: Int): List<Spending> {
        val date = LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_DATE)
        return db.spendingDao().getByDate(date)
    }

    fun addSpendingData(spending: Spending) {
        db.spendingDao().insert(spending)
    }

    fun deleteSpendingData(id: Int) {
        return db.spendingDao().deleteById(id)
    }
}