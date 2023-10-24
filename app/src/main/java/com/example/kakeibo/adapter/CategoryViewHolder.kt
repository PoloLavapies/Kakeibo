package com.example.kakeibo.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kakeibo.R

class CategoryViewHolder(categoryView: View) : RecyclerView.ViewHolder(categoryView) {
    val categoryName: TextView = categoryView.findViewById(R.id.category_name)
    val deleteButton: Button = categoryView.findViewById(R.id.delete_button)
}