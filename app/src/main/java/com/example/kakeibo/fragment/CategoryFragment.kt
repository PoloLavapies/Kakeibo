package com.example.kakeibo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kakeibo.R
import com.example.kakeibo.adapter.CategoryViewAdapter
import com.example.kakeibo.databinding.FragmentCategoryBinding
import com.example.kakeibo.viewmodel.CategoryViewModel

class CategoryFragment : Fragment() {
    private val vm: CategoryViewModel by viewModels {
        CategoryViewModel.Factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCategoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        binding.vm = vm
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.init()

        // 収入カテゴリーの一覧表示
        val incomeListView: ListView = view.findViewById(R.id.income_list)
        incomeListView.adapter = CategoryViewAdapter(
            requireContext(),
            this,
            vm.incomeCategories,
            R.layout.fragment_category_row,
            arrayOf("id", "name"),
            intArrayOf(R.id.category_name)
        )

        // TODO 以下のコードが動くようにする
        // 収入カテゴリーの追加ボタン押下時の処理
        /*val incomeCategoryAddButton = view.findViewById<Button>(R.id.income_category_add_button)
        incomeCategoryAddButton.setOnClickListener {
            val incomeCategoryToAdd =
                view.findViewById<EditText>(R.id.income_category_add).text.toString()
            vm.addCategory(false, incomeCategoryToAdd)
            vm.init()
            incomeListView.adapter = CategoryViewAdapter(
                requireContext(),
                this,
                vm.incomeCategories,
                R.layout.fragment_category_row,
                arrayOf("id", "name"),
                intArrayOf(R.id.category_name)
            )
            // 以下でエラーが発生する
            (incomeListView.adapter as CategoryViewAdapter).notifyDataSetChanged()
        }*/


        val spendingListView: ListView = view.findViewById(R.id.spending_list)
        spendingListView.adapter = CategoryViewAdapter(
            requireContext(),
            this,
            vm.spendingCategories,
            R.layout.fragment_category_row,
            arrayOf("id", "name"),
            intArrayOf(R.id.category_name)
        )

        // TODO 追加についても実装
    }

    suspend fun deleteCategory(id: Int) {
        vm.deleteCategory(id)
    }
}