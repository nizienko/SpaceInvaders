package com.github.nizienko.spaceinvaders.objects


import java.awt.Point
import java.util.concurrent.ThreadLocalRandom


class Invader(private val shiftX: Int, private val shiftY: Int, private val n: Int) : GameObject() {
    private var x: Int = 10 + shiftX
    private var y: Int = 10 + shiftY
    override val position: Point
        get() = Point(x, y)
    override val width: Int = 70
    override val height: Int = 70

    override val pictureSizeX: Int = 11
    override val pictureSizeY: Int = 8

    private var phase = 0

    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject)
        rect(0, 4, 11, 1)
        rect(2, 2, 7, 4)

        rect(2, 6, 1, 1)
        rect(8, 6, 1, 1)
        rect(2, 0, 1, 1)
        rect(3, 1, 1, 1)
        rect(8, 0, 1, 1)
        rect(7, 1, 1, 1)
        if (phase < 5) {
            rect(0, 4, 1, 3)
            rect(10, 4, 1, 3)
            rect(1, 3, 1, 1)
            rect(9, 3, 1, 1)
            rect(3, 7, 2, 1)
            rect(6, 7, 2, 1)
        } else {
            rect(0, 2, 1, 3)
            rect(10, 2, 1, 3)
            rect(1, 5, 1, 1)
            rect(9, 5, 1, 1)
            rect(1, 7, 1, 1)
            rect(9, 7, 1, 1)
        }
        color(defaultColours.background)
        rect(3, 3, 1, 1)
        rect(7, 3, 1, 1)
    }

    var totalInvadersLeft = 0
    override fun process() {
        phase++
        if (phase > 9) phase = 0
        val shotSpeed = when {
            totalInvadersLeft < 5 -> 130
            totalInvadersLeft < 10 -> 200
            totalInvadersLeft < 20 -> 500
            totalInvadersLeft < 30 -> 900
            else -> 1000
        }
        if (ThreadLocalRandom.current().nextInt(shotSpeed) == shotSpeed / 60 * n) fire(x, y)
    }


    fun move(point: Point) {
        x = point.x + shiftX
        y = point.y + shiftY
    }

    var isKilled: Boolean = false

    var fire: (x: Int, y: Int) -> Unit = { _, _ -> }
    override fun isExpired(): Boolean = isKilled
}
