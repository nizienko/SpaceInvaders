package com.github.nizienko.spaceinvaders.objects

import java.awt.Point

class InvadersDeathAnimation(x: Int, y: Int) : Animation() {
    override val position: Point = Point(x, y)
    override val width: Int = 70
    override val height: Int = 70
    override val pictureSizeX: Int = 110
    override val pictureSizeY: Int = 80
    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject)
        rect(0 - shift, 40 - shift, 55, 10)
        rect(55 + shift, 40 - shift, 55, 10)
        rect(20 - shift, 20 - shift, 35, 40)
        rect(55 + shift, 20 - shift, 35, 40)

        rect(20 - shift, 60 + shift, 10, 10)
        rect(80 + shift, 60 + shift, 10, 10)
        rect(20 - shift, 0 - shift, 10, 10)
        rect(30 - shift, 10 - shift, 10, 10)
        rect(80 + shift, 0 - shift, 10, 10)
        rect(70 + shift, 10 - shift, 10, 10)
        rect(0 - shift, 20 - shift, 10, 30)
        rect(100 + shift, 20 - shift, 10, 30)
        rect(10 - shift, 50 + shift, 10, 10)
        rect(90 + shift, 50 + shift, 10, 10)
        rect(10 - shift, 70 + shift, 10, 10)
        rect(90 + shift, 70 + shift, 10, 10)

        color(defaultColours.background)
        rect(30 - shift, 30 - shift, 10, 10)
        rect(70 + shift, 30 - shift, 10, 10)
    }

    private val shift
        get() = faze * 4

    override val durationMs = 200L
}