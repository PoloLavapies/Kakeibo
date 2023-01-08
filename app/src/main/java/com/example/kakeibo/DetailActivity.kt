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
        // TODO 金額のみIntにできないか?
        val spendingMapList: MutableList<MutableMap<String, String>> = mutableListOf()
        for (spending: Spending in spendings) {
            spendingMapList.add(mutableMapOf(
                // TODO 後でカテゴリ名に直す
                "category" to spending.categoryId.toString(),
                "money" to spending.money.toString(),
                "detail" to spending.detail
            ))
        }
        val from = arrayOf("category", "money", "detail")
        val to = intArrayOf(android.R.id.text1, android.R.id.text1, android.R.id.text2)
        val adapter = SimpleAdapter(this, spendingMapList, android.R.layout.simple_list_item_2, from, to)
        val listView: ListView = findViewById(R.id.detail_list)
        listView.adapter = adapter
    }

    private fun getSpendingData(date: String): List<Spending> {
        val db = KakeiboDatabase.getInstance(this)
        return db.spendingDao().getByDate(date)
    }
}