package com.example.kakeibo

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var dateEdit = findViewById<EditText>(R.id.date)
        dateEdit.setOnClickListener() {
            showDatePickerDialog(dateEdit)
        }
    }

    /* 日付ピッカーダイアログを開くためのメソッド */
    fun showDatePickerDialog(dateEdit: EditText) {
        val calendar: Calendar = Calendar.getInstance()

        //日付ピッカーダイアログを生成および設定
        DatePickerDialog(
            this,
            //ダイアログのクリックイベント設定
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val currentDate =
                    Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }

                //選択された日付でEditTextに設定
                dateEdit.setText(year.toString() + "-" + (monthOfYear+1).toString() + "-" + dayOfMonth.toString())
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
        }.show()
    }
}