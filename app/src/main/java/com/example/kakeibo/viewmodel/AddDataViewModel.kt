package com.example.kakeibo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kakeibo.database.DatabaseModel
import com.example.kakeibo.entity.Spending

class AddDataViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddDataViewModel(context) as T
        }
    }

    private val dbModel = DatabaseModel(context)

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    // 分類目のリスト
    var categoryNameListSpending = mutableListOf<String>()
    var categoryNameListIncome = mutableListOf<String>()

    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    private var categoryIdMapSpending = mutableMapOf<String, Int>()
    private var categoryIdMapIncome = mutableMapOf<String, Int>()

    // 表示されている分類
    private val _isSpendingsShown = MutableLiveData(true)
    val isSpendingsShown: LiveData<Boolean> get() = _isSpendingsShown

    init {
        val categories = dbModel.getAllCategories()

        for (category in categories) {
            if (category.isSpending) {
                categoryNameListSpending.add(category.name)
                categoryIdMapSpending[category.name] = category.id
            } else {
                categoryNameListIncome.add(category.name)
                categoryIdMapIncome[category.name] = category.id
            }
        }
    }

    fun changeCategoryListToSpending() {
        _isSpendingsShown.value = true
    }

    fun changeCategoryListToIncome() {
        _isSpendingsShown.value = false
    }

    fun getDate(): String {
        val monthPadding = "%02d".format(month)
        val dayPadding = "%02d".format(day)
        return "${year}-${monthPadding}-${dayPadding}"
    }

    fun addData(categoryName: String, money: Int, dateStr: String, detail: String) {
        val categoryId: Int = if (isSpendingsShown.value!!) {
            categoryIdMapSpending[categoryName]!!
        } else {
            categoryIdMapIncome[categoryName]!!
        }
        val spending = Spending(0, categoryId, money, dateStr, detail)

        dbModel.addSpendingData(spending)
    }
}