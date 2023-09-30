package com.example.kakeibo.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.example.kakeibo.R
import com.example.kakeibo.fragment.DetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewAdapter : SimpleAdapter {
    private var context: Context? = null
    private var fragment: DetailFragment? = null
    private var inflater: LayoutInflater
    private var list: MutableList<MutableMap<String, Any>>

    constructor(
        context: Context,
        fragment: DetailFragment,
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
        var view: View = inflater.inflate(R.layout.fragment_detail_row, parent, false)

        // 金額
        val moneyView = view.findViewById<TextView>(R.id.money)
        moneyView.text = list[position]["money"] as SpannedString

        // 詳細
        val detailView = view.findViewById<TextView>(R.id.detail)
        detailView.text = list[position]["detail"].toString()

        // 削除ボタン
        val button = view.findViewById<TextView>(R.id.delete_button)
        button.setOnClickListener {
            val spendingId: Int = list[position]["spendingId"] as Int

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("確認")
                .setMessage("選択されたデータを削除します。")
                .setPositiveButton("OK") { dialog, id ->
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            fragment?.deleteSpendingData(spendingId)
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
