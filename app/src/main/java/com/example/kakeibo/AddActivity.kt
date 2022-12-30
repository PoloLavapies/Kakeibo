package com.example.kakeibo

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var dateEdit = findViewById<EditText>(R.id.date)
        dateEdit.inputType = InputType.TYPE_NULL
        dateEdit.setText(getDate())
        dateEdit.setOnClickListener() {
            showDatePickerDialog(dateEdit)
        }

        // 分類ボタン押下時の処理
        val spendingButton = findViewById<TextView>(R.id.button_spending)
        val incomeButton = findViewById<TextView>(R.id.button_income)
        incomeButton.setOnClickListener {
            spendingButton.setTextColor(resources.getColor(R.color.purple_500))
            spendingButton.background = resources.getDrawable(R.drawable.income_button)
            incomeButton.setTextColor(resources.getColor(R.color.white));
            incomeButton.background = resources.getDrawable(R.drawable.spending_button)
        }
        spendingButton.setOnClickListener {
            incomeButton.setTextColor(resources.getColor(R.color.purple_500))
            incomeButton.background = resources.getDrawable(R.drawable.income_button)
            spendingButton.setTextColor(resources.getColor(R.color.white));
            spendingButton.background = resources.getDrawable(R.drawable.spending_button)
        }

        // 追加ボタン押下時の処理
        val addButton = findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            addData()
        }
    }

    /* 日付ピッカーダイアログを開くためのメソッド */
    fun showDatePickerDialog(dateEdit: EditText) {
        val selectedDate: LocalDate = LocalDate.parse(
            findViewById<EditText>(R.id.date).text.toString(),
            DateTimeFormatter.ISO_DATE)

        //日付ピッカーダイアログを生成および設定
        DatePickerDialog(
            this,
            //ダイアログのクリックイベント設定
            { _, year, month, day ->
                dateEdit.setText(year.toString() + "-" + (month + 1).toString() + "-" + day.toString())
            },
            // ダイアログを開いたときに選択されている日付 (月のみ0オリジン)
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).apply {
        }.show()
    }

    private fun getDate(): String {
        val date: LocalDate = LocalDate.now()
        return date.format(DateTimeFormatter.ISO_DATE)
    }

    private fun addData() {
        val money: Int = findViewById<EditText>(R.id.money).text.toString().toInt()
        val dateStr = findViewById<EditText>(R.id.date).text.toString()
        val detail = findViewById<EditText>(R.id.detail).text.toString()
        val spending = Spending(0, 1, money, dateStr, detail)
        println("日付:${dateStr} 金額:${money} 詳細:${detail}")

        val db = KakeiboDatabase.getInstance(this)
        db.spendingDao().insert(spending)

        finish()
    }
}