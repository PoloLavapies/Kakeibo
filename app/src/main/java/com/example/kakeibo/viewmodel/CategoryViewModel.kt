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

    var incomeCategoryList: MutableList<Category> = mutableListOf()
    var spendingCategoryList: MutableList<Category> = mutableListOf()

    fun init() {
        for (category in dbModel.getAllCategories()) {
            if (category.isSpending) {
                spendingCategoryList.add(category)
            } else {
                incomeCategoryList.add(category)
            }
        }
    }

    fun addCategory(isSpending: Boolean, name: String) {
        val categoryToAdd = Category(0, isSpending, name)
        dbModel.addCategory(categoryToAdd)
        if (isSpending) {
            spendingCategoryList.add(categoryToAdd)
        } else {
            incomeCategoryList.add(categoryToAdd)
        }
    }

    suspend fun deleteCategory(id: Int) {
        dbModel.deleteCategory(id)
    }
}