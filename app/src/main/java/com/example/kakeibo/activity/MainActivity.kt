package com.example.kakeibo.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.kakeibo.R
import java.util.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(R.navigation.navigation_graph)

        val appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            .setOpenableLayout(findViewById<View>(R.id.drawer_layout) as DrawerLayout)
            .build()

        NavigationUI.setupWithNavController(toolBar, navController, appBarConfiguration)
    }
}