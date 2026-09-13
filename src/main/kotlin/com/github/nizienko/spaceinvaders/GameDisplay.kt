package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeListener
import javax.swing.JPanel
import javax.swing.UIManager
import kotlin.math.roundToInt


class GameDisplay(val gameWidth: Int, val gameHeight: Int) : JPanel() {
    var onThemeChanged: (() -> Unit)? = null
    private val themeChangeListener = PropertyChangeListener { event ->
        if (event.propertyName == "lookAndFeel") {
            onThemeChanged?.invoke()
            repaint()
        }
    }

    init {
        UIManager.addPropertyChangeListener(themeChangeListener)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                requestFocusInWindow()
            }
        })
    }

    // All game state is updated and painted on the EDT, so ordered mutable lists are sufficient.
    private val gameObjects = ArrayList<GameObject>()
    private val viewObjects = ArrayList<GameObject>()

    private val xMultiplier: Double
        get() = (width.toDouble() / gameWidth.toDouble())
    private val yMultiplier: Double
        get() = (height.toDouble() / gameHeight.toDouble())

     fun addObject(gameObject: GameObject, zoomable: Boolean = true) {
        val objects = if (zoomable) gameObjects else viewObjects
        if (gameObject !in objects) {
            objects.add(gameObject)
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
                val realX = (it.x - camera.x).toDouble() * xMultiplier * camera.zoom
                val realY = (it.y - camera.y).toDouble() * yMultiplier * camera.zoom
                val realWidth = it.width.toDouble() * xMultiplier * camera.zoom
                val realHeight = it.height.toDouble() * yMultiplier * camera.zoom
                val objectWidth = realWidth.roundToInt()
                val objectHeight = realHeight.roundToInt()
                val objectX = realX.roundToInt() - objectWidth / 2
                val objectY = realY.roundToInt() - objectHeight / 2
                if (objectX < width && objectY < height && objectX + objectWidth > 0 && objectY + objectHeight > 0) {
                    it.paint(g, objectX, objectY, objectWidth, objectHeight)
                }
            }
            viewObjects.forEach {
                val realX = it.x.toDouble() * xMultiplier
                val realY = it.y.toDouble() * yMultiplier
                val realWidth = it.width.toDouble() * xMultiplier
                val realHeight = it.height.toDouble() * yMultiplier
                val objectWidth = realWidth.roundToInt()
                val objectHeight = realHeight.roundToInt()
                val objectX = realX.roundToInt() - objectWidth / 2
                val objectY = realY.roundToInt() - objectHeight / 2
                if (objectX < width && objectY < height && objectX + objectWidth > 0 && objectY + objectHeight > 0) {
                    it.paint(g, objectX, objectY, objectWidth, objectHeight)
                }
            }
        }
    }

    fun clean() {
        gameObjects.clear()
        viewObjects.clear()
    }

    fun dispose() {
        UIManager.removePropertyChangeListener(themeChangeListener)
    }

    val camera = Camera(gameWidth, gameHeight)

    class Camera(private val gameWidth: Int, private val gameHeight: Int) {
        var x: Int = 0
            private set
        var y: Int = 0
            private set
        var zoom: Double = 1.0
        var zoomTarget = 1.0
        val width
            get() = (gameWidth.toDouble() / zoom).roundToInt()
        val height
            get() = (gameHeight.toDouble() / zoom).roundToInt()


        fun process(positionX: Int, positionY: Int) {
            var newX = positionX - width / 2
            var newY = positionY - height / 2
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

