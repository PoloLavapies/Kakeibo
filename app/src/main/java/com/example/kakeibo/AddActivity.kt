package com.example.kakeibo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddActivity : AppCompatActivity() {
    private val categoryIdMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var dateEdit = findViewById<EditText>(R.id.date)
        dateEdit.inputType = InputType.TYPE_NULL
        dateEdit.setText(getDate())
        dateEdit.setOnClickListener() {
            showDatePickerDialog(dateEdit)
        }

        // プルダウンの実装
        val adapterSpending: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapterSpending.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapterIncome: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapterSpending.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val db = KakeiboDatabase.getInstance(this)
        val categories = db.categoryDao().getAll()
        for (category in categories) {
            if (category.isSpending) {
                adapterSpending.add(category.category)
            } else {
                adapterIncome.add(category.category)
            }
            categoryIdMap[category.category] = category.id
        }

        val spinner = findViewById<Spinner>(R.id.category_spinner)
        spinner.adapter = adapterSpending

        // 分類ボタン押下時の処理
        val spendingButton = findViewById<TextView>(R.id.button_spending)
        val incomeButton = findViewById<TextView>(R.id.button_income)
        incomeButton.setOnClickListener {
            spendingButton.setTextColor(resources.getColor(R.color.purple_500))
            spendingButton.background = resources.getDrawable(R.drawable.income_button)
            incomeButton.setTextColor(resources.getColor(R.color.white));
            incomeButton.background = resources.getDrawable(R.drawable.spending_button)
            spinner.adapter = adapterIncome
        }
        spendingButton.setOnClickListener {
            incomeButton.setTextColor(resources.getColor(R.color.purple_500))
            incomeButton.background = resources.getDrawable(R.drawable.income_button)
            spendingButton.setTextColor(resources.getColor(R.color.white));
            spendingButton.background = resources.getDrawable(R.drawable.spending_button)
            spinner.adapter = adapterSpending
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
                val date: LocalDate = LocalDate.of(year, month + 1, day)
                dateEdit.setText(date.format(DateTimeFormatter.ISO_DATE))
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
        val categoryName: String = findViewById<Spinner>(R.id.category_spinner).selectedItem.toString()

        // TODO nullが入ることはないはずなので、簡潔な記述に直せるはず
        val categoryIdPossibleNull: Int? = categoryIdMap[categoryName]
        var categoryId: Int = 0
        if (categoryIdPossibleNull != null) {
            categoryId = categoryIdPossibleNull.toInt()
        }

        val money: Int = findViewById<EditText>(R.id.money).text.toString().toInt()
        val dateStr = findViewById<EditText>(R.id.date).text.toString()
        val detail = findViewById<EditText>(R.id.detail).text.toString()
        val spending = Spending(0, categoryId, money, dateStr, detail)

        val db = KakeiboDatabase.getInstance(this)
        db.spendingDao().insert(spending)

        // TODO MainActivityの当月以外の画面や別画面から遷移した場合の処理を追加
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}