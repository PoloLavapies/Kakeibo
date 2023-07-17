package com.example.kakeibo.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.entity.Spending
import com.example.kakeibo.viewmodel.AddDataViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddDataFragment : Fragment() {
    private val args: AddDataFragmentArgs by navArgs()
    private var date: String = ""
    private val viewModel: AddDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        date = args.date

        val view: View = inflater.inflate(R.layout.fragment_add_data, container, false)

        // 日付
        val dateEdit = view.findViewById<EditText>(R.id.date)
        if (date.isNotEmpty()) {
            dateEdit.setText(date)
        } else {
            dateEdit.setText(getDate())
        }
        dateEdit.setOnClickListener() {
            showDatePickerDialog(dateEdit, view)
        }

        // プルダウンの実装 (分類)
        // TODO adapterに関するロジックを別クラスに切り出したい
        val adapterSpending: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterSpending.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapterIncome: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterIncome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val db = KakeiboDatabase.getInstance(requireContext())
        val categories = db.categoryDao().getAll()
        for (category in categories) {
            if (category.isSpending) {
                adapterSpending.add(category.name)
                viewModel.categoryIdMapSpending[category.name] = category.id
            } else {
                adapterIncome.add(category.name)
                viewModel.categoryIdMapIncome[category.name] = category.id
            }
        }

        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        spinner.adapter = adapterSpending
        viewModel.isSpendingsShwon = true

        // 分類ボタン押下時の処理
        val spendingButton = view.findViewById<TextView>(R.id.button_spending)
        val incomeButton = view.findViewById<TextView>(R.id.button_income)
        incomeButton.setOnClickListener {
            spendingButton.setTextColor(resources.getColor(R.color.purple_500))
            spendingButton.background = resources.getDrawable(R.drawable.income_button)
            incomeButton.setTextColor(resources.getColor(R.color.white));
            incomeButton.background = resources.getDrawable(R.drawable.spending_button)
            spinner.adapter = adapterIncome
            viewModel.isSpendingsShwon = false
        }
        spendingButton.setOnClickListener {
            incomeButton.setTextColor(resources.getColor(R.color.purple_500))
            incomeButton.background = resources.getDrawable(R.drawable.income_button)
            spendingButton.setTextColor(resources.getColor(R.color.white));
            spendingButton.background = resources.getDrawable(R.drawable.spending_button)
            spinner.adapter = adapterSpending
            viewModel.isSpendingsShwon = true
        }

        // 追加ボタン押下時の処理
        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            addData(view)
            findNavController().popBackStack()
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

        val categoryId: Int = if (viewModel.isSpendingsShwon) {
            viewModel.categoryIdMapSpending[categoryName]!!
        } else {
            viewModel.categoryIdMapIncome[categoryName]!!
        }
        val money: Int = view.findViewById<EditText>(R.id.money).text.toString().toInt()
        val dateStr = view.findViewById<EditText>(R.id.date).text.toString()
        val detail = view.findViewById<EditText>(R.id.detail).text.toString()
        val spending = Spending(0, categoryId, money, dateStr, detail)

        val db = KakeiboDatabase.getInstance(requireContext())
        db.spendingDao().insert(spending)
    }
}