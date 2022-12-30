package com.example.kakeibo.entity

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "spending")
data class Spending(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val categoryId: Int,
    val money: Int,
    val date: String,
    val detail: String
)