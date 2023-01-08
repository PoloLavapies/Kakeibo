package com.example.kakeibo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val date: String = intent.getStringExtra("date").toString()

        val spendings: List<Spending> = getSpendingData(date)
        val spendingMapList: MutableList<MutableMap<String, String>> = mutableListOf()
        for (spending: Spending in spendings) {
            val category: String = getCategoryName(spending.categoryId)
            val money: String = spending.money.toString()
            val detail: String = spending.detail
            spendingMapList.add(mutableMapOf(
                "money" to "${money}円",
                "detail" to "分類:${category} 詳細:${detail}円"
            ))
        }

        val listView: ListView = findViewById(R.id.detail_list)
        listView.adapter = SimpleAdapter(
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

    private fun getCategoryName(id: Int): String {
        val db = KakeiboDatabase.getInstance(this)
        return db.categoryDao().getCategoryName(id)
    }
}