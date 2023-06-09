package com.example.kakeibo.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.example.kakeibo.R
import com.example.kakeibo.adapter.DetailViewAdapter
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending

class DetailFragment : Fragment() {
    private var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString("date")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val view: View = inflater.inflate(R.layout.fragment_detail, container, false)

        val spendings: List<Spending> = getSpendingData(date)
        val spendingMapList: MutableList<MutableMap<String, Any>> = mutableListOf()

        if (spendings.isNotEmpty()) {
            view.findViewById<TextView>(R.id.text_no_data).visibility = View.GONE
        }

        for (spending: Spending in spendings) {
            // TODO このメソッドを毎回呼び出さず、IDと分類名の対応表をメモリに載せておく実装もアリ?
            val category: String = getCategoryName(spending.categoryId)
            val money: SpannedString = buildSpannedString {
                if (isSpending(spending.categoryId)) {
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

            spendingMapList.add(mutableMapOf(
                "money" to money,
                "detail" to if (detail == "") "分類:${category}" else "分類:${category} 詳細:${detail}",
                // 以下の要素は表示はしない
                "spendingId" to spending.id
            ))
        }

        val listView: ListView = view.findViewById(R.id.detail_list)
        listView.adapter = DetailViewAdapter(
            requireContext(),
            this,
            spendingMapList,
            R.layout.activity_detail_row,
            arrayOf("money", "detail"),
            intArrayOf(R.id.money, R.id.detail)
        )

        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val fragment = AddDataFragment.newInstance(date)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

        return view
    }

    private fun getSpendingData(date: String): List<Spending> {
        val db = KakeiboDatabase.getInstance(requireContext())
        return db.spendingDao().getByDate(date)
    }

    fun deleteSpendingData(id: Int) {
        val db = KakeiboDatabase.getInstance(requireContext())
        return db.spendingDao().deleteById(id)
    }

    private fun getCategoryName(id: Int): String {
        val db = KakeiboDatabase.getInstance(requireContext())
        return db.categoryDao().getCategoryName(id)
    }

    private fun isSpending(id: Int): Boolean {
        val db = KakeiboDatabase.getInstance(requireContext())
        return db.categoryDao().isSpending(id)
    }

    companion object {
        @JvmStatic
        fun newInstance(date: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString("date", date)
                }
            }
    }
}