package com.utd.asg6_nkc160130

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Shapes Frenzy App.
 *
 * Handles the RecyclerView and displays the high score list on the screen. When the list updates, the
 * RecyclerView will update with it.
 *
 * @param mHighScores the high score list to be displayed on the screen.
 * @param context the activity context that the RecyclerView is associated with.
 *
 * Assignment 6
 * File:            HighScoresAdapter.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class HighScoresAdapter(
    private val mHighScores: MutableList<HighScore>,
    private val context: HighScoreActivity
) :

    RecyclerView.Adapter<HighScoresAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val playerPlaceView: TextView = itemView.findViewById(R.id.player_place_item)
        val playerNameView: TextView = itemView.findViewById(R.id.player_name_item)
        val playerScoreView: TextView = itemView.findViewById(R.id.player_score_item)
        val dateTimeView: TextView = itemView.findViewById(R.id.datetime_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        // Inflate the custom layout
        val highScoreView = inflater.inflate(R.layout.recycler_view_item, parent, false)

        // Return a new holder instance
        return ViewHolder(highScoreView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get data model based on position
        val highScore: HighScore = mHighScores[position]

        // Set item views
        viewHolder.playerPlaceView.text = (position + 1).toString()
        viewHolder.playerNameView.text = highScore.playerName
        viewHolder.playerScoreView.text =
            context.resources.getString(R.string.score_str) + highScore.score.toString()
        viewHolder.dateTimeView.text = highScore.dateTimeAsString()
    }

    override fun getItemCount(): Int {
        return mHighScores.size
    }
}