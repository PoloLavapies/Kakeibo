package com.example.kakeibo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kakeibo.entity.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE is_spending = 'true'")
    fun getAllSpendingCategory(): List<Category>

    @Query("SELECT * FROM category WHERE is_spending = 'false'")
    fun getAllIncomeCategory(): List<Category>

    // TODO エラーになるので調査
    /*@Query("SELECT * FROM spending WHERE id = :id")
    fun getById(id: String): List<Category>*/

    @Insert
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)
}