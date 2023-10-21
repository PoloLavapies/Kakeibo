package com.example.kakeibo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kakeibo.database.DatabaseModel
import com.example.kakeibo.entity.Category

class CategoryViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryViewModel(context) as T
        }
    }

    private val dbModel = DatabaseModel(context)

    var incomeCategories: MutableList<MutableMap<String, Any>> = mutableListOf()
    var spendingCategories: MutableList<MutableMap<String, Any>> = mutableListOf()

    fun init() {
        for (category in dbModel.getAllCategories()) {
            if (category.isSpending) {
                spendingCategories.add(
                    mutableMapOf(
                        "id" to category.id,
                        "name" to category.name
                    )
                )
            } else {
                incomeCategories.add(
                    mutableMapOf(
                        "id" to category.id,
                        "name" to category.name
                    )
                )
            }
        }
    }

    fun addCategory(isSpending: Boolean, name: String) {
        dbModel.addCategory(Category(0, isSpending, name))
    }

    suspend fun deleteCategory(id: Int) {
        dbModel.deleteCategory(id)
    }
}