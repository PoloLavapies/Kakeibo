package com.example.kakeibo.database

import android.content.Context
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatabaseModel(context: Context) {

    val db = KakeiboDatabase.getInstance(context)


    fun getCategory(id: Int): Category {
        return db.categoryDao().getCategory(id)
    }

    fun getAllCategories(): List<Category> {
        return db.categoryDao().getAll()
    }

    fun getSpendingData(year: Int, month: Int, day: Int): List<Spending> {
        val date = LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_DATE)
        return db.spendingDao().getByDate(date)
    }

    fun getSpendingDataByMonth(year: Int, month: Int): List<Spending> {
        return db.spendingDao().getByMonth(year, month)
    }

    fun addSpendingData(spending: Spending) {
        db.spendingDao().insert(spending)
    }

    suspend fun deleteSpendingData(id: Int) {
        return db.spendingDao().deleteById(id)
    }
}