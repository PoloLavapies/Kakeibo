package com.example.kakeibo

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannedString
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO スワイプなどで遷移した場合に当月以外のデータも表示できるようにする

        // 月の表示
        val month: TextView = findViewById(R.id.month);
        month.text = getMonth()

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
                    button.text = getSpentMoneyText()
                } else {
                    dateView.setTextColor(Color.parseColor("lightgray"))
                }
            }
        }
    }

    // TODO このメソッドを呼び出すところで落ちているので、ログを仕込んでみても良いと思う
    /*public fun add() {
        println("あいうえお")
        //Intent intent = new Intent(getApplication(),SubActivity.class);
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonth(): String {
        val date: LocalDate = LocalDate.now()
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

    private fun getSpentMoneyText(): SpannedString {
        return buildSpannedString {
            color(Color.RED) {
                append("2,980円\n")
            }
            color(Color.BLUE) {
                append("100,000円\n")
            }
            append("+97,020円")
        }
    }
}