package com.utd.asg6_nkc160130

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

/**
 * Shapes Frenzy App.
 *
 * The Game Activity is the game part of the app. There are two cards for display statistics: one for time
 * since the game started, and one for the total score. The rest of the screen is occupied by a custom View
 * (ShapesView) that contains an assortment of shapes, which will be updated every second.
 * For more information about how the shapes are handled and how the positions are updated every second,
 * please refer to the TimerService.kt and ShapesView.kt files.
 * Pressing the system back button will allow you to quit the game and return to the main screen.
 *
 * Assignment 6
 * File:            GameActivity.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class GameActivity : AppCompatActivity() {
    private val timerUpdateReceiver = TimerUpdateReceiver()
    private val timer = Calendar.getInstance()
    private val startTime = System.currentTimeMillis()
    private var timerIntent: Intent = Intent()
    private val intentFilter: IntentFilter = IntentFilter("com.utd.asg6_nkc160130.GameActivity")
    private var score: Long = 0
    private var numCirclesTouched: Int = 0
    var numCirclesMissed: Int = 0

    /**
     * Handles when the system's back button (<-) is pressed, when the user wants to quit the game.
     * Shows a popup message that confirms the user's choice.
     */
    inner class SystemBackPressed : OnBackPressedCallback(true), PopupWindow.OnDismissListener {
        var popupWindow: PopupWindow? = null

        override fun handleOnBackPressed() {
            // Popup Window hasn't been created, or needs to be recreated
            if (popupWindow == null) {
                // Show the Popup Window to confirm that the user wants to quit the game
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.game_back_confirm_popup, null)

                val wh = ConstraintLayout.LayoutParams.WRAP_CONTENT
                popupWindow = PopupWindow(popupView, wh, wh, false)
                popupWindow!!.setBackgroundDrawable(getDrawable(android.R.drawable.picture_frame))
                popupWindow!!.showAtLocation(
                    findViewById(R.id.gameActivityConstraintLayout),
                    Gravity.CENTER, 0, 0
                )

                // Set the Yes and No button listeners, and dismiss listener for PopupWindow
                popupWindow!!.setOnDismissListener(this)
                popupView.findViewById<MaterialButton>(R.id.gameBackYes)
                    .setOnClickListener(YesClickListener())
                popupView.findViewById<MaterialButton>(R.id.gameBackNo)
                    .setOnClickListener(NoClickListener(popupWindow!!))
            }
        }

        override fun onDismiss() {
            // When the popup window is dismissed, it needs to be recreated
            popupWindow = null
        }

        /**
         * Exits the game when the user presses "Yes"
         */
        inner class YesClickListener : View.OnClickListener {
            override fun onClick(view: View) {
                stopPlaying()
            }
        }

        /**
         * Dismisses the Popup Window when the user presses "No".
         */
        inner class NoClickListener(private val popupWindow: PopupWindow) : View.OnClickListener {
            override fun onClick(view: View) {
                popupWindow.dismiss()
            }
        }
    }

    /**
     * Receives the current timer value, and updates the shapes' positions.
     */
    inner class TimerUpdateReceiver : BroadcastReceiver() {
        private val timeFormat = SimpleDateFormat("mm:ss", Locale.ENGLISH)

        override fun onReceive(context: Context, intent: Intent) {
            // Update timer UI
            timer.timeInMillis = intent.getLongExtra("timer", 0)
            findViewById<TextView>(R.id.timerCardText).text = timeFormat.format(timer.time)

            // Update positions of shapes
            val shapesView = findViewById<ShapesView>(R.id.shapesArea)
            for(shape in shapesView.shapes)
                shape.movePosition(shapesView.shapes.minus(shape), shapesView.maxX, shapesView.maxY)

            // Check to see if there are any shapes that need to be removed
            shapesView.checkRemoveShapes()

            // Redraw the entire view
            shapesView.invalidate()

        }
    }

    /**
     * Acts as a listener for the variable "score"; updates the score,
     * updates the UI with the new score, and update the number of target shapes touched.
     * @param addScore the score to be added to the variable "score".
     */
    fun updateScore(addScore: Long) {
        score += addScore
        if(score < 0)
            score = 0
        findViewById<TextView>(R.id.scoreCardText).text = score.toString()

        // Update the number of circles touched, if the score has increased or stayed the same
        if(addScore >= 0)
            updateCirclesTouched()
    }

    /**
     * Updates the number of target shapes touched; if 10 target shapes are touched,
     * this method will end the game and handle passing game metrics to the High Score Activity.
     */
    private fun updateCirclesTouched() {
        // Increments the number of target shapes touched
        numCirclesTouched++

        // 10 shapes touched, game over
        if(numCirclesTouched == 10) {
            // Notify the user that the game is over
            val gameFinished = Toast.makeText(
                this@GameActivity,
                resources.getString(R.string.game_finished_toast),
                Toast.LENGTH_LONG
            )
            gameFinished.show()

            // Launches the High Score Activity with an intent
            val intentHighScore = Intent(this, HighScoreActivity::class.java)
            intentHighScore.putExtra("score", score)
            intentHighScore.putExtra("timeInMillis", System.currentTimeMillis() - startTime)
            intentHighScore.putExtra("numCirclesMissed", numCirclesMissed)
            intentHighScore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Stop the timer
            unregisterReceiver(timerUpdateReceiver)
            stopService(timerIntent)

            // Start the High Score Activity
            startActivityForResult(intentHighScore, 0)
        }
    }

    /**
     * Called when the Activity is starting. Starts the Timer Service, adds a listener for when the back button
     * is pressed, and sets the height of the custom View, where the circles will be to 2/3 of the screen height.
     *
     * @param savedInstanceState unused parameter.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Handle system back button pressed
        onBackPressedDispatcher.addCallback(SystemBackPressed())

        // Start background timer
        timerIntent = Intent(this@GameActivity, TimerService::class.java)
        startService(timerIntent)
        registerReceiver(timerUpdateReceiver, intentFilter)

        // Enlarge game area to 2/3 of screen height
        val shapesAreaView = findViewById<ShapesView>(R.id.shapesArea)
        val shapesAreaParams = shapesAreaView.layoutParams
        shapesAreaParams.height =
            (Resources.getSystem().displayMetrics.heightPixels * 2.0 / 3.0).roundToInt()
        shapesAreaView.layoutParams = shapesAreaParams
    }

    /**
     * When the app is returning from the High Score Activity, return back to the Main Activity.
     *
     * @param requestCode unused parameter.
     * @param resultCode unused parameter.
     * @param data unused parameter.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stopPlaying()
    }

    /**
     * Stops the game, notifies the user that the game has ended, stops the timer service and
     * returns to the Main Activity.
     */
    fun stopPlaying() {
        // Show thanks for playing toast
        val playingThanks = Toast.makeText(
            this@GameActivity,
            resources.getString(R.string.thanks_playing_toast),
            Toast.LENGTH_LONG
        )
        playingThanks.show()

        // Stop the timer
        unregisterReceiver(timerUpdateReceiver)
        stopService(timerIntent)

        finish()
    }

}