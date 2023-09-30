package com.example.kakeibo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.kakeibo.R
import com.example.kakeibo.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCategoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        val view: View = binding.root

        return view
    }
}