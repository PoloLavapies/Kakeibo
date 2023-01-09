package com.example.kakeibo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.example.kakeibo.R

class DetailViewAdapter : SimpleAdapter {
    private var inflater: LayoutInflater //= null
    private var list: MutableList<MutableMap<String, Any>> //= mutableListOf()

    constructor(context: Context, list: MutableList<MutableMap<String, Any>>, layout :Int, from :Array<String>, to :IntArray)
            : super(context, list, layout, from, to) {
        this.inflater = LayoutInflater.from(context)
        this.list = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View = inflater.inflate(R.layout.activity_detail_row, parent, false)

        // 金額
        val moneyView = view.findViewById<TextView>(R.id.money)
        moneyView.text = list[position]["money"].toString()

        // 詳細
        val detailView = view.findViewById<TextView>(R.id.detail)
        detailView.text = list[position]["detail"].toString()

        // 削除ボタン
        val button = view.findViewById<TextView>(R.id.delete_button)
        button. setOnClickListener() {
            val spendingId: Int = list[position]["spendingId"] as Int
            // TODO 削除する処理
        }

        return view
    }
}
