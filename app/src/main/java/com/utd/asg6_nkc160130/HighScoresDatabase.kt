package com.utd.asg6_nkc160130

import java.io.*
import java.lang.Exception
import java.util.*

/**
 * Shapes Frenzy App.
 *
 * Acts as a database for high scores. Contains the high score list which the Main Activity
 * interacts with to pull high score information. Also handles file I/O with the high_scores.txt
 * file to store high scores so that the app will load from it when it starts.
 *
 * @property highScoreList the list of the top 12 high scores.
 *
 * Assignment 6
 * File:            HighScoresDatabase.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class HighScoresDatabase {
    var highScoreList: MutableList<HighScore> = mutableListOf()

    /**
     * Saves the high score list to the high_scores.txt file.
     * @param filesDir the path of the Android app data folder.
     * @return whether saving the high score list to text file was successful.
     */
    fun saveToFile(filesDir: String): Boolean {

        // Create high scores folder if it doesn't already exist
        val highScoresFolder = File(filesDir, "high_scores")
        if (!highScoresFolder.exists()) highScoresFolder.mkdir()

        // Write data to text file
        return try {
            val txtDB = File(highScoresFolder, "high_scores.txt")
            val writer = FileWriter(txtDB)

            for ((iHighScore, highScore) in highScoreList.withIndex()) {
                if (iHighScore < highScoreList.size - 1)
                    writer.append("$highScore\n")
                else
                    writer.append("$highScore")
            }
            writer.flush()
            writer.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Loads the high scores from the high_scores.txt file and stores it in the highScoreList.
     * @param filesDir the path of the Android app data folder.
     * @return whether loading from file to the high score list was successful.
     */
    fun loadFromFile(filesDir: String): Boolean {
        // Create high scores folder if it doesn't already exist
        val highScoresFolder = File(filesDir, "high_scores")
        if (!highScoresFolder.exists()) {
            highScoresFolder.mkdir()
            return false
        }

        // Get file and initialize StringBuilder
        val txtDB = File(highScoresFolder, "high_scores.txt")
        val input = StringBuilder()

        // Get string from file
        try {
            val reader = BufferedReader(FileReader(txtDB))

            var line = reader.readLine();
            while (line != null && line != "\n") {
                input.append(line)
                input.append("\n")

                line = reader.readLine();
            }

            reader.close()

            // Convert each line to a high score and append to list
            for (player_line in input.toString().split("\n")) {
                if (player_line != "") {
                    val values = player_line.split("\t")
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = values[2].toLong()
                    val newScore = HighScore(values[0], values[1].toInt(), calendar)
                    highScoreList.add(newScore)
                }
            }
        } catch (e: IOException) {
            return false
        }

        // Sort the list after everything is added and make sure there are only 12 high scores
        highScoreList.sortDescending()
        if (highScoreList.size > 12) highScoreList = highScoreList.toMutableList().subList(0, 12)

        return true
    }

    /**
     * Adds a high score to the high score list and sorts the list by score.
     * @param highScore a high score to add to the list.
     * @return if the high score is in the top 12 high scores.
     */
    fun addToList(highScore: HighScore): Boolean {
        // Add a new high score to the list, then keep the list sorted
        highScoreList.add(highScore)
        highScoreList.sortDescending()

        // The list should only have 12 high scores
        if (highScoreList.size > 12) highScoreList.removeAt(highScoreList.size - 1)

        return highScoreList.contains(highScore)
    }
}