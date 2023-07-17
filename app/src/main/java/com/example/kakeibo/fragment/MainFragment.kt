package com.example.kakeibo.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannedString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category
import com.example.kakeibo.viewmodel.AddDataViewModel
import com.example.kakeibo.viewmodel.MainViewModel
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.DateMonthDialogListener
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainFragment : Fragment() {
    private val args: MainFragmentArgs by navArgs()
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        viewModel.year = args.year
        viewModel.month = args.month

        val addButton = view.findViewById<Button>(R.id.add_button)

        addButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainToAddData("")
            findNavController().navigate(action)
        }

        val today: LocalDate = LocalDate.now()

        val date = if (viewModel.year != 0 && viewModel.month != 0) {
            LocalDate.of(viewModel.year, viewModel.month, 1)
        } else {
            today
        }

        // 年と月の表示
        setPageTitle(view, date)

        // 表の生成 (日と金額の表示)
        viewModel.initCategoryList(requireContext())
        createTable(date, view)

        return view
    }

    private fun setPageTitle(view: View, date: LocalDate) {
        val monthView: TextView = view.findViewById(R.id.month);
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY年MM月")
        monthView.text = date.format(formatter)

        monthView.setOnClickListener() {
            showMonthPickerDialog(date)
        }
    }

    private fun showMonthPickerDialog(date: LocalDate) {
        RackMonthPicker(requireContext())
            // JAPANESEかも
            .setLocale(Locale.ENGLISH)
            // TODO Yearは機能するが、Monthは機能していない (当月が必ず選択される)
            .setSelectedMonth(date.monthValue)
            .setSelectedYear(date.year)
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

    private fun createTable(date: LocalDate, view: View) {
        // ループ内で処理している週が当月であるかを示すフラグ
        var thisMonthFlag: Boolean = false
        val dayList: List<Int> = getDayList(date)
        for (i in 0 until dayList.size / 7) {
            for (j in 0..6) {
                val dayOfMonth = dayList.get(i * 7 + j)
                if (dayOfMonth == 1) {
                    thisMonthFlag = !thisMonthFlag
                }

                val textViewId =
                    resources.getIdentifier("date${i}_${j}", "id", requireContext().packageName)
                val dateView: TextView = view.findViewById(textViewId)
                dateView.text = dayList.get(i * 7 + j).toString()

                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier(
                        "button${i}_${j}",
                        "id",
                        requireContext().packageName
                    )
                    val button: Button = view.findViewById(buttonId)
                    val date: LocalDate = date.withDayOfMonth(dayOfMonth)
                    button.text = getSpentMoneyText(date)
                    button.setOnClickListener {
                        val dateIsoFormat: String = date.format(DateTimeFormatter.ISO_DATE)
                        val action = MainFragmentDirections.actionMainToDetail(dateIsoFormat)
                        findNavController().navigate(action)
                    }
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }
    }

    // 前後の月を含む、6週分の日のリストを返す (日曜始まり)
    private fun getDayList(dayOfThisMonth: LocalDate): List<Int> {
        val firstDayOfMonth: LocalDate = LocalDate.of(dayOfThisMonth.year, dayOfThisMonth.month, 1)
        val lastDayOfMonth: LocalDate = firstDayOfMonth.plusMonths(1).minusDays(1)

        var dayList: List<Int> = arrayListOf()

        var date: LocalDate = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())
        while (!date.isAfter(lastDayOfMonth)) {
            dayList += date.dayOfMonth
            date = date.plusDays(1)
        }
        while (dayList.size % 7 != 0) {
            dayList += date.dayOfMonth
            date = date.plusDays(1)
        }

        return dayList
    }

    // TODO DBとの通信回数を減らしたい。一度の通信でクラス変数などにデータを格納しておく?
    private fun getSpentMoneyText(date: LocalDate): SpannedString {
        val dateString: String = date.toString()

        val db = KakeiboDatabase.getInstance(requireContext())

        // カテゴリ
        var spendingTotal:Int = 0
        var incomeTotal:Int = 0

        for (spending in db.spendingDao().getByDate(dateString)) {
            if (spending.categoryId in viewModel.spendingCategoryIds) {
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