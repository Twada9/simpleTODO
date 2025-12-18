package com.example.simpletodo.Database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseCallbacks {
    val TAG = "TodoDatabase"
    val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.i(TAG, "データベース作成: 新しいデータベースが作成されました")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d(TAG, "データベースオープン: データベースが開かれました")
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            Log.w(TAG, "破壊的マイグレーション: データベースが再作成されました")
        }
    }
}