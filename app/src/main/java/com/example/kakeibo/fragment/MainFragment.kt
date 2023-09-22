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
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.databinding.FragmentMainBinding
import com.example.kakeibo.viewmodel.MainViewModel
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.DateMonthDialogListener
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import java.time.LocalDate
import java.util.*

class MainFragment : Fragment() {
    private val args: MainFragmentArgs by navArgs()
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

        vm.year = args.year
        vm.month = args.month

        val today = LocalDate.now()
        if (vm.year == 0 && vm.month == 0) {
            vm.year = today.year
            vm.month = today.monthValue
        }

        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val action =
                MainFragmentDirections.actionMainToAddData(vm.year, vm.month, today.dayOfMonth)
            findNavController().navigate(action)
        }

        // TODO year・monthに変更があった場合、以下を再度回す

        // 年と月の表示
        setPageTitle(view, vm.year, vm.month)

        // 表の生成 (日と金額の表示)
        vm.initCategoryList()
        createTable(view, vm.year, vm.month)

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
            // JAPANESEかも
            .setLocale(Locale.ENGLISH)
            // TODO Yearは機能するが、Monthは機能していない (当月が必ず選択される)
            .setSelectedMonth(month)
            .setSelectedYear(year)
            .setPositiveButton(DateMonthDialogListener
            { month, startDate, endDate, year, monthLabel ->
                // TODO 同じ画面での遷移なので、viewModelやliveDataなどで対応したい
                val action = MainFragmentDirections.actionMainToMain(year, month)
                findNavController().navigate(action)
            })
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
                dateView.text = dayList.get(i * 7 + j).toString()

                // 当月の日の場合、タップ可能にする
                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier(
                        "button${i}_${j}",
                        "id",
                        requireContext().packageName
                    )
                    val button: Button = view.findViewById(buttonId)
                    button.text = getSpentMoneyText(day)
                    button.setOnClickListener {
                        // TODO DatabaseModelまでyear, month, valueで渡すようにしたい
                        val action =
                            MainFragmentDirections.actionMainToDetail(vm.year, vm.month, day)
                        findNavController().navigate(action)
                    }
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }
    }

    // TODO DBとの通信回数を減らしたい。その月の全データを取得してViewModelで保持し、必要な日のデータだけ取得するのはどうか?
    // TODO そもそもViewModelに書くべきか?
    private fun getSpentMoneyText(day: Int): SpannedString {
        // カテゴリ
        var spendingTotal = 0
        var incomeTotal = 0

        for (spending in vm.getDataByDate(day)) {
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