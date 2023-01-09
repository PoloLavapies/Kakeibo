package com.example.kakeibo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.example.kakeibo.DetailActivity
import com.example.kakeibo.R

class DetailViewAdapter : SimpleAdapter {
    private var activity: DetailActivity? = null
    private var inflater: LayoutInflater
    private var list: MutableList<MutableMap<String, Any>>

    constructor(activity: DetailActivity, list: MutableList<MutableMap<String, Any>>, layout :Int, from :Array<String>, to :IntArray)
            : super(activity, list, layout, from, to) {
        this.activity = activity
        this.inflater = LayoutInflater.from(activity)
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
            // TODO Activity側でDialogを出すように実装
            this.activity?.deleteSpendingData(spendingId)
            // TODO 削除を行った場合と行わなかった場合で↑のメソッドが違う値を返すよう実装
            //  その値に応じて↓を実行するかを決める
            list.removeAt(position)
            this.notifyDataSetChanged()
        }

        return view
    }
}
