package com.example.kakeibo.viewmodel

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    // 表示対象の月
    var year: Int = 0
    var month: Int = 0
    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    var spendingCategoryIds = mutableListOf<Int>()
    var incomeCategoryIds = mutableListOf<Int>()
}