package com.example.groceryapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class BaseApplication : Application() {

    companion object{
        const val CHANNEL_ID = "notifikasi"
    }

    override fun onCreate() {
        super.onCreate()
        createNofitikasionChannel()
    }

    private fun createNofitikasionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Channel Nofitication",
                    NotificationManager.IMPORTANCE_HIGH
                )
            channel.description = "Ini adalah nofitikasi"
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}