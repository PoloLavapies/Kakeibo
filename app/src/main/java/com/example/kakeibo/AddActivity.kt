package com.example.kakeibo

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
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
}