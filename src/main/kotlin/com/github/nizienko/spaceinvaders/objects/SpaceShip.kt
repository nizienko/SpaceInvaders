package com.github.nizienko.spaceinvaders.objects

import java.awt.Color
import java.awt.Point
import kotlin.math.roundToInt

class SpaceShip(private var x: Int, private var y: Int, private val maxX: Int) :
    GameObject() {
    override val position: Point
        get() = Point(x, y)
    override val width: Int = 130
    override val height: Int = 200
    override val pictureSizeX: Int = 16
    override val pictureSizeY: Int = 20

    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject.brighter())
        polygon(
            Point(7, 0),
            Point(9, 0),
            Point(10, 1),
            Point(11, 8),
            Point(16, 10),
            Point(16, 16),
            Point(11, 16),
            Point(11, 14),
            Point(9, 13),
            Point(7, 13),
            Point(5, 14),
            Point(5, 16),
            Point(0, 16),
            Point(0, 10),
            Point(5, 8),
            Point(6, 1),
        )
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

        color(defaultColours.gameObject.darker())
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
            color(Color(255, 115, 0))
            polygon(Point(1, 16), Point(5, 16), Point(4, 18), Point(2, 18))
            polygon(Point(11, 16), Point(15, 16), Point(14, 18), Point(12, 18))
            color(Color(255, 196, 0))
            rect(3, 16, 1, 2)
            rect(12, 16, 1, 2)
        } else {
            color(Color(255, 196, 0))
            polygon(Point(1, 16), Point(5, 16), Point(4, 18), Point(2, 18))
            polygon(Point(11, 16), Point(15, 16), Point(14, 18), Point(12, 18))
            color(Color(255, 115, 0))
            rect(3, 16, 1, 2)
            rect(12, 16, 1, 2)
        }
        color(Color(109, 109, 255))
        rect(7, 2, 2, 1)

        color(Color(0, 0, 255))
        rect(7, 3, 2, 2)

        color(Color(0, 0, 185))
        rect(7, 5, 2, 1)
    }

    private var acceleration: Double = 0.0

    fun moveLeft() {
        acceleration -= 20
    }

    fun moveRight() {
        acceleration += 20
    }

    private var phase = 0
    override fun process() {
        phase++
        if (phase > 3) {
            phase = 0
        }
        // moving
        if (isFire) {
            fireFunction(position.x, position.y)
            isFire = false
        }
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
        if (acceleration > 0) {
            acceleration -= 0.5
        } else if (acceleration < 0) {
            acceleration += 0.5
        }
    }

    override fun isExpired(): Boolean = isKilled

    fun isObjectHit(bullet: GameObject): Boolean {
        if (isKilled) return false
        val bx = bullet.position.x
        val by = bullet.position.y
        return (bx >= position.x - width / 2
                && bx <= position.x + width / 2
                && by + bullet.height / 2 >= position.y
                && by - bullet.height / 2 <= position.y + height / 2)
                || (bx >= position.x - width / 4
                && bx <= position.x + width / 4
                && by + bullet.height / 2 >= position.y - height / 2
                && by - bullet.height / 2 <= position.y + height / 2)
    }

    private var lastBulletTime = System.currentTimeMillis()
    fun fire() {
        if (System.currentTimeMillis() - lastBulletTime > 800 - totalKilled) {
            isFire = true
            lastBulletTime = System.currentTimeMillis()
        }
    }

    private var isFire = false

    var isKilled = false
    var fireFunction: (x: Int, y: Int) -> Unit = { _, _ -> }
    var totalKilled: Int = 0
}