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
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO スワイプなどで遷移した場合に当月以外のデータも表示できるようにする

        // 月の表示
        val today: LocalDate = LocalDate.now()
        val monthView: TextView = findViewById(R.id.month);
        monthView.text = getMonthView(today)

        // 表の生成
        val dateList: List<Int> = getDateList()

        var thisMonthFlag: Boolean = false
        for (i in 0 until dateList.size / 7) {
            for (j in 0..6) {
                val date = dateList.get(i * 7 + j)
                if (date == 1) {
                    thisMonthFlag = !thisMonthFlag
                }

                val textViewId = resources.getIdentifier("date${i}_${j}", "id", packageName)
                val dateView: TextView = findViewById(textViewId)
                dateView.text = dateList.get(i * 7 + j).toString()

                if (thisMonthFlag) {
                    val buttonId = resources.getIdentifier("button${i}_${j}", "id", packageName)
                    val button: Button = findViewById(buttonId)
                    button.text = getSpentMoneyText(today.withDayOfMonth(date))
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
    private fun getMonthView(date: LocalDate): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY年MM月")
        return date.format(formatter)
    }

    // 3月分のButtonのリストを返す (日曜始まり)
    private fun getDateList(): List<Int> {
        val today: LocalDate = LocalDate.now()
        val firstDayOfMonth: LocalDate = LocalDate.of(today.year, today.month, 1)
        val lastDayOfMonth: LocalDate = firstDayOfMonth.plusMonths(1).minusDays(1)

        // MONDAYが1
        var dateList: List<Int> = arrayListOf()

        var date: LocalDate = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())
        while (!date.isAfter(lastDayOfMonth)) {
            dateList += date.dayOfMonth
            date = date.plusDays(1)
        }
        while (dateList.size % 7 != 0) {
            dateList += date.dayOfMonth
            date = date.plusDays(1)
        }

        return dateList
    }

    // TODO DBとの通信回数を減らしたい (特にcategoriesは同じSQL文を何回も実行している)
    private fun getSpentMoneyText(date: LocalDate): SpannedString {
        val dateString: String = date.toString()
        // TODO 後で消す
        println("いい" + dateString)

        val db = KakeiboDatabase.getInstance(this)
        // カテゴリ
        val categories: List<Category> = db.categoryDao().getAll()
        val spendingCategoryIds = mutableListOf<Int>()
        val incomeCategoryIds = mutableListOf<Int>()
        for (category in categories) {
            if (category.isSpending) {
                spendingCategoryIds.add(category.id)
            } else {
                incomeCategoryIds.add(category.id)
            }
        }

        var spendings:Int = 0
        var incomes:Int = 0

        for (spending in db.spendingDao().getByDate(dateString)) {
            if (spending.categoryId in spendingCategoryIds) {
                spendings += spending.money
            } else {
                incomes += spending.money
            }
        }

        return buildSpannedString {
            color(Color.RED) {
                append(spendings.toString() + "円\n")
            }
            color(Color.BLUE) {
                append(incomes.toString() + "円\n")
            }
            // TODO 変数名は適当 文字数の埋め込みも
            val diff = incomes - spendings
            if (diff > 0) {
                append("+" + diff.toString() + "円")
            } else {
                append(diff.toString() + "円")
            }
        }
    }
}