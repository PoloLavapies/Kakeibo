package com.example.kakeibo.viewmodel

import android.content.Context
import android.graphics.Color
import android.text.SpannedString
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.kakeibo.entity.Spending
import com.example.kakeibo.model.DataModel

class DetailViewModel(context: Context) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(context) as T
        }
    }

    private val dataModel = DataModel(context)

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var noDataTextVisible: Boolean = true
    lateinit var spendings: List<Spending>
    lateinit var spendingMapList: MutableList<MutableMap<String, Any>>

    fun init() {
        spendings = dataModel.getSpendingData(year, month, day)
        if (spendings.isNotEmpty()) {
            noDataTextVisible = false
        }

        spendingMapList = mutableListOf()
        for (spending: Spending in spendings) {
            // TODO このメソッドを毎回呼び出さず、IDと分類名の対応表をメモリに載せて処理する
            val category: String = dataModel.getCategoryName(spending.categoryId)
            val money: SpannedString = buildSpannedString {
                // TODO ここもDB呼び出しをしない
                if (dataModel.isSpending(spending.categoryId)) {
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
                    "detail" to if (detail == "") "分類:${category}" else "分類:${category} 詳細:${detail}",
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
        dataModel.deleteSpendingData(id)
    }
}