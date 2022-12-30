package com.example.kakeibo.entity;

import androidx.room.ColumnInfo
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "is_spending") val isSpending: Boolean,
    val category: String
)