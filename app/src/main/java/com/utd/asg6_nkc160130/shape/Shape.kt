package com.utd.asg6_nkc160130.shape

import android.graphics.Paint
import java.security.SecureRandom
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * Shapes Frenzy App.
 *
 * Generic Shapes class, that contains metrics for any shape, sets a random color,
 * stores the shape's creation time and age, and the shape's lifetime.
 *
 * Assignment 6
 * File:            Shape.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
abstract class Shape {

    val color: Paint = Paint()
    private val randomNumber = SecureRandom()
    var lifetime: Int = 0
    var curAge: Int = 0
    private val createTime: Long = System.currentTimeMillis()

    var centerX: Float = 0.0F
    var centerY: Float = 0.0F
    var radius: Float = 0.0F

    private val shapeColors = mapOf(
        "RED" to 0xFFDB4437,
        "ORANGE" to 0xFFFB8C00,
        "YELLOW" to 0xFFF4B400,
        "GREEN" to 0xFF0F9D58,
        "BLUE" to 0xFF4285F4,
        "PURPLE" to 0xFF8E24AA,
        "WHITE" to 0xFFFFFFFF
    )

    // Initializes the random variables: color and lifetime.
    init {
        color.color = shapeColors.entries.elementAt(randomNumber.nextInt(7)).value.toInt()
        lifetime = randomNumber.nextInt(5) + 3
    }

    /**
     * Moves the position of this shape randomly, and checks if the random move would collide with
     * another shape or the wall.
     *
     * @param otherShapes a list of all other shapes currently on the ShapesView.
     * @param maxX maximum value of X, taken from the ShapesView.
     * @param maxY maximum value of Y, taken from the ShapesView.
     */
    open fun movePosition(otherShapes: List<Shape>, maxX: Int, maxY: Int) {
        // Move the center of the shape by 20
        var moveCenterY = randomNumber.nextInt(21).toFloat()
        var moveCenterX = sqrt(400.0 - moveCenterY.pow(2)).toFloat()

        // Determine the direction of movement
        if (randomNumber.nextBoolean()) moveCenterY *= -1
        if (randomNumber.nextBoolean()) moveCenterX *= -1

        // Apply movement
        this.centerY += moveCenterY
        this.centerX += moveCenterX

        // Update if this shape is a square
        if(this is Square)
            setMetrics(this.length, this.centerX, this.centerY)

        // Check if this move would collide with any other shape
        for (shape in otherShapes) {
            if (checkShapeOverlap(shape)) {
                // Go the other way
                this.centerY -= (moveCenterY * 2)
                this.centerX -= (moveCenterX * 2)
                break
            }
        }

        // Check if this move would collide with the wall
        if(this.centerX >= maxX || this.centerY >= maxY || this.centerX <= radius + 10 || this.centerY <= radius + 10) {
            // Go the other way
            this.centerY -= (moveCenterY * 2)
            this.centerX -= (moveCenterX * 2)
        }

        // Update the age of the shape
        curAge++
    }

    /**
     * Compares two shapes' positions to see if they would collide.
     *
     * @param rhs the other shape, as opposed to this shape.
     * @return whether these two shapes would collide.
     */
    fun checkShapeOverlap(rhs: Shape): Boolean {
        // Get distance between centers of circle and circle/square
        val centerDistance = sqrt(((this.centerX - rhs.centerX).toDouble().pow(2) + (this.centerY - rhs.centerY).toDouble().pow(2)))
        return centerDistance <= (this.radius + rhs.radius)
    }

    /**
     * Gets the reward of tapping this shape (only used for the target shape).
     * Calculated as inversely proportional to the sum of time it took for the player to touch the shape
     * and the radius of the shape.
     *
     * @return the calculated reward for touching this shape.
     */
    fun getReward(): Long {
        val timeSinceCreation: Double = (System.currentTimeMillis() - createTime.toDouble()) / 1000.0
        return (1000 * ((timeSinceCreation + radius).pow(-1))).roundToLong()
    }
}