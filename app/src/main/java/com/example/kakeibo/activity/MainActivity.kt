package com.example.kakeibo.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.kakeibo.R
import com.example.kakeibo.fragment.AddDataFragment
import com.example.kakeibo.fragment.MainFragment
import java.util.*


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)

        // 消せる部分がある (同じ処理をしている) 気がするので確認
        val fragment = MainFragment.newInstance(0, 0)
        val args = Bundle()
        args.putInt("year", 0)
        args.putInt("month", 0)
        fragment.arguments = args

        // val fragment = AddDataFragment.newInstance(null)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commit()
    }
}