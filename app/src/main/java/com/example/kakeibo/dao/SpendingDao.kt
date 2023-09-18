package com.example.kakeibo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kakeibo.entity.Spending

@Dao
interface SpendingDao {
    @Query("SELECT * FROM spending")
    fun getAll(): List<Spending>

    @Query("SELECT * FROM spending WHERE date = :date")
    fun getByDate(date: String): List<Spending>

    @Query("SELECT * FROM spending WHERE id = :id")
    fun getById(id: String): List<Spending>

    @Insert
    fun insert(spending: Spending)

    @Update
    fun update(spending: Spending)

    @Query("DELETE FROM spending WHERE id = :id")
    suspend fun deleteById(id: Int)
}