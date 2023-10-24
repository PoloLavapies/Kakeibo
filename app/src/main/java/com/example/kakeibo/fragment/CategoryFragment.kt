package com.example.kakeibo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kakeibo.R
import com.example.kakeibo.adapter.CategoryRecyclerViewAdapter
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
        val incomeRecyclerView: RecyclerView = view.findViewById(R.id.incomeRecyclerView)
        incomeRecyclerView.adapter =
            CategoryRecyclerViewAdapter(context, this, vm.incomeCategoryList)
        incomeRecyclerView.layoutManager = LinearLayoutManager(view.context)

        // 収入カテゴリーの追加ボタン押下時の処理
        val incomeCategoryAddButton = view.findViewById<Button>(R.id.incomeCategoryAddButton)
        incomeCategoryAddButton.setOnClickListener {
            val editText: EditText = view.findViewById(R.id.incomeCategoryAdd)
            val incomeCategoryToAdd = editText.text.toString()
            // TODO suspendにする
            vm.addCategory(false, incomeCategoryToAdd)
            // TODO 以下の処理はViewModelで行う方が適切かもしれない
            (incomeRecyclerView.adapter as CategoryRecyclerViewAdapter).notifyItemChanged(
                vm.incomeCategoryList.size - 1
            )
            editText.editableText.clear()
        }

        // 支出カテゴリーの一覧表示
        val spendingRecyclerView: RecyclerView = view.findViewById(R.id.spendingRecyclerView)
        spendingRecyclerView.adapter =
            CategoryRecyclerViewAdapter(context, this, vm.spendingCategoryList)
        spendingRecyclerView.layoutManager = LinearLayoutManager(view.context)

        // 収入カテゴリーの追加ボタン押下時の処理
        val spendingCategoryAddButton = view.findViewById<Button>(R.id.spendingCategoryAddButton)
        spendingCategoryAddButton.setOnClickListener {
            val editText: EditText = view.findViewById(R.id.spendingCategoryAdd)
            val spendingCategoryToAdd = editText.text.toString()
            vm.addCategory(true, spendingCategoryToAdd)
            (spendingRecyclerView.adapter as CategoryRecyclerViewAdapter).notifyItemChanged(
                vm.spendingCategoryList.size - 1
            )
            editText.editableText.clear()
        }
    }

    suspend fun deleteCategory(id: Int) {
        vm.deleteCategory(id)
    }
}