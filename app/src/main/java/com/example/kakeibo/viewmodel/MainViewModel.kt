package com.example.kakeibo.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kakeibo.database.DatabaseModel
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending
import java.time.LocalDate

class MainViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(context) as T
        }
    }

    // 表示対象の月
    val date = MutableLiveData<LocalDate>(null)

    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    var spendingCategoryIds = mutableListOf<Int>()
    var incomeCategoryIds = mutableListOf<Int>()

    // 当月のデータ
    var spendingDataMap = mutableMapOf<Int, MutableList<Spending>>()
    var spendingDataMapInitialized = false

    private val dbModel = DatabaseModel(context)

    // 前後の月を含む、6週分の日のリストを返す (日曜始まり)
    fun getDayList(): List<Int> {
        val firstDayOfMonth: LocalDate = LocalDate.of(date.value!!.year, date.value!!.month, 1)
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
        val categories: List<Category> = dbModel.getAllCategories()

        for (category in categories) {
            if (category.isSpending) {
                spendingCategoryIds.add(category.id)
            } else {
                incomeCategoryIds.add(category.id)
            }
        }
    }

    fun getDataByDate(year: Int, month: Int, day: Int): List<Spending> {
        // spendingDataMapに当月のデータが格納されていない場合は格納
        if (!spendingDataMapInitialized || date.value?.year != year || date.value?.monthValue != month) {
            spendingDataMapInitialized = true
            createSpendingDataMap(year, month)
        }
        return spendingDataMap[day] ?: emptyList()
    }

    private fun createSpendingDataMap(year: Int, month: Int) {
        val spendingDataList = dbModel.getSpendingDataByMonth(year, month)

        for (spending in spendingDataList) {
            val day = spending.date.substring(8, 10).toIntOrNull()!!
            spendingDataMap[day]?.let {
                it.add(spending)
            } ?: run {
                spendingDataMap[day] = mutableListOf(spending)
            }
        }
    }
}