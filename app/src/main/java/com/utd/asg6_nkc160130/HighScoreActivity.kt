package com.utd.asg6_nkc160130

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shapes Frenzy App.
 *
 * This Activity is an interface for the user to add high scores to a high score list, and
 * to display the top 12 high scores recorded by the app.
 *
 * There are two activities to handle high scores: this activity, the High Score Activity, has a Toolbar with the app title
 * and an add button to launch the New High Score Activity from which to add a new high score to the list.
 * The rest of the activity is filled with a Recycler View that displays a list of the top 12 high scores with the player's name,
 * the player's score, and the date and time that the score was achieved.
 *
 * If a high score added to the list is too low to be included, the high score will not be added to the list and the user
 * will be notified as such. If the high score displaces another high score with a lower score from the top 12, the list will
 * be updated as such, and the user will be notified that the high score has been successfully added. In all other scenarios,
 * the user should be able to see their high score on the list and be notified that it has been successfully added.
 *
 * See the NewHighScoreActivity.kt file for information about the New High Score Activity.
 *
 * @property highScoresDB the file I/O class and the location of the high score list.
 * @property highScoreUI the Recycler View contained within the Main Activity.
 *
 * Assignment 6
 * File:            HighScoreActivity.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class HighScoreActivity : AppCompatActivity() {
    private val LAUNCH_NEW_HIGH_SCORE_ACTIVITY: Int = 1
    private val highScoresDB: HighScoresDatabase = HighScoresDatabase()
    private lateinit var highScoreUI: RecyclerView
    private var score: Long = 0

    /**
     * Called when the activity is starting. Initializes the Toolbar and binds the Recycler View adapter
     * to the Recycler View.
     * @param savedInstanceState unused parameter.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This activity needs to be launched with an intent
        if (intent != null) {
            setContentView(R.layout.activity_high_score)

            // Change the top bar to the Toolbar in the XML
            this.setSupportActionBar(findViewById(R.id.highScoresToolbar))

            // Change the title
            supportActionBar!!.title = resources.getString(R.string.highscores_appbar_title)

            // Load high scores from text file
            highScoresDB.loadFromFile(applicationContext.filesDir.absolutePath)

            // Bind the RecyclerView to the Adapter
            highScoreUI = findViewById(R.id.high_score_list_view)
            highScoreUI.adapter =
                HighScoresAdapter(highScoresDB.highScoreList, this@HighScoreActivity)
            highScoreUI.layoutManager = LinearLayoutManager(this)

            // Get score and time elapsed from the Game Activity
            score = intent.getLongExtra("score", 0)
            val timeElapsed = Calendar.getInstance()
            timeElapsed.timeInMillis = intent.getLongExtra("timeInMillis", 0)

            // Update the corresponding score and time views
            findViewById<TextView>(R.id.timerCardHighScoreText).text = SimpleDateFormat("mm:ss.SS", Locale.ENGLISH).format(timeElapsed.time)
            findViewById<TextView>(R.id.scoreCardHighScoreText).text = score.toString()
            findViewById<TextView>(R.id.missedCirclesText).text = intent.getIntExtra("numCirclesMissed", 0).toString()
        }
    }

    /**
     * Populate the Toolbar with the add button.
     * @param menu the Toolbar menu of the current activity.
     * @return whether the creation of the menu was handled.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_high_score, menu)
        return true
    }

    /**
     * Sends an Intent to the New High Score Activity for the user to input a new high score.
     * @param item the menu item that is selected.
     * @return whether the item selection was handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_high_score -> {
                // Launches the New High Score Activity with an intent and expects a result.
                val intentHighScore = Intent(this, NewHighScoreActivity::class.java)
                intentHighScore.putExtra("score", score)
                intentHighScore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(intentHighScore, LAUNCH_NEW_HIGH_SCORE_ACTIVITY)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Gets high score data from the NewHighScore Activity, stores the new high score in the High Score database,
     * and updates the RecyclerView with the new high score list.
     * @param requestCode the activity that the app is coming back from
     * @param resultCode the status of the result from the activity that the app is returning from
     * @param data the extra data within the intent that is being sent from the other activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Returning from the NewHighScore Activity
        if (requestCode == LAUNCH_NEW_HIGH_SCORE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // Get the high score passed as an intent from the NewHighScore Activity
                val newHighScore = data.getParcelableExtra<HighScore>("high_score")

                // Add the new high score to the list
                if (newHighScore != null) {

                    // Notify the user that the high score was added
                    val toastText: String = if (highScoresDB.addToList(newHighScore)) {
                        newHighScore.playerName + resources.getString(R.string.add_score_toast)
                    }
                    // Score is too low to fit in the 12 high scores list
                    else newHighScore.playerName + resources.getString(R.string.low_score_toast)

                    val highScoreToast = Toast.makeText(
                        this@HighScoreActivity,
                        toastText,
                        Toast.LENGTH_LONG
                    )
                    highScoreToast.show()

                    // Notify the RecyclerView adapter that the list has changed
                    highScoreUI.adapter?.notifyDataSetChanged()

                    // Update the high scores database file
                    highScoresDB.saveToFile(applicationContext.filesDir.absolutePath)
                }
            }

            // A new high score has not been entered
            if (resultCode != Activity.RESULT_CANCELED) {
                // Return to the Main Activity
                finish()
            }
        }
    }

    /**
     * Returns the user to the Main Activity if the back (<-) button on the Toolbar is pressed.
     * @return whether the press was handled.
     */
    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
        return true
    }
}