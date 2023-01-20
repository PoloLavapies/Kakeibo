package com.example.kakeibo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannedString
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Category
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private val spendingCategoryIds = mutableListOf<Int>()
    private val incomeCategoryIds = mutableListOf<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO スワイプなどで遷移した場合に当月以外のデータも表示できるようにする
        //  Intentを使い、年と月を受け取れるようにするのが良さそう
        val today: LocalDate = LocalDate.now()

        // 月の表示
        setMonthView(today)

        // 表の生成
        initCategoryList()
        val dayList: List<Int> = getDayList(today)

        var thisMonthFlag: Boolean = false
        for (i in 0 until dayList.size / 7) {
            for (j in 0..6) {
                val dayOfMonth = dayList.get(i * 7 + j)
                if (dayOfMonth == 1) {
                    thisMonthFlag = !thisMonthFlag
                }

                val textViewId = resources.getIdentifier("date${i}_${j}", "id", packageName)
                val dateView: TextView = findViewById(textViewId)
                dateView.text = dayList.get(i * 7 + j).toString()

                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier("button${i}_${j}", "id", packageName)
                    val button: Button = findViewById(buttonId)
                    val date: LocalDate = today.withDayOfMonth(dayOfMonth)
                    button.text = getSpentMoneyText(date)
                    button.setOnClickListener {
                        val intent = Intent(application, DetailActivity::class.java)
                        intent.putExtra("date", date.format(DateTimeFormatter.ISO_DATE))
                        startActivity(intent)
                    }
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }

        val addButton = findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val intent = Intent(application, AddActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView(date: LocalDate) {
        val monthView: TextView = findViewById(R.id.month);
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY年MM月")
        monthView.text = date.format(formatter)
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
        val db = KakeiboDatabase.getInstance(this)
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

        val db = KakeiboDatabase.getInstance(this)

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
}