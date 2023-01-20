package com.example.kakeibo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannedString
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
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
            val money: SpannedString = buildSpannedString {
                if (isSpending(spending.categoryId)) {
                    color(Color.RED) {
                        append("-${spending.money}円")
                    }
                } else {
                    color(Color.BLUE) {
                        append("+${spending.money}円")
                    }
                }
            }
            val detail: String = spending.detail

            spendingMapList.add(mutableMapOf(
                "money" to money,
                "detail" to if (detail == "") "分類:${category}" else "分類:${category} 詳細:${detail}",
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

        val addButton = findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val intent = Intent(application, AddActivity::class.java)
            intent.putExtra("date", date)
            startActivity(intent)
        }
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

    private fun isSpending(id: Int): Boolean {
        val db = KakeiboDatabase.getInstance(this)
        return db.categoryDao().isSpending(id)
    }
}