package com.example.maniasorvete

import android.app.Application
import com.example.maniasorvete.model.AppDatabase

class App : Application() {
    val db: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
