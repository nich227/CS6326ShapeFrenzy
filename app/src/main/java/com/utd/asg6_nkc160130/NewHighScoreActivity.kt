package com.utd.asg6_nkc160130

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shapes Frenzy App.
 *
 * This New High Scores Activity allows the user to add a high score to the high score database, which
 * the Main Activity will pull from to display the top 12 high scores. There are three text fields, corresponding
 * to the three parameters of the High Score class:
 * - Player Name: The player name associated with the new high score.
 * - Score: The high score that the player achieved.
 * - Date and Time: The date and time that the user scored the high score.
 *
 * These fields also have built-in input validation, and will notify the user if the input in these fields are
 * improperly formatted. There is a save button on the Toolbar that will not activate until all fields are filled in
 * and are valid. After the user wants to save and the input is valid, this activity will send the Main Activity the
 * high score information through an Intent that contains a High Score object.
 *
 * See the MainActivity.kt file for information about the Main Activity.
 *
 * @property datetime stores the date and time chosen in the Date Picker and Time Picker.
 * @property datetimeFormat the standard date/time formatting used in the app.
 * @property inputValid whether all fields have valid input.
 *
 * Assignment 6
 * File:            NewHighScoreActivity.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class NewHighScoreActivity : AppCompatActivity() {

    val datetime: Calendar = Calendar.getInstance()
    val datetimeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
    private var inputValid = false

    /**
     * Called when the activity is starting. Initializes the Toolbar and all view listeners, and sets default values.
     * @param savedInstanceState unused parameter.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This activity needs to be launched with an intent
        if (intent != null) {
            setContentView(R.layout.activity_new_high_score)

            // Change the top bar to the Toolbar in the XML
            val toolbar = findViewById<Toolbar>(R.id.appNewHighScoreToolbar)
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            // Change the title
            supportActionBar!!.title = resources.getString(R.string.new_appbar_title)

            // Set date for datetime to current time

            // Set listener for name text box
            val nameEditBox = findViewById<TextInputEditText>(R.id.editTextPersonName)
            nameEditBox.addTextChangedListener(NameInputListener())

            // Set listener for score text box
            val scoreEditBox = findViewById<TextInputEditText>(R.id.editScoreValue)
            scoreEditBox.addTextChangedListener(ScoreInputListener())
            scoreEditBox.setText(intent.getLongExtra("score", 0).toString())

            // Set listener for datetime text box
            val datetimeEditBox = findViewById<TextInputEditText>(R.id.editDateTimeValue)
            datetimeEditBox.onFocusChangeListener =
                DateTimeClickListener(DateSetListener(TimeSetListener()))
            datetimeEditBox.addTextChangedListener(DateTimeManualInputListener())

            // Set date for datetime to current time
            datetimeEditBox.setText(datetimeFormat.format(datetime.time))
        }
    }

    /**
     * Listener for when the Date Picker's date is set. Processes the user's input,
     * updates the text box field, and shows a Time Picker dialog afterwards.
     */
    inner class DateSetListener(private val timePicker: TimePickerDialog.OnTimeSetListener) :
        DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            datetime.set(Calendar.YEAR, year)
            datetime.set(Calendar.MONTH, monthOfYear)
            datetime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            findViewById<TextInputEditText>(R.id.editDateTimeValue).setText(
                datetimeFormat.format(datetime.time)
            )

            TimePickerDialog(
                this@NewHighScoreActivity,
                timePicker,
                datetime.get(Calendar.HOUR_OF_DAY),
                datetime.get(Calendar.MINUTE), true
            ).show()
        }
    }

    /**
     * Listener for when the Time Picker's time is set. Processes the user's input and updates the text box field.
     */
    inner class TimeSetListener : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker, hours: Int, minutes: Int) {
            datetime.set(Calendar.HOUR_OF_DAY, hours)
            datetime.set(Calendar.MINUTE, minutes)

            findViewById<TextInputEditText>(R.id.editDateTimeValue).setText(
                datetimeFormat.format(datetime.time)
            )
        }
    }

    /**
     * Launches a Date Picker when the user changes current focus to the date and time field,
     * and attaches a Date Picker listener.
     */
    inner class DateTimeClickListener(
        private val datePickerListener: DatePickerDialog.OnDateSetListener
    ) :
        View.OnFocusChangeListener {

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (!hasFocus) return

            System.err.println("Hello world!")
            DatePickerDialog(
                this@NewHighScoreActivity,
                datePickerListener,
                datetime.get(Calendar.YEAR),
                datetime.get(Calendar.MONTH),
                datetime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    /**
     * Performs input validation on the player name field when the user types in the information, then checks
     * if all inputs are valid.
     *
     * Input validation criterion: The name is not empty.
     */
    inner class NameInputListener : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (nameBoxValid()) findViewById<TextView>(R.id.player_input_error).text = ""
            else findViewById<TextView>(R.id.player_input_error).text =
                resources.getString(R.string.player_name_error)

            checkAllInputValid()
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    /**
     * Performs input validation on the score field when the user types in the information, then checks
     * if all inputs are valid.
     *
     * Input validation criterion: The score is a positive 32-bit integer.
     */
    inner class ScoreInputListener : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (scoreBoxValid()) findViewById<TextView>(R.id.score_input_error).text = ""
            else findViewById<TextView>(R.id.score_input_error).text =
                resources.getString(R.string.score_error)
            checkAllInputValid()
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    /**
     * Performs input validation on the date and time field when the user types in the information, then checks
     * if all inputs are valid.
     *
     * Input validation criterion: The date is in a valid date format, is not in the future and is not more than
     * one month before the current time.
     */
    inner class DateTimeManualInputListener : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            val monthFromToday = Calendar.getInstance()
            monthFromToday.add(Calendar.MONTH, -1)

            if (datetimeBoxValid()) findViewById<TextView>(R.id.datetime_input_error).text = ""
            else {
                try {
                    val inputDate =
                        datetimeFormat.parse(findViewById<TextInputEditText>(R.id.editDateTimeValue).text.toString())

                    // Date is in the future or more than a month in the past
                    if (inputDate != null
                        && (inputDate > Calendar.getInstance().time
                                || inputDate <= monthFromToday.time)
                    ) {
                        findViewById<TextView>(R.id.datetime_input_error).text =
                            resources.getString(R.string.datetime_bounds_error)
                    }
                }

                // Date is not in an acceptable format
                catch (e: Exception) {
                    findViewById<TextView>(R.id.datetime_input_error).text =
                        resources.getString(R.string.datetime_format_error)
                    return
                }
            }

            checkAllInputValid()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * Performs input validation on all fields and calls the options menu of the Toolbar to check
     * if the save button needs to be enabled.
     */
    fun checkAllInputValid() {
        // Check if all of the input is valid
        inputValid = nameBoxValid() && scoreBoxValid() && datetimeBoxValid()

        // Check if the save button needs to be enabled
        invalidateOptionsMenu()
    }

    /**
     * Validate the player name input.
     * @return whether the player name is empty or filled.
     */
    fun nameBoxValid(): Boolean {
        return findViewById<TextInputEditText>(R.id.editTextPersonName).text.toString().isNotEmpty()
    }

    /**
     * Validate the score input.
     * @return whether the score input is valid.
     */
    fun scoreBoxValid(): Boolean {
        return try {
            val score = findViewById<TextInputEditText>(R.id.editScoreValue).text.toString().toInt()

            // Score must be positive
            score > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Validate the date time input.
     * @return whether all the inputs (player name, score, date and time) are valid.
     */
    fun datetimeBoxValid(): Boolean {
        return try {
            val inputDate =
                datetimeFormat.parse(findViewById<TextInputEditText>(R.id.editDateTimeValue).text.toString())
            val monthFromToday = Calendar.getInstance()
            monthFromToday.add(Calendar.MONTH, -1)
            return inputDate != null
                    && inputDate <= Calendar.getInstance().time
                    && inputDate > monthFromToday.time
        } catch (e: java.lang.Exception) {
            false
        }
    }

    /**
     * Populate the Action Bar with the save button.
     * @param menu the Toolbar menu of the current activity.
     * @return whether the creation of the menu was handled.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_new_high_score, menu)
        return true
    }

    /**
     * Determines whether the save button should be enabled.
     * @param menu the Toolbar menu of the current activity.
     * @return whether the update of the menu was handled.
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.isEnabled = inputValid
        return true
    }

    /**
     * Returns the user to the High Score Activity if the back (<-) button on the Toolbar is pressed.
     * @return whether the press was handled.
     */
    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
        return true
    }

    /**
     * Sends an Intent back to the MainActivity for saving the new high score.
     * @param item the menu item that is selected.
     * @return whether the item selection was handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_high_score -> {
                val intentHighScore = Intent()

                val playerName =
                    findViewById<TextInputEditText>(R.id.editTextPersonName).text.toString()
                val playerScore =
                    findViewById<TextInputEditText>(R.id.editScoreValue).text.toString().toInt()
                val dateTimeIn = Calendar.getInstance()
                try {
                    dateTimeIn.time =
                        datetimeFormat.parse(findViewById<TextInputEditText>(R.id.editDateTimeValue).text.toString())!!
                } catch (e: Exception) {
                }

                intentHighScore.putExtra(
                    "high_score",
                    HighScore(playerName, playerScore, dateTimeIn)
                )
                setResult(Activity.RESULT_OK, intentHighScore)
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}