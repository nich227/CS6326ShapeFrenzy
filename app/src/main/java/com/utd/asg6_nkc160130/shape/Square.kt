package com.utd.asg6_nkc160130.shape

import android.graphics.Rect
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Shapes Frenzy app.
 *
 * Square class that extends Shape. Additional parameters for right, left, top and bottom are included
 * for passing to drawRect on the Canvas. This shape is initialized and treated almost exactly like a
 * Circle, except the side length is passed in instead of the radius, which is calculated based on the
 * side length of the Square.
 *
 * Assignment 6
 * File:            Square.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class Square : Shape() {
    var right: Float = 0.0F
    var left: Float = 0.0F
    var top: Float = 0.0F
    var bottom: Float = 0.0F

    var length: Int = 0

    /**
     * Sets the parameters needed to call drawRect on the Square, and also calculates the radius
     * of the Square based on the side length.
     *
     * @param sideLength the side length of the square.
     * @param centerX the X coordinate of the center of the square.
     * @param centerY the Y coordinate of the center of the square.
     */
    fun setMetrics(sideLength: Int, centerX: Float, centerY: Float) {
        length = sideLength
        this.centerX = centerX
        this.centerY = centerY

        right = centerX + (sideLength / 2.0F)
        left = centerX - (sideLength / 2.0F)
        top = centerY - (sideLength / 2.0F)
        bottom = centerY + (sideLength / 2.0F)
        
        radius = (length / 2.0F) * sqrt(2.0).toFloat()
    }


}