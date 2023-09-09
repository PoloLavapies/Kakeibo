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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.adapter.DetailViewAdapter
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.databinding.FragmentDetailBinding
import com.example.kakeibo.entity.Spending
import com.example.kakeibo.viewmodel.AddDataViewModel
import com.example.kakeibo.viewmodel.DetailViewModel
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    private val vm: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm.date = args.date

        val binding: FragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.vm = vm

        val view: View = binding.root

        val spendings: List<Spending> = getSpendingData(vm.date)
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
            R.layout.fragment_detail_row,
            arrayOf("money", "detail"),
            intArrayOf(R.id.money, R.id.detail)
        )

        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailToAddData(vm.date)
            findNavController().navigate(action)
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
}