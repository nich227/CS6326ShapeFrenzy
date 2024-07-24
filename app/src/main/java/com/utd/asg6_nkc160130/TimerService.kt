package com.utd.asg6_nkc160130

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

/**
 * Shapes Frenzy App.
 *
 * Background service that contains a timer and will send an intent broadcast to the Game Activity
 * every second with the timer value.
 *
 * Assignment 6
 * File:            TimerService.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class TimerService : Service() {
    private var intent: Intent = Intent("com.utd.asg6_nkc160130.GameActivity")

    private var handler = Handler(Looper.getMainLooper())
    private var updateUI = UpdateUI()
    private var initialTime: Long = 0
    var curTimerValue: Long = 0

    /**
     * Sends a broadcast to the Game Activity with the current timer value.
     */
    inner class UpdateUI : Runnable {
        override fun run() {
            curTimerValue = System.currentTimeMillis() - initialTime
            intent.putExtra("timer", curTimerValue)
            sendBroadcast(intent)
            handler.postDelayed(this, 1000)
        }
    }

    /**
     * Initializes the service and the Handler for the Timer Runnable.
     */
    override fun onCreate() {
        super.onCreate()
        initialTime = System.currentTimeMillis()
        handler.postDelayed(updateUI, 1000)
    }

    /**
     * Stops the handler when the Service is stopped.
     */
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateUI)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}