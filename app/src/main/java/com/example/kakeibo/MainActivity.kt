package com.example.kakeibo

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannedString
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
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

        // TODO 元に戻す
        for (i in 0 until 1) {
        //for (i in 0 until dateList.size / 7) {
            for (j in 0..6) {
                val textViewId = resources.getIdentifier("date${i}_${j}", "id", packageName)
                val date: TextView = findViewById(textViewId)
                date.text = dateList.get(i * 7 + j).toString()
            }
        }

        // TODO 元に戻す
        for (i in 0 until 1) {
            //for (i in 0 until dateList.size / 7) {
            for (j in 0..6) {
                val buttonId = resources.getIdentifier("button${i}_${j}", "id", packageName)
                val button: Button = findViewById(buttonId)
                button.text = "10050"
            }
        }
    }

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

    /*private fun getSpentMoneyText(): SpannedString {
        val sampleLabel = "Sample Text"
        val colorRange = sampleLabel.rangeOfIndex("Text")
        return buildSpannedString {
            append(sampleLabel.subSequence(0, colorRange.first))
            color(Color.RED) {
                append(sampleLabel.subSequence(colorRange))
            }
        }
    }*/
}