package com.github.nizienko.spaceinvaders.objects

import com.github.nizienko.spaceinvaders.CanExpired
import com.github.nizienko.spaceinvaders.Colors
import java.awt.*
import kotlin.math.roundToInt

abstract class GameObject : CanExpired {
    abstract val x: Int
    abstract val y: Int
    abstract val width: Int
    abstract val height: Int
    abstract val pictureSizeX: Int
    abstract val pictureSizeY: Int
    abstract val picture: Painter.() -> Unit
    abstract fun process()
    var defaultColours = Colors(1)

    private val painter by lazy { Painter(pictureSizeX, pictureSizeY) }

    fun paint(graphics: Graphics2D, x: Int, y: Int, width: Int, height: Int) {
        painter.reset(graphics, x, y, width, height)
        painter.picture()
    }
}


class Painter(
    sizeX: Int,
    sizeY: Int,
) {
    private val sizeX = sizeX.toDouble()
    private val sizeY = sizeY.toDouble()
    private lateinit var graphics: Graphics2D
    private var positionX = 0
    private var positionY = 0
    private var xMultiplier = 1.0
    private var yMultiplier = 1.0
    private var polygonXs = IntArray(0)
    private var polygonYs = IntArray(0)

    fun reset(graphics: Graphics2D, x: Int, y: Int, width: Int, height: Int) {
        this.graphics = graphics
        positionX = x
        positionY = y
        xMultiplier = width.toDouble() / sizeX
        yMultiplier = height.toDouble() / sizeY
    }

    fun color(color: Color) {
        graphics.color = color
    }

    fun rect(x: Int, y: Int, width: Int, height: Int) {
        graphics.fillRect(
            positionX + (x * xMultiplier).roundToInt(),
            positionY + (y * yMultiplier).roundToInt(),
            (width * xMultiplier).roundToInt(),
            (height * yMultiplier).roundToInt()
        )
    }

    fun polygon(points: IntArray) {
        val pointCount = points.size / 2
        if (polygonXs.size < pointCount) {
            polygonXs = IntArray(pointCount)
            polygonYs = IntArray(pointCount)
        }
        for (index in 0 until pointCount) {
            polygonXs[index] = positionX + (points[index * 2] * xMultiplier).roundToInt()
            polygonYs[index] = positionY + (points[index * 2 + 1] * yMultiplier).roundToInt()
        }
        graphics.fillPolygon(
            polygonXs,
            polygonYs,
            pointCount
        )
    }

    fun oval(x: Int, y: Int, width: Int, height: Int) {
        val w = (width * xMultiplier).roundToInt()
        val h = (height * yMultiplier).roundToInt()
        graphics.fillOval(
            positionX + (x * xMultiplier).roundToInt() - w / 2,
            positionY + (y * yMultiplier).roundToInt() - h / 2,
            w,
            h
        )
    }

    fun stroke(stroke: Stroke) {
        graphics.stroke = stroke
    }

    fun arc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
        val w = (width * xMultiplier).roundToInt()
        val h = (height * yMultiplier).roundToInt()
        graphics.drawArc(
            positionX + (x * xMultiplier).roundToInt() - w / 2,
            positionY + (y * yMultiplier).roundToInt() - h / 2,
            w,
            h,
            startAngle,
            arcAngle
        )
    }

    fun text(text: String, x: Int, y: Int, font: Int, center: Boolean = true) {
        val fontSize = (font * yMultiplier).roundToInt()
        graphics.font = graphics.font.deriveFont(fontSize.toFloat())
        val textWidth: Int = graphics.fontMetrics.stringWidth(text)
        val gx = if (center) {
            positionX + (x * xMultiplier).roundToInt() - textWidth / 2
        } else {
            positionX + (x * xMultiplier).roundToInt()
        }
        val gy = positionY + (y * yMultiplier).roundToInt() + fontSize / 2
        graphics.drawString(text, gx, gy)
    }
}
