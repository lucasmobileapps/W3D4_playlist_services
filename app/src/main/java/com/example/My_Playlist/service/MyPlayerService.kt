package com.example.My_Playlist.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.example.My_Playlist.R

class MyPlayerService : Service(){
    lateinit var mediaPlayer: MediaPlayer
    private val myBinder = MyPlayerBinder()
    lateinit var notificationManager: NotificationManager
    var  audioPlaying : Int = R.raw.keyboard

    override fun onBind(intent: Intent): IBinder {

        playPlayer()
        return myBinder
    }

    private fun createNotification(): Notification {
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "my_channel",
                    "my player channel",
                    NotificationManager.IMPORTANCE_LOW
                )

            notificationManager.createNotificationChannel(notificationChannel)

            notification = Notification.Builder(this, "my_channel")
                .setSmallIcon(R.drawable.ic_play)
                .setTicker("My Sound is Playing")
                .setOngoing(true)
                .build()

        } else {
            notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker("My Sound is Playing")
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .build()
        }

        return notification
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mediaPlayer = MediaPlayer.create(this, audioPlaying)
        mediaPlayer.isLooping = false
        mediaPlayer.start()


        notificationManager.notify(1, createNotification())

    }

    fun playTracks(track : Int){
       // if (track != audioPlaying)
        mediaPlayer.stop()
        mediaPlayer.reset()
        audioPlaying = track



    }

    fun playPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer = MediaPlayer.create(this, audioPlaying)
            mediaPlayer.isLooping = false
            mediaPlayer.start()
            sendBroadcast(Intent("com.my.sender").apply {
                this.putExtra("my_key", "PLAYING")
            })
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        playTracks(audioPlaying)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("com.my.sender").apply {
            this.putExtra("my_key", "STOPPED")
        })
        mediaPlayer.stop()
    }

    fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.reset()
            sendBroadcast(Intent("com.my.sender").apply {
                this.putExtra("my_key", "PAUSED")
            })
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }
    inner class MyPlayerBinder : Binder() {
        fun getPlayerService(): MyPlayerService {
            return this@MyPlayerService
        }
    }

}