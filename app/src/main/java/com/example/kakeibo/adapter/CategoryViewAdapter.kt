package com.example.kakeibo.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.example.kakeibo.R
import com.example.kakeibo.fragment.CategoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewAdapter : SimpleAdapter {
    private var context: Context? = null
    private var fragment: CategoryFragment? = null
    private var inflater: LayoutInflater
    private var list: MutableList<MutableMap<String, Any>>

    constructor(
        context: Context,
        fragment: CategoryFragment,
        list: MutableList<MutableMap<String, Any>>,
        layout: Int,
        from: Array<String>,
        to: IntArray
    )
            : super(context, list, layout, from, to) {
        this.context = context
        this.fragment = fragment
        this.inflater = LayoutInflater.from(context)
        this.list = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View = inflater.inflate(R.layout.fragment_category_row, parent, false)

        // 分類名
        val categoryNameView = view.findViewById<TextView>(R.id.category_name)
        categoryNameView.text = list[position]["name"] as String

        // 削除ボタン
        val button = view.findViewById<TextView>(R.id.delete_button)
        button.setOnClickListener {
            val categoryId: Int = list[position]["id"] as Int

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("確認")
                .setMessage("選択された分類を削除します。")
                .setPositiveButton("OK") { _, _ ->
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            fragment?.deleteCategory(categoryId)
                        }
                    }
                    list.removeAt(position)
                    this.notifyDataSetChanged()
                }
                .setNegativeButton("キャンセル", null)
            builder.create().show()
        }

        return view
    }
}
