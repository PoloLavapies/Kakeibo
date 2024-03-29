package com.example.kakeibo.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.databinding.FragmentAddDataBinding
import com.example.kakeibo.viewmodel.AddDataViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddDataFragment : Fragment() {
    private val args: AddDataFragmentArgs by navArgs()
    private val vm: AddDataViewModel by viewModels {
        AddDataViewModel.Factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_data, container, false)
        binding.vm = vm
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 日付
        vm.year = args.year
        vm.month = args.month
        vm.day = args.day

        // 日付クリック時の処理
        val dateEdit = view.findViewById<EditText>(R.id.date)
        dateEdit.setOnClickListener {
            showDatePickerDialog(dateEdit, view)
        }

        // プルダウンの実装 (分類)
        val adapterSpending: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterSpending.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapterIncome: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        adapterIncome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        for (category in vm.categoryNameListSpending) {
            adapterSpending.add(category)
        }
        for (category in vm.categoryNameListIncome) {
            adapterIncome.add(category)
        }

        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        spinner.adapter = adapterSpending

        // 分類ボタン押下時の処理
        val spendingButton = view.findViewById<TextView>(R.id.button_spending)
        val incomeButton = view.findViewById<TextView>(R.id.button_income)

        // TODO XMLを修正し、vm.isSpendingShownが変更するだけで色を切り替えるようにしたい
        //  ただし、以下のようなコードは機能しなかった
        //  (isSpendingShownの初期値によっては切り替わったが、値を変更しても見た目が変わらなかった)
        //  android:textColor="{vm.isSpendingsShown() ? @color/white : @color/purple_500}"
        incomeButton.setOnClickListener {
            vm.changeCategoryListToIncome()
            spinner.adapter = adapterIncome

            spendingButton.setTextColor(resources.getColor(R.color.purple_500))
            spendingButton.background = resources.getDrawable(R.drawable.income_button)
            incomeButton.setTextColor(resources.getColor(R.color.white));
            incomeButton.background = resources.getDrawable(R.drawable.spending_button)
        }
        spendingButton.setOnClickListener {
            vm.changeCategoryListToSpending()
            spinner.adapter = adapterSpending

            incomeButton.setTextColor(resources.getColor(R.color.purple_500))
            incomeButton.background = resources.getDrawable(R.drawable.income_button)
            spendingButton.setTextColor(resources.getColor(R.color.white));
            spendingButton.background = resources.getDrawable(R.drawable.spending_button)
        }

        // 追加ボタン押下時の処理
        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val categoryName =
                view.findViewById<Spinner>(R.id.category_spinner).selectedItem.toString()
            val money = view.findViewById<EditText>(R.id.money).text.toString().toInt()
            val dateStr = view.findViewById<EditText>(R.id.date).text.toString()
            val detail = view.findViewById<EditText>(R.id.detail).text.toString()
            // TODO ここでDBViewModelのisProcessingをtrueにする
            vm.addData(categoryName, money, dateStr, detail)
            findNavController().popBackStack()
            // TODO ここでDBViewModelのisProcessingをfalseにする
            //  → Detailでobserveしておき、falseに変更された場合に初期化を再実行する
        }
    }


    /* 日付ピッカーダイアログを開くためのメソッド */
    fun showDatePickerDialog(dateEdit: EditText, view: View) {
        val selectedDate: LocalDate = LocalDate.parse(
            view.findViewById<EditText>(R.id.date).text.toString(),
            DateTimeFormatter.ISO_DATE
        )

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
}