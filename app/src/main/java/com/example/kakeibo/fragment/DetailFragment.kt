package com.example.kakeibo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kakeibo.R
import com.example.kakeibo.adapter.DetailViewAdapter
import com.example.kakeibo.databinding.FragmentDetailBinding
import com.example.kakeibo.viewmodel.DetailViewModel

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    private val vm: DetailViewModel by viewModels {
        DetailViewModel.Factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.vm = vm
        val view: View = binding.root

        vm.year = args.year
        vm.month = args.month
        vm.day = args.day
        vm.init()

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
            val action = DetailFragmentDirections.actionDetailToAddData(vm.year, vm.month, vm.day)
            findNavController().navigate(action)
        }

        return view
    }

    suspend fun deleteSpendingData(id: Int) {
        vm.deleteSpendingData(id)
    }
}