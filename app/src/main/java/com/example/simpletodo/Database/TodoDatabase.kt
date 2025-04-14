package com.example.simpletodo.Database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simpletodo.DataAccess.TodoDao
import com.example.simpletodo.Model.Todo

@Database(entities = [Todo::class], version = 3)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
    
    companion object {
        private const val TAG = "TodoDatabase" // ログ出力用のタグ
        @Volatile
        private var INSTANCE: TodoDatabase? = null
        
        // バージョン1から2へのマイグレーション
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.i(TAG, "マイグレーション実行: バージョン1→2 (Todo テーブルに date カラムを追加)")
                database.execSQL("ALTER TABLE Todo ADD COLUMN date INTEGER")
                Log.d(TAG, "マイグレーション1→2: 完了")
            }
        }
        
        // バージョン2から3へのマイグレーション（将来の拡張用）
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.i(TAG, "マイグレーション実行: バージョン2→3")
                // 将来的なスキーマ変更のためのプレースホルダー
                // 現在は実際の変更はないが、バージョンを上げて将来の拡張に備える
                Log.d(TAG, "マイグレーション2→3: 完了 (スキーマ変更なし、将来の拡張用)")
            }
        }
        
        // 全てのマイグレーションパスを配列にまとめる
        private val ALL_MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
        
        /**
         * データベースのシングルトンインスタンスを取得
         * @param context アプリケーションコンテキスト
         * @return TodoDatabaseのインスタンス
         */
        fun getDatabase(context: Context): TodoDatabase {
            // すでにインスタンスが存在する場合はそれを返す
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "データベースインスタンスを初期化します")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                .addMigrations(*ALL_MIGRATIONS) // 全てのマイグレーション戦略を追加
                .fallbackToDestructiveMigration() // 深刻なマイグレーションエラー時はデータベースを再作成
                .setJournalMode(JournalMode.AUTOMATIC) // パフォーマンス向上のためのジャーナルモード設定
                .addCallback(object : Callback() {
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
                })
                .build()
                INSTANCE = instance
                Log.i(TAG, "データベース初期化が完了しました")
                instance
            }
        }
        
        /**
         * データベースを初期化し、設定済みのインスタンスを返す
         * （MainViewModelからの移行用メソッド）
         * @param context アプリケーションコンテキスト
         * @return 初期化されたTodoDatabaseのインスタンス
         */
        fun initializeDatabase(context: Context): TodoDatabase {
            Log.d(TAG, "initializeDatabase: データベースの初期化を開始")
            val db = getDatabase(context)
            Log.d(TAG, "initializeDatabase: データベースの初期化が完了")
            return db
        }
    }
}