package com.github.nizienko.spaceinvaders.objects

import java.awt.Color
import java.awt.Point

class SpaceShipExplosionAnimation(x: Int, y: Int) : Animation() {
    override val durationMs: Long = 400L

    override val position: Point = Point(x, y)
    override val width: Int = 100
    override val height: Int = 100
    override val pictureSizeX: Int = 40
    override val pictureSizeY: Int = 40

    override val picture: Painter.() -> Unit = {
        color(Color.RED)
        oval(25, 25, 50 - shift, 50 - shift)
        color(Color.ORANGE)
        oval(25, 25, 40 - shift, 40 - shift)
        color(Color.YELLOW)
        oval(25, 25, 25 - shift, 25 - shift)
    }
    private val shift: Int
        get() = phase * 3
}