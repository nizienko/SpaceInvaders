package com.github.nizienko.spaceinvaders.objects

import java.awt.Color
import java.awt.Point

class Healer(private var x: Int, private var y: Int, private val maxY: Int) : GameObject() {
    override val position: Point
        get() = Point(x, y)
    override val width: Int = 50
    override val height: Int = 50
    override val pictureSizeX: Int = 3
    override val pictureSizeY: Int = 3
    override val picture: Painter.() -> Unit = {
        color(Color.RED)
        rect(0, 1, 3, 1)
        rect(1, 0, 1, 3)
    }
    private val speed = 13
    override fun process() {
        y += speed
        if (y >= maxY) isOut = true
    }

    override fun isExpired(): Boolean = isOut

    var isOut = false
}