package com.example.kakeibo.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kakeibo.R
import com.example.kakeibo.entity.Category
import com.example.kakeibo.fragment.CategoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryRecyclerViewAdapter(
    val context: Context?,
    val fragment: CategoryFragment,
    val categoryList: MutableList<Category>
) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_category_row, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryName.text = categoryList[position].name
        holder.deleteButton.setOnClickListener {
            val categoryId: Int = categoryList[position].id

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("確認")
                .setMessage("選択された分類を削除します。")
                .setPositiveButton("OK") { _, _ ->
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            fragment?.deleteCategory(categoryId)
                        }
                    }

                    categoryList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, categoryList.size);
                }
                .setNegativeButton("キャンセル", null)
            builder.create().show()
        }
    }

    override fun getItemCount(): Int = categoryList.size
}
