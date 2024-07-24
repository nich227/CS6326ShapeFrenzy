package com.utd.asg6_nkc160130

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

/**
 * Shapes Frenzy Game.
 *
 * This is the "splash screen" of the game that shows the logo, has a play button, and has an option to show
 * game instructions to the player. The game is a simple Shapes game, as the name suggests. The game consists of
 * 12 shapes, each with different colors and is either a Square or a Circle. If a shape is touched, the shape will
 * automatically disappear and a new shape will be drawn randomly. Each shape has a lifetime between 3 and 7 seconds,
 * after which the shape will disappear and a new shape will be drawn randomly. If the shape touched is a blue circle,
 * this shape's reward will be added to the player's score. If 10 of these shapes are touched,
 * the game ends and the user is taken to the High Scores screen, where the user can view past player's high scores
 * and can enter his or her own score into the list.
 * For more details about how the game works behind the hood, please refer to the other class files.
 *
 * Assignment 6
 * File:            MainActivity.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class MainActivity : AppCompatActivity() {
    inner class InstructionsButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.instructions_popup, null)

            val wh = ConstraintLayout.LayoutParams.WRAP_CONTENT
            val popupWindow = PopupWindow(popupView, wh, wh, false)
            popupWindow.setBackgroundDrawable(getDrawable(android.R.drawable.picture_frame))
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            popupView.findViewById<MaterialButton>(R.id.instructionsOK)
                .setOnClickListener(GotItButtonClickListener(popupWindow))
        }
    }

    inner class GotItButtonClickListener(private val popupWindow: PopupWindow) :
        View.OnClickListener {
        override fun onClick(view: View) {
            popupWindow.dismiss()
        }
    }

    inner class PlayButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val playGame = Intent(applicationContext, GameActivity::class.java)
            startActivity(playGame)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.instructionsButton).setOnClickListener(
            InstructionsButtonClickListener()
        )
        findViewById<MaterialButton>(R.id.startButton).setOnClickListener(PlayButtonClickListener())
    }
}