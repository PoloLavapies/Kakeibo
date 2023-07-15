package com.example.kakeibo.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kakeibo.R
import com.example.kakeibo.fragment.AddDataFragment
import com.example.kakeibo.fragment.MainFragment
import com.example.kakeibo.fragment.MainFragmentArgs
import java.util.*


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)

        val navController = findNavController(R.id.nav_host_fragment)
        val args = MainFragmentArgs(0, 0)
        navController.setGraph(R.navigation.navigation_graph, args.toBundle())
    }
}