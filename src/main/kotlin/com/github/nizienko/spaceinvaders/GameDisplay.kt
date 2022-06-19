package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.GameObject
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.JPanel
import kotlin.math.roundToInt


class GameDisplay(val gameWidth: Int, val gameHeight: Int) : JPanel() {
    init {
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {
                requestFocusInWindow()
            }

            override fun mousePressed(e: MouseEvent?) {
            }

            override fun mouseReleased(e: MouseEvent?) {
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
            }
        })
    }

    private val gameObjects = mutableListOf<GameObject>()

    private val xMultiplier: Double
        get() = width.toDouble() / gameWidth.toDouble()
    private val yMultiplier: Double
        get() = height.toDouble() / gameHeight.toDouble()

    fun addObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
    }

    fun removeObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
    }
    var defaultColors = Colors(1)

    override fun paint(g: Graphics?) {
        if (g != null && g is Graphics2D) {
            g.background = defaultColors.background
            g.clearRect(0, 0, width, height)
            gameObjects.forEach {
                val realX = it.position.x.toDouble() * xMultiplier
                val realY = it.position.y.toDouble() * yMultiplier
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
}



