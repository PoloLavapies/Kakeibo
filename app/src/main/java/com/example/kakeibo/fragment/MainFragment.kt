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
import com.example.kakeibo.R
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category
import com.kal.rackmonthpicker.RackMonthPicker
import com.kal.rackmonthpicker.listener.DateMonthDialogListener
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainFragment : Fragment() {
    private var year: Int = 0
    private var month: Int = 0
    private val spendingCategoryIds = mutableListOf<Int>()
    private val incomeCategoryIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            year = it.getInt("year")
            month = it.getInt("month")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)

        val addButton = view.findViewById<Button>(R.id.add_button)

        addButton.setOnClickListener {
            val fragment = AddDataFragment.newInstance(null)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val view: View = requireView()

        val today: LocalDate = LocalDate.now()

        val date = if (year != 0 && month != 0) {
            LocalDate.of(year, month, 1)
        } else {
            today
        }

        // 年と月の表示
        setMonthView(view, date)

        // 表の生成 (日と金額の表示)
        initCategoryList()
        val dayList: List<Int> = getDayList(date)

        // 当月の日かどうかのフラグ (先月末から来月頭の日をループで処理するにあたり必要)
        var thisMonthFlag: Boolean = false
        for (i in 0 until dayList.size / 7) {
            for (j in 0..6) {
                val dayOfMonth = dayList.get(i * 7 + j)
                if (dayOfMonth == 1) {
                    thisMonthFlag = !thisMonthFlag
                }

                val textViewId = resources.getIdentifier("date${i}_${j}", "id", requireContext().packageName)
                val dateView: TextView = view.findViewById(textViewId)
                dateView.text = dayList.get(i * 7 + j).toString()

                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier("button${i}_${j}", "id", requireContext().packageName)
                    val button: Button = view.findViewById(buttonId)
                    val date: LocalDate = date.withDayOfMonth(dayOfMonth)
                    button.text = getSpentMoneyText(date)
                    // TODO 遷移
                    /*button.setOnClickListener {
                        val intent = Intent(application, DetailActivity::class.java)
                        intent.putExtra("date", date.format(DateTimeFormatter.ISO_DATE))
                        startActivity(intent)
                    }*/
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }
    }

    private fun setMonthView(view: View, date: LocalDate) {
        val monthView: TextView = view.findViewById(R.id.month);
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY年MM月")
        monthView.text = date.format(formatter)

        // TODO クリック時の処理
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
                val fragment = newInstance(year, month)
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.commit()
            })
            .setNegativeButton(OnCancelMonthDialogListener
            { dialog ->
                dialog.dismiss()
            })
            .show()
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

    private fun initCategoryList() {
        val db = KakeiboDatabase.getInstance(requireContext())
        val categories: List<Category> = db.categoryDao().getAll()

        for (category in categories) {
            if (category.isSpending) {
                spendingCategoryIds.add(category.id)
            } else {
                incomeCategoryIds.add(category.id)
            }
        }
    }

    // TODO DBとの通信回数を減らしたい。一度の通信でクラス変数などにデータを格納しておく?
    private fun getSpentMoneyText(date: LocalDate): SpannedString {
        val dateString: String = date.toString()

        val db = KakeiboDatabase.getInstance(requireContext())

        // カテゴリ
        var spendingTotal:Int = 0
        var incomeTotal:Int = 0

        for (spending in db.spendingDao().getByDate(dateString)) {
            if (spending.categoryId in spendingCategoryIds) {
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

    companion object {
        @JvmStatic
        fun newInstance(year: Int, month: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    // yearとmonthは定数化したい。他のクラスについても同様。
                    putInt("year", year)
                    putInt("month", month)
                }
            }
    }
}