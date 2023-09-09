package com.example.kakeibo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category

class MainViewModel: ViewModel() {
    // 表示対象の月
    var year: Int = 0
    var month: Int = 0
    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    var spendingCategoryIds = mutableListOf<Int>()
    var incomeCategoryIds = mutableListOf<Int>()

    // TODO 初期化処理は別のところに書きたい
    //  以下によるとinit{}で良さそう
    //  https://qiita.com/kame_yang/items/a88c316b5fd90c212d78
    fun initCategoryList(context: Context) {
        val db = KakeiboDatabase.getInstance(context)
        val categories: List<Category> = db.categoryDao().getAll()

        for (category in categories) {
            if (category.isSpending) {
                spendingCategoryIds.add(category.id)
            } else {
                incomeCategoryIds.add(category.id)
            }
        }
    }
}