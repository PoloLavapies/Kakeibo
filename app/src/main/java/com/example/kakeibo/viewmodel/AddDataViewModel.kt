package com.example.kakeibo.viewmodel

import androidx.lifecycle.ViewModel

class AddDataViewModel: ViewModel() {
    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    var categoryIdMapSpending = mutableMapOf<String, Int>()
    var categoryIdMapIncome = mutableMapOf<String, Int>()
    // 表示されている分類
    var isSpendingsShwon: Boolean = true
}