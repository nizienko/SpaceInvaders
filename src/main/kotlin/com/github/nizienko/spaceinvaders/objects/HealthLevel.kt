package com.github.nizienko.spaceinvaders.objects

import java.awt.Point

class HealthLevel : GameObject() {
    override val position: Point = Point(300, 20)
    override val width: Int = 600
    override val height: Int = 40
    override val pictureSizeX: Int = 100
    override val pictureSizeY: Int = 2
    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject)
        rect(0, 0, value, 2)
        color(defaultColours.gameObject.brighter().brighter())
        rect(value, 0, 100 - value, 2)
        color(defaultColours.background)
        text("health", 2, 1, 1)
    }

    var value = 100

    override fun process() {

    }

    override fun isExpired(): Boolean = false
}