package com.example.kakeibo.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kakeibo.R
import com.example.kakeibo.databinding.FragmentMainBinding
import com.example.kakeibo.viewmodel.MainViewModel
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import java.time.LocalDate
import java.util.*

class MainFragment : Fragment() {
    private val vm: MainViewModel by viewModels {
        MainViewModel.Factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.vm = vm
        val view: View = binding.root

        vm.date.observe(viewLifecycleOwner) {
            // 年と月の表示
            setPageTitle(view, vm.date.value!!.year, vm.date.value!!.monthValue)

            // 表の生成 (日と金額の表示)
            vm.initCategoryList()
            createTable(view, vm.date.value!!.year, vm.date.value!!.monthValue)
        }

        val today = LocalDate.now()
        // 表示する月を当月に設定 (起動時のみ実行される)
        if (vm.date.value == null) {
            vm.date.value = LocalDate.now()
        }

        // 右下の追加ボタンをタップした場合、入力される日付のデフォルト値は当日
        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainToAddData(
                    today.year,
                    today.monthValue,
                    today.dayOfMonth
                )
            findNavController().navigate(action)
        }

        return view
    }

    private fun setPageTitle(view: View, year: Int, month: Int) {
        val monthView: TextView = view.findViewById(R.id.month)
        monthView.text = year.toString() + "年" + month.toString() + "月"

        monthView.setOnClickListener() {
            showMonthPickerDialog(year, month)
        }
    }

    private fun showMonthPickerDialog(year: Int, month: Int) {
        RackMonthPicker(requireContext())
            .setLocale(Locale.ENGLISH)
            // TODO Yearは機能するが、Monthは機能していない (当月が必ず選択される)
            .setSelectedMonth(month)
            .setSelectedYear(year)
            .setPositiveButton { month, _, _, year, _ ->
                vm.date.value = LocalDate.of(year, month, 1)
            }
            .setNegativeButton(OnCancelMonthDialogListener
            { dialog ->
                dialog.dismiss()
            })
            .show()
    }

    private fun createTable(view: View, year: Int, month: Int) {
        // ループ内で処理している週が当月であるかを示すフラグ
        var thisMonthFlag = false
        val dayList: List<Int> = vm.getDayList()
        for (i in 0 until dayList.size / 7) {
            for (j in 0..6) {
                val day = dayList.get(i * 7 + j)
                if (day == 1) {
                    thisMonthFlag = !thisMonthFlag
                }

                val textViewId =
                    resources.getIdentifier("date${i}_${j}", "id", requireContext().packageName)
                val dateView: TextView = view.findViewById(textViewId)
                dateView.text = dayList[i * 7 + j].toString()

                // 当月の日の場合、タップ可能にする
                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier(
                        "button${i}_${j}",
                        "id",
                        requireContext().packageName
                    )
                    val button: Button = view.findViewById(buttonId)

                    button.text = getSpentMoneyText(year, month, day)
                    button.setOnClickListener {
                        val action =
                            MainFragmentDirections.actionMainToDetail(
                                year,
                                month,
                                day
                            )
                        findNavController().navigate(action)
                    }
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }
    }

    private fun getSpentMoneyText(year: Int, month: Int, day: Int): SpannedString {
        // カテゴリ
        var spendingTotal = 0
        var incomeTotal = 0

        for (spending in vm.getDataByDate(year, month, day)) {
            if (spending.categoryId in vm.spendingCategoryIds) {
                spendingTotal += spending.money
            } else {
                incomeTotal += spending.money
            }
        }

        return buildSpannedString {
            color(Color.RED) {
                append("${"%,d".format(spendingTotal)}円\n")
            }
            color(Color.BLUE) {
                append("${"%,d".format(incomeTotal)}円\n")
            }
            val savings = incomeTotal - spendingTotal
            if (savings > 0) {
                append("+${"%,d".format(savings)}円")
            } else {
                append("${"%,d".format(savings)}円")
            }
        }
    }
}