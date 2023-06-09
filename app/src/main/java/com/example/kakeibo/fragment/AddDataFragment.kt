package com.example.kakeibo.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kakeibo.R
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddDataFragment : Fragment() {
    private var date: String? = null
    // 分類名とIDの対応表 選択されたカテゴリ名からIDを取得するために使用する
    private val categoryIdMapSpending = mutableMapOf<String, Int>()
    private val categoryIdMapIncome = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString("date")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val view: View = inflater.inflate(R.layout.fragment_add_data, container, false)

        // 日付
        val dateEdit = view.findViewById<EditText>(R.id.date)
        if (date != null) {
            dateEdit.setText(date)
        } else {
            dateEdit.setText(getDate())
        }
        dateEdit.setOnClickListener() {
            showDatePickerDialog(dateEdit, view)
        }

        // プルダウンの実装 (分類)
        val adapterSpending: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterSpending.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapterIncome: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterIncome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val db = KakeiboDatabase.getInstance(requireContext())
        val categories = db.categoryDao().getAll()
        for (category in categories) {
            if (category.isSpending) {
                adapterSpending.add(category.name)
                categoryIdMapSpending[category.name] = category.id
            } else {
                adapterIncome.add(category.name)
                categoryIdMapIncome[category.name] = category.id
            }
        }

        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        spinner.adapter = adapterSpending

        // 分類ボタン押下時の処理
        val spendingButton = view.findViewById<TextView>(R.id.button_spending)
        val incomeButton = view.findViewById<TextView>(R.id.button_income)
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
        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            addData(view)
        }

        return view
    }


    /* 日付ピッカーダイアログを開くためのメソッド */
    fun showDatePickerDialog(dateEdit: EditText, view: View) {
        val selectedDate: LocalDate = LocalDate.parse(
            view.findViewById<EditText>(R.id.date).text.toString(),
            DateTimeFormatter.ISO_DATE)

        //日付ピッカーダイアログを生成および設定
        DatePickerDialog(
            requireContext(),
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

    private fun addData(view: View) {
        val categoryName: String = view.findViewById<Spinner>(R.id.category_spinner).selectedItem.toString()

        // TODO ボタン色の判定に関して、白色の場合「-1」になるので動きはするが、安全なコードに直したい
        val categoryId: Int = if (view.findViewById<TextView>(R.id.button_spending).currentTextColor == -1) {
            categoryIdMapSpending[categoryName]!!
        } else {
            categoryIdMapIncome[categoryName]!!
        }
        val money: Int = view.findViewById<EditText>(R.id.money).text.toString().toInt()
        val dateStr = view.findViewById<EditText>(R.id.date).text.toString()
        val detail = view.findViewById<EditText>(R.id.detail).text.toString()
        val spending = Spending(0, categoryId, money, dateStr, detail)

        val db = KakeiboDatabase.getInstance(requireContext())
        db.spendingDao().insert(spending)

        // TODO 画面を閉じる処理
        parentFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(date: String?) =
            AddDataFragment().apply {
                arguments = Bundle().apply {
                    putString("date", date)
                }
            }
    }
}