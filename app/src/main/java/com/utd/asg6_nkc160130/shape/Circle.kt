package com.utd.asg6_nkc160130.shape

/**
 * Shapes Frenzy App.
 *
 * Circles class that extends Shape, just adds a function to set the dimensions of the circle
 * all at once.
 *
 * Assignment 6
 * File:            Circle.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class Circle : Shape() {
    /**
     * Set the dimensions of the shape all at once instead of setting them each manually.
     *
     * @param centerX the X coordinate of the center of the shape.
     * @param centerY the Y coordinate of the center of the shape.
     * @param radius the radius of the shape.
     */
    fun setDimensions(centerX: Float, centerY: Float, radius: Float) {
        this.centerX = centerX
        this.centerY = centerY
        this.radius = radius
    }
}