package com.github.nizienko.spaceinvaders.objects

import java.awt.Point

class ProgressLevel(private val text: String, position: Point, initValue: Int, private val minValue: Int, private val maxValue: Int) : GameObject() {
    override val position: Point = position
    override val width: Int = 600
    override val height: Int = 45
    override val pictureSizeX: Int = 100
    override val pictureSizeY: Int = 2
    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject)
        rect(0, 0, value, 2)
        color(defaultColours.gameObject.brighter().brighter())
        rect(value, 0, maxValue - value, 2)
        color(defaultColours.background)
        text(text, 2, 1, 1, false)
    }

    private var _value = initValue
    var value
        get() = _value
        set(value) {
            if (value < minValue) {
                _value = minValue
            } else if (value > maxValue) {
                _value = maxValue
            } else {
                _value = value
            }
        }

    override fun process() {

    }

    override fun isExpired(): Boolean = false
}