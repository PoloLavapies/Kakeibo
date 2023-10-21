package com.example.kakeibo.dao

import androidx.room.*
import com.example.kakeibo.entity.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategory(id: Int): Category

    @Insert
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Query("DELETE FROM category WHERE id = :id")
    suspend fun deleteById(id: Int)
}