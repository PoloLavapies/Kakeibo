package com.example.kakeibo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.kakeibo.adapter.DetailViewAdapter
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val date: String = intent.getStringExtra("date").toString()

        val spendings: List<Spending> = getSpendingData(date)
        val spendingMapList: MutableList<MutableMap<String, Any>> = mutableListOf()
        for (spending: Spending in spendings) {
            val category: String = getCategoryName(spending.categoryId)
            val money: String = spending.money.toString()
            // TODO 未記入の場合の表示 「:」で行が切れるのは避けたい
            val detail: String = spending.detail
            spendingMapList.add(mutableMapOf(
                "money" to "${money}円",
                "detail" to "分類:${category} 詳細:${detail}",
                // 以下の要素は表示はしない
                "spendingId" to spending.id
            ))
        }

        val listView: ListView = findViewById(R.id.detail_list)
        listView.adapter = DetailViewAdapter(
            this,
            spendingMapList,
            R.layout.activity_detail_row,
            arrayOf("money", "detail"),
            intArrayOf(R.id.money, R.id.detail)
        )
    }

    private fun getSpendingData(date: String): List<Spending> {
        val db = KakeiboDatabase.getInstance(this)
        return db.spendingDao().getByDate(date)
    }

    fun deleteSpendingData(id: Int) {
        val db = KakeiboDatabase.getInstance(this)
        return db.spendingDao().deleteById(id)
    }

    private fun getCategoryName(id: Int): String {
        val db = KakeiboDatabase.getInstance(this)
        return db.categoryDao().getCategoryName(id)
    }
}