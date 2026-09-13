package com.github.nizienko.spaceinvaders.objects

import java.awt.Color
import kotlin.math.roundToInt

class SpaceShip(override var x: Int, override var y: Int, private val maxX: Int) :
    GameObject() {
    override val width: Int = 130
    override val height: Int = 200
    override val pictureSizeX: Int = 16
    override val pictureSizeY: Int = 20

    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObjectBrighter)
        polygon(BODY_POINTS)
        rect(7, 1, 2, 1)
        rect(6, 2, 4, 1)
        rect(7, 3, 2, 8)
        rect(1, 8, 3, 7)
        rect(12, 8, 3, 7)

        color(defaultColours.gameObject)
        rect(7, 0, 2, 1)
        rect(7, 9, 2, 1)
        rect(6, 3, 1, 9)
        rect(9, 3, 1, 9)
        rect(4, 6, 1, 9)
        rect(11, 6, 1, 9)
        rect(3, 8, 2, 7)
        rect(11, 8, 2, 7)

        color(defaultColours.gameObjectDarker)
        rect(7, 6, 2, 1)
        rect(7, 10, 2, 1)
        rect(5, 5, 1, 8)
        rect(10, 5, 1, 8)
        rect(4, 9, 1, 3)
        rect(11, 9, 1, 3)
        rect(0, 10, 1, 6)
        rect(15, 10, 1, 6)
        rect(2, 15, 2, 1)
        rect(2, 15, 2, 1)
        rect(12, 15, 2, 1)

        if (phase < 2) {
            color(FLAME_ORANGE)
            polygon(LEFT_FLAME_POINTS)
            polygon(RIGHT_FLAME_POINTS)
            color(FLAME_YELLOW)
            rect(3, 16, 1, 2)
            rect(12, 16, 1, 2)
        } else {
            color(FLAME_YELLOW)
            polygon(LEFT_FLAME_POINTS)
            polygon(RIGHT_FLAME_POINTS)
            color(FLAME_ORANGE)
            rect(3, 16, 1, 2)
            rect(12, 16, 1, 2)
        }
        color(COCKPIT_LIGHT)
        rect(7, 2, 2, 1)

        color(COCKPIT_BLUE)
        rect(7, 3, 2, 2)

        color(COCKPIT_DARK_BLUE)
        rect(7, 5, 2, 1)
    }

    private var acceleration: Double = 0.0
    private var direction: Direction = Direction.STOP

    enum class Direction {
        LEFT, RIGHT, STOP
    }

    fun moveLeft() {
        direction = Direction.LEFT

        acceleration = -15.0
    }

    fun stopLeft() {
        if (direction == Direction.LEFT) {
            direction = Direction.STOP
        }
    }

    fun moveRight() {
        direction = Direction.RIGHT
        acceleration = 15.0
    }

    fun stopRight() {
        if (direction == Direction.RIGHT) {
            direction = Direction.STOP
        }
    }

    var shootLag = 0
    private var nextFire = 0
    private var phase = 0
    override fun process() {
        phase++
        if (phase > 3) {
            phase = 0
        }
        // moving
        if (isFire) {
            if (nextFire > 0) {
                fireFunction(x, y)
                nextFire = (shootLag) * -1
            }
        }
        nextFire++
        if (nextFire > 10) nextFire = 10

        val newX = x + acceleration
        x = if (newX > width / 2 && newX < maxX - width / 2) {
            newX.roundToInt()
        } else if (newX <= width / 2) {
            acceleration = 0.0
            width / 2
        } else {
            acceleration = 0.0
            maxX - width / 2
        }
        if (direction == Direction.STOP) {
            if (acceleration > 0) {
                acceleration -= 5
            } else if (acceleration < 0) {
                acceleration += 5
            }
        }
    }

    override fun isExpired(): Boolean = isKilled

    fun isObjectHit(bullet: GameObject): Boolean {
        if (isKilled) return false
        val bx = bullet.x
        val by = bullet.y
        return (bx >= x - width / 2
                && bx <= x + width / 2
                && by + bullet.height / 2 >= y
                && by - bullet.height / 2 <= y + height / 2)
                || (bx >= x - width / 4
                && bx <= x + width / 4
                && by + bullet.height / 2 >= y - height / 2
                && by - bullet.height / 2 <= y + height / 2)
    }

    private var lastBulletTime = System.currentTimeMillis()
    fun fire(on: Boolean) {
        isFire = on
    }

    private var isFire = false

    var isKilled = false
    var fireFunction: (x: Int, y: Int) -> Unit = { _, _ -> }

    private companion object {
        val FLAME_ORANGE = Color(255, 115, 0)
        val FLAME_YELLOW = Color(255, 196, 0)
        val COCKPIT_LIGHT = Color(109, 109, 255)
        val COCKPIT_BLUE = Color(0, 0, 255)
        val COCKPIT_DARK_BLUE = Color(0, 0, 185)
        val BODY_POINTS = intArrayOf(
            7, 0, 9, 0, 10, 1, 11, 8, 16, 10, 16, 16, 11, 16, 11, 14,
            9, 13, 7, 13, 5, 14, 5, 16, 0, 16, 0, 10, 5, 8, 6, 1,
        )
        val LEFT_FLAME_POINTS = intArrayOf(1, 16, 5, 16, 4, 18, 2, 18)
        val RIGHT_FLAME_POINTS = intArrayOf(11, 16, 15, 16, 14, 18, 12, 18)
    }
}
