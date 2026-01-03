package com.example.simpletodo.Database

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    private const val TAG = "TodoDatabase"

    // バージョン1から2へのマイグレーション
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            Log.i(TAG, "マイグレーション実行: バージョン1→2 (Todo テーブルに date カラムを追加)")
            db.execSQL("ALTER TABLE Todo ADD COLUMN date INTEGER")
            Log.d(TAG, "マイグレーション1→2: 完了")
        }
    }

    // バージョン2から3へのマイグレーション（将来の拡張用）
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            Log.i(TAG, "マイグレーション実行: バージョン2→3")
            db.execSQL("ALTER TABLE Todo ADD COLUMN priority INTEGER DEFAULT 1")
            Log.d(TAG, "マイグレーション2→3: 完了 (スキーマ変更なし、将来の拡張用)")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            Log.i(TAG, "マイグレーション実行: バージョン3→4")
            // 将来的なスキーマ変更のためのプレースホルダー
            // 現在は実際の変更はないが、バージョンを上げて将来の拡張に備える
            Log.d(TAG, "マイグレーション3→4: 完了 (スキーマ変更なし、将来の拡張用)")
        }
    }

    // 全てのマイグレーションパスを配列にまとめる
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )
}