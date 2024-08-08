package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.GameObject
import com.jetbrains.rd.util.ConcurrentHashMap
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import kotlin.math.roundToInt


class GameDisplay(val gameWidth: Int, val gameHeight: Int) : JPanel() {
    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                requestFocusInWindow()
            }
        })
    }

    // this is fast but error can occur
    private val gameObjects =  ConcurrentHashMap.newKeySet<GameObject>()//mutableSetOf<GameObject>()
    private val viewObjects = ConcurrentHashMap.newKeySet<GameObject>()

    private val xMultiplier: Double
        get() = (width.toDouble() / gameWidth.toDouble())
    private val yMultiplier: Double
        get() = (height.toDouble() / gameHeight.toDouble())

     fun addObject(gameObject: GameObject, zoomable: Boolean = true) {
        if (zoomable) {
            gameObjects.add(gameObject)
        } else {
            viewObjects.add(gameObject)
        }
    }

    fun removeObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
        viewObjects.remove(gameObject)
    }

    var defaultColors = Colors(1)

    override fun paintComponent(g: Graphics?) {
        if (g != null && g is Graphics2D) {
            g.background = defaultColors.background
            g.clearRect(0, 0, width, height)
            gameObjects.forEach {
                val realX = (it.position.x - camera.position.x).toDouble() * xMultiplier * camera.zoom
                val realY = (it.position.y - camera.position.y).toDouble() * yMultiplier * camera.zoom
                val realWidth = it.width.toDouble() * xMultiplier * camera.zoom
                val realHeight = it.height.toDouble() * yMultiplier * camera.zoom
                it.paint(
                    g,
                    Point(
                        realX.roundToInt() - (realWidth / 2).roundToInt(),
                        realY.roundToInt() - (realHeight / 2).roundToInt()
                    ),
                    realWidth.roundToInt(),
                    realHeight.roundToInt()
                )
            }
            viewObjects.forEach {
                val realX = (it.position.x).toDouble() * xMultiplier
                val realY = (it.position.y).toDouble() * yMultiplier
                val realWidth = it.width.toDouble() * xMultiplier
                val realHeight = it.height.toDouble() * yMultiplier
                it.paint(
                    g,
                    Point(
                        realX.roundToInt() - (realWidth / 2).roundToInt(),
                        realY.roundToInt() - (realHeight / 2).roundToInt()
                    ),
                    realWidth.roundToInt(),
                    realHeight.roundToInt()
                )
            }
        }
    }

    fun clean() {
        gameObjects.clear()
    }

    val camera = Camera(gameWidth, gameHeight)

    class Camera(private val gameWidth: Int, private val gameHeight: Int) {
        val position: Point
            get() = Point(x, y)
        private var x: Int = 0
        private var y: Int = 0
        var zoom: Double = 1.0
        var zoomTarget = 1.0
        val width
            get() = (gameWidth.toDouble() / zoom).roundToInt()
        val height
            get() = (gameHeight.toDouble() / zoom).roundToInt()


        fun process(position: Point) {
            var newX = position.x - width / 2
            var newY = position.y - height / 2
            if (newX < 0) newX = 0
            if (newX + width > gameWidth) newX = gameWidth - width
            if (newY < 0) newY = 0
            if (newY + height > 0) newY = gameHeight - height
            x = newX
            y = newY

            if (zoom > zoomTarget) {
                val step = (zoom - zoomTarget) / 10.0
                zoom -= step
            }
            if (zoom < zoomTarget) {
                val step = (zoomTarget - zoom) / 10.0
                zoom += step
            }
        }
    }
}



