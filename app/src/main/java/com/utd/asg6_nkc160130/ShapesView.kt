package com.utd.asg6_nkc160130

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import com.utd.asg6_nkc160130.shape.*
import java.security.SecureRandom
import java.util.concurrent.Semaphore
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Shapes Frenzy app.
 *
 * This is the custom view on which Shapes are drawn for the game to display. This class handles
 * initializing the shapes, redrawing the shapes, removing shapes, generating new shapes, and handles
 * when the player touches on this custom View.
 *
 * @param appContext the activity on which this ShapesView is on.
 * @param attributes unused parameter.
 *
 * Assignment 6
 * File:            ShapesView.kt
 * @author Kevin Chen
 * NetID:           nkc160130
 * Class:           CS 6326.001
 * Starting:        11/10/2020
 * Due:             11/16/2020
 */
class ShapesView(appContext: Context, attributes: AttributeSet) : View(appContext, attributes) {
    private val NUM_SHAPES = 12

    val shapes : MutableList<Shape> = mutableListOf()

    private val randomNumber = SecureRandom()
    private var viewInitialize = true
    var maxX: Int = 0
    var maxY: Int = 0

    // Generates 12 new shapes randomly.
    init {
        for (iShape in (0 until NUM_SHAPES)) {
            val shapeType = randomNumber.nextInt(2)

            // Add square or circle randomly
            if(shapeType == 0)
                shapes.add(Square())
            if(shapeType == 1)
                shapes.add(Circle())
        }
    }

    /**
     * When the game is started, this will set the properties such as location coordinates for each
     * generated random Shape, and draw each random Shape onto the screen.
     * When the custom View needs to be updated,
     * the custom View will redraw all of the Shapes
     * in the Shapes list according to the new properties of the Shape.
     *
     * @param canvas the canvas to draw the shapes on.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Go through shapes list and draw the shapes
        for(curShape in shapes) {

            // View just initialized, set positions of shapes
            if(viewInitialize) {
                // Set random size of shape (between 32 and 64 dp)
                val shapeSize = randomNumber.nextInt(33) + 32

                // Get bounds of where the shapes can be
                maxX = width - shapeSize
                maxY = height - shapeSize

                // Set location of shape
                var shapeXCoord: Int
                var shapeYCoord: Int

                // Make sure the shape does not intersect any other shape
                var intersectsShape = false
                do {
                    // Set location of shape
                    shapeXCoord = randomNumber.nextInt(maxX - shapeSize + 1) + shapeSize
                    shapeYCoord = randomNumber.nextInt(maxY - shapeSize + 1) + shapeSize

                    // Set properties of this shape
                    if (curShape is Circle) {
                        curShape.setDimensions(
                            shapeXCoord.toFloat(),
                            shapeYCoord.toFloat(),
                            shapeSize.toFloat()
                        )
                    }
                    if (curShape is Square) {
                        curShape.setMetrics(
                            (shapeSize * sqrt(2.0)).roundToInt(),
                            shapeXCoord.toFloat(),
                            shapeYCoord.toFloat(),
                            )
                    }

                    // Check if this shape intersects any other shape
                    for (thisShape in shapes.minus(curShape)) {
                        intersectsShape = thisShape.checkShapeOverlap(curShape)
                        if (intersectsShape)
                            break
                    }
                } while (intersectsShape)
            }

            // Draw the shapes onto the canvas
            if(curShape is Circle)
                canvas.drawCircle(curShape.centerX, curShape.centerY, curShape.radius, curShape.color)
            if(curShape is Square)
                canvas.drawRect(curShape.left, curShape.top, curShape.right, curShape.bottom, curShape.color)
        }

        viewInitialize = false
    }

    /**
     * Randomly generates a new Shape and adds it to the list of Shapes.
     */
    private fun generateNewShape() {
        val shapeType = randomNumber.nextInt(2)

        // Add square or circle randomly
        var newShape: Shape = Square()
        if(shapeType == 1)
            newShape = Circle()

        // Set random size of shape (between 32 and 64 dp)
        val shapeSize = randomNumber.nextInt(33) + 32

        // Get x and y coordinates of the screen
        val location = IntArray(2)
        getLocationOnScreen(location)

        // Get bounds of where the shapes can be
        maxX = width - shapeSize - 10
        maxY = height - shapeSize - 10

        // Set location of shape
        var shapeXCoord: Int
        var shapeYCoord: Int

        // Make sure the shape does not intersect any other shape
        var intersectsShape = false
        do {
            // Set location of shape
            shapeXCoord = randomNumber.nextInt(maxX - shapeSize + 1) + shapeSize
            shapeYCoord = randomNumber.nextInt(maxY - shapeSize + 1) + shapeSize

            // Set properties of this shape
            if (newShape is Circle) {
                newShape.setDimensions(
                    shapeXCoord.toFloat(),
                    shapeYCoord.toFloat(),
                    shapeSize.toFloat()
                )
            }
            if (newShape is Square) {
                newShape.setMetrics(
                    (shapeSize * sqrt(2.0)).roundToInt(),
                    shapeXCoord.toFloat(),
                    shapeYCoord.toFloat(),
                )
            }

            // Check if this shape intersects any other shape
            for (thisShape in shapes) {
                intersectsShape = thisShape.checkShapeOverlap(newShape)
                if (intersectsShape)
                    break
            }
        } while (intersectsShape)

        shapes.add(newShape)

    }

    /**
     * Check if any shapes have reached their maximum age and need to be removed from the
     * list of Shapes.
     */
    fun checkRemoveShapes() {
        // Ensure there is no concurrent modification of the original list
        val shapesCopy = shapes.toMutableList()

        // Check for any shapes that need to be removed
        for(thisShape in shapesCopy) {
            if(thisShape.curAge > thisShape.lifetime) {
                // Shape being removed is the target shape, penalize the player
                if(thisShape is Circle && thisShape.color.color == 0xFF4285F4.toInt()) {
                    // Update score in parent activity
                    val parentActivity = context as GameActivity
                    parentActivity.updateScore(-thisShape.getReward())
                    parentActivity.numCirclesMissed++
                }

                shapes.remove(thisShape)
                generateNewShape()
            }
        }
    }

    /**
     * When the custom ShapesView is touched, check to see if the touch is within any Shape. If so,
     * remove the Shape and generate a new Shape in its place. If a target Shape is touched, update
     * the score on the Game Activity.
     *
     * @param event the motion event that the screen recorded.
     * @return whether the event was handled.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Motion Event is not ACTION_DOWN (cannot swipe across the screen to touch shapes)
        if(event.action != MotionEvent.ACTION_DOWN)
            return true

        // Play touch noise
        playSoundEffect(SoundEffectConstants.CLICK)

        // Ensure there is no concurrent modification of the original list
        val shapesCopy = shapes.toMutableList()

        // Iterate through all the shapes and see if any of the shapes are touched
        for(curShape in shapesCopy) {
            // Shape is the target shape
            if(curShape is Circle && curShape.color.color == 0xFF4285F4.toInt()) {
                // Touch is within the shape
                val distanceFromCenter = sqrt((event.x - curShape.centerX).pow(2) + (event.y - curShape.centerY).pow(2))
                if(distanceFromCenter < curShape.radius) {
                    // Update score in parent activity
                    val parentActivity = context as GameActivity
                    parentActivity.updateScore(curShape.getReward())

                    // Replace this shape with a new shape
                    shapes.remove(curShape)
                    generateNewShape()
                    invalidate()
                    return true
                }
            }

            // Touch is in a non-goal shape
            else {
                // Touch is within the shape
                val distanceFromCenter = sqrt((event.x - curShape.centerX).pow(2) + (event.y - curShape.centerY).pow(2))
                if(distanceFromCenter < curShape.radius) {
                    // Replace this shape with a new shape
                    shapes.remove(curShape)
                    generateNewShape()
                    invalidate()
                    return true
                }
            }
        }

        return super.onTouchEvent(event)
    }
}