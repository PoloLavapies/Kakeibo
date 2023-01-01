package com.example.kakeibo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kakeibo.dao.CategoryDao
import com.example.kakeibo.dao.SpendingDao
import com.example.kakeibo.entity.Category
import com.example.kakeibo.entity.Spending

@Database(entities = [Category::class, Spending::class], version = 1, exportSchema = false)
abstract class KakeiboDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun spendingDao(): SpendingDao

    companion object {
        private var INSTANCE: KakeiboDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): KakeiboDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        KakeiboDatabase::class.java, "Kakeibo.db")
                        // TODO 調査
                        .allowMainThreadQueries()
                        .addCallback(object : RoomDatabase.Callback() {
                            // TODO ほかの書き方も調査
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                val sql = "INSERT INTO 'category' VALUES " +
                                        "(null, 0, '給料')," +
                                        "(null, 1, 'ごはん')," +
                                        "(null, 1, 'おやつ');"
                                        db.execSQL(sql)
                            }
                        })
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}