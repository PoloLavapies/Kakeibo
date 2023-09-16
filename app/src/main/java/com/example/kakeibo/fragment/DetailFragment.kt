package com.example.kakeibo.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.adapter.DetailViewAdapter
import com.example.kakeibo.database.KakeiboDatabase
import com.example.kakeibo.databinding.FragmentDetailBinding
import com.example.kakeibo.entity.Spending
import com.example.kakeibo.viewmodel.AddDataViewModel
import com.example.kakeibo.viewmodel.DetailViewModel
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    private val vm: DetailViewModel by viewModels{
        DetailViewModel.Factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm.date = args.date
        vm.init()

        val binding: FragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.vm = vm
        val view: View = binding.root

        val listView: ListView = view.findViewById(R.id.detail_list)
        listView.adapter = DetailViewAdapter(
            requireContext(),
            this,
            vm.spendingMapList,
            R.layout.fragment_detail_row,
            arrayOf("money", "detail"),
            intArrayOf(R.id.money, R.id.detail)
        )

        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailToAddData(vm.date)
            findNavController().navigate(action)
        }

        return view
    }

    fun deleteSpendingData(id: Int) {
        // TODO ViewModel経由で削除するのは微妙な気もする
        vm.deleteSpendingData(id)
    }
}