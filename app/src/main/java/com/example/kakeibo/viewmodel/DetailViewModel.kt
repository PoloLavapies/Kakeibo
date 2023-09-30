package com.example.kakeibo.viewmodel

import android.content.Context
import android.graphics.Color
import android.text.SpannedString
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kakeibo.database.DatabaseModel
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending

class DetailViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(context) as T
        }
    }

    private val dbModel = DatabaseModel(context)

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var noDataTextVisible: Boolean = true
    lateinit var spendings: List<Spending>
    lateinit var spendingMapList: MutableList<MutableMap<String, Any>>

    fun init() {
        spendings = dbModel.getSpendingData(year, month, day)
        if (spendings.isNotEmpty()) {
            noDataTextVisible = false
        }

        spendingMapList = mutableListOf()
        for (spending: Spending in spendings) {
            val category: Category = dbModel.getCategory(spending.categoryId)
            val money: SpannedString = buildSpannedString {
                if (category.isSpending) {
                    color(Color.RED) {
                        append("-${"%,d".format(spending.money)}円")
                    }
                } else {
                    color(Color.BLUE) {
                        append("+${"%,d".format(spending.money)}円")
                    }
                }
            }
            val detail: String = spending.detail

            spendingMapList.add(
                mutableMapOf(
                    "money" to money,
                    "detail" to if (detail == "") "分類:${category.name}" else "分類:${category.name} 詳細:${detail}",
                    // 以下の要素は表示はしない
                    "spendingId" to spending.id
                )
            )
        }
    }

    fun getDate(): String {
        return "${year}年${month}月${day}日"
    }

    suspend fun deleteSpendingData(id: Int) {
        dbModel.deleteSpendingData(id)
    }
}