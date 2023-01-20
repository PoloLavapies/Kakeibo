package com.example.kakeibo.adapter

import android.app.AlertDialog
import android.content.DialogInterface
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

            val builder: AlertDialog.Builder = AlertDialog.Builder(this.activity)
            builder.setTitle("確認")
                .setMessage("選択されたデータを削除します。")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    this.activity?.deleteSpendingData(spendingId)
                    list.removeAt(position)
                    this.notifyDataSetChanged()
                })
                .setNegativeButton("キャンセル", null)
            builder.create().show()
        }

        return view
    }
}
