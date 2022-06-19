package com.github.nizienko.spaceinvaders.objects

import java.awt.Point

class Text(x: Int, y: Int, width: Int, height: Int): GameObject() {
    override val position: Point = Point(x, y)
    override val width = width
    override val height = height
    override val pictureSizeX: Int = 1
    override val pictureSizeY: Int = 1
    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject.brighter())
        text(text, 0, 0, 1)
    }
    var text = ""

    override fun process() {

    }

    override fun isExpired(): Boolean = false
}