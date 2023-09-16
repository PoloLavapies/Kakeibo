package com.example.kakeibo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending
import com.example.kakeibo.model.DataModel
import java.time.LocalDate

class MainViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(context) as T
        }
    }

    // 表示対象の月
    var year: Int = 0
    var month: Int = 0

    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    var spendingCategoryIds = mutableListOf<Int>()
    var incomeCategoryIds = mutableListOf<Int>()

    private val dataModel = DataModel(context)

    // 前後の月を含む、6週分の日のリストを返す (日曜始まり)
    fun getDayList(): List<Int> {
        val firstDayOfMonth: LocalDate = LocalDate.of(year, month, 1)
        val lastDayOfMonth: LocalDate = firstDayOfMonth.plusMonths(1).minusDays(1)

        var dayList: List<Int> = arrayListOf()

        var date: LocalDate =
            firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())
        while (!date.isAfter(lastDayOfMonth)) {
            dayList += date.dayOfMonth
            date = date.plusDays(1)
        }
        while (dayList.size % 7 != 0) {
            dayList += date.dayOfMonth
            date = date.plusDays(1)
        }

        return dayList
    }

    fun initCategoryList() {
        val categories: List<Category> = dataModel.getAllCategories()

        for (category in categories) {
            if (category.isSpending) {
                spendingCategoryIds.add(category.id)
            } else {
                incomeCategoryIds.add(category.id)
            }
        }
    }

    fun getDataByDate(day: Int): List<Spending> {
        val date = LocalDate.of(year, month, day).toString()
        return dataModel.getSpendingData(date)
    }
}