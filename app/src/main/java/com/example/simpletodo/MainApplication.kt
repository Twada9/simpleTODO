package com.example.simpletodo

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simpletodo.Database.DatabaseCallbacks
import com.example.simpletodo.Database.DatabaseMigrations
import com.example.simpletodo.Database.TodoDatabase
import com.example.simpletodo.ViewModel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(modules)
        }
    }

    val modules: Module = module {
        single {
            Room.databaseBuilder(
                androidContext(),
                TodoDatabase::class.java,
                "todoDatabase"
            )
                .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                .fallbackToDestructiveMigration()
                .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                .addCallback(DatabaseCallbacks.callback)
                .build()
        }
        single { get<TodoDatabase>().todoDao() }
        viewModel { MainViewModel(get()) }
    }

}