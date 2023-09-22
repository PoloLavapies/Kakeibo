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

    // TODO 事前に当月のデータをメモリに載せておき、該当の日のデータを返す方が軽くなる?
    fun getDataByDate(day: Int): List<Spending> {
        return dbModel.getSpendingData(date.value!!.year, date.value!!.monthValue, day)
    }
}