package com.github.nizienko.spaceinvaders.objects

import com.github.nizienko.spaceinvaders.CanExpired
import com.github.nizienko.spaceinvaders.Colors
import java.awt.*
import kotlin.math.roundToInt

abstract class GameObject : CanExpired {
    abstract val position: Point
    abstract val width: Int
    abstract val height: Int
    abstract val pictureSizeX: Int
    abstract val pictureSizeY: Int
    abstract val picture: Painter.() -> Unit
    abstract fun process()
    var defaultColours = Colors(1)

    fun paint(graphics: Graphics2D, position: Point, width: Int, height: Int) {
        val painter = Painter(pictureSizeX, pictureSizeY, graphics, position, width, height)
        painter.picture()
    }
}


class Painter(
    sizeX: Int,
    sizeY: Int,
    private val graphics: Graphics2D,
    private val position: Point,
    private val width: Int,
    private val height: Int
) {
    private val xMultiplier = this.width.toDouble() / sizeX.toDouble()
    private val yMultiplier = this.height.toDouble() / sizeY.toDouble()

    fun color(color: Color) {
        graphics.color = color
    }

    fun rect(x: Int, y: Int, width: Int, height: Int) {
        graphics.fillRect(
            position.x + (x * xMultiplier).roundToInt(),
            position.y + (y * yMultiplier).roundToInt(),
            (width * xMultiplier).roundToInt(),
            (height * yMultiplier).roundToInt()
        )
    }

    fun polygon(vararg points: Point) {
        val multipliedPoints = points.map {
            Point(
                position.x + (it.x * xMultiplier).roundToInt(),
                position.y + (it.y * yMultiplier).roundToInt()
            )
        }
        graphics.fillPolygon(
            multipliedPoints.map { it.x }.toIntArray(),
            multipliedPoints.map { it.y }.toIntArray(),
            multipliedPoints.size
        )
    }

    fun oval(x: Int, y: Int, width: Int, height: Int) {
        val w = (width * xMultiplier).roundToInt()
        val h = (height * yMultiplier).roundToInt()
        graphics.fillOval(
            position.x + (x * xMultiplier).roundToInt() - w / 2,
            position.y + (y * yMultiplier).roundToInt() - h / 2,
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
            position.x + (x * xMultiplier).roundToInt() - w / 2,
            position.y + (y * yMultiplier).roundToInt() - h / 2,
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
            position.x + (x * xMultiplier).roundToInt() - textWidth / 2
        } else {
            position.x + (x * xMultiplier).roundToInt()
        }
        val gy = position.y + (y * yMultiplier).roundToInt() + fontSize / 2
        graphics.drawString(text, gx, gy)
    }
}