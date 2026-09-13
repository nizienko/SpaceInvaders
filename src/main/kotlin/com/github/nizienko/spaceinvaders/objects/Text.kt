package com.github.nizienko.spaceinvaders.objects

class Text(x: Int, y: Int, width: Int, height: Int): GameObject() {
    override val x = x
    override val y = y
    override val width = width
    override val height = height
    override val pictureSizeX: Int = 1
    override val pictureSizeY: Int = 1
    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObjectBrighter)
        text(text, 0, 0, 1)
    }
    var text = ""

    override fun process() {

    }

    override fun isExpired(): Boolean = false
}
