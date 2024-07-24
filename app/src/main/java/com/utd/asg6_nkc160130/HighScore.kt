package com.utd.asg6_nkc160130

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * A High Score object that stores the player's name, score and date and time associated with the high score achieved.
 *
 * @property playerName the player's name associated with the high score entry.
 * @property score the score that the player achieved.
 * @property dateTime the date and time associated with the high score entry.
 *
 * Assignment 6
 * File:            HighScore.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class HighScore : Parcelable, Comparable<HighScore> {
    var playerName: String = ""
    var score: Int = 0
    var dateTime: Calendar = Calendar.getInstance()

    /**
     * Constructs a High Score object from a player name, score, and dateTime
     */
    constructor(playerName: String?, score: Int, dateTime: Calendar) {
        this.playerName = playerName ?: ""
        this.score = score
        this.dateTime = dateTime
    }

    /**
     * Custom constructor for a Parcel into HighScore.
     */
    private constructor(parcel: Parcel) {

        playerName = parcel.readString().toString()
        score = parcel.readInt()
        dateTime = Calendar.getInstance()
        dateTime.timeInMillis = parcel.readLong()
    }

    // Parcelable creator
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<HighScore> {
            override fun createFromParcel(parcel_in: Parcel): HighScore {
                return HighScore(parcel_in)
            }

            override fun newArray(size: Int): Array<HighScore> {
                return emptyArray()
            }
        }
    }

    /**
     * Prints the HighScore out as a tab-delimited String.
     * @return a string representation of the HighScore object.
     */
    override fun toString(): String {
        return "$playerName\t$score\t${dateTime.timeInMillis}"
    }

    /**
     * Compares two HighScores by score value. If the score value is equal, it uses the dateTime to compare.
     * @param other the right hand side HighScore.
     * @return if the calling HighScore is greater than, less than or equal to the right hand side's HighScore.
     */
    override fun compareTo(other: HighScore): Int {
        // Compare scores if they are not equal
        if (this.score != other.score) return this.score - other.score

        // In case of duplicate scores, compare dates
        return this.dateTime.compareTo(other.dateTime)
    }

    /**
     * Determines if two HighScore objects are equal.
     * @param other the right hand side HighScore.
     * @return whether the HighScore parameters of the calling object is equal to the right hand side's HighScore parameters.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is HighScore)
            playerName == other.playerName && score == other.score && dateTime == other.dateTime
        else false
    }

    /**
     * Formats the dateTime of this high score as a string in the standard format.
     * @return a string formatted as "MM/dd/yyyy HH:mm" from the high score object's dateTime parameter.
     */
    fun dateTimeAsString(): String {
        val datetimeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
        return datetimeFormat.format(dateTime.time)
    }

    /**
     * Write high score data to a Parcel.
     * @param out parcel to write high score data in.
     * @param flags unused parameter.
     */
    override fun writeToParcel(out: Parcel?, flags: Int) {
        out?.writeString(playerName)
        out?.writeInt(score)
        out?.writeLong(dateTime.timeInMillis)
    }

    // Does not need to be implemented
    override fun describeContents(): Int {
        return 0
    }

    // No extra manipulation needed by the class
    override fun hashCode(): Int {
        return super.hashCode()
    }
}