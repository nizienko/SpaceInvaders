package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.GameObject
import com.intellij.util.ConcurrencyUtil
import com.intellij.util.containers.ConcurrentList
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image.SCALE_SMOOTH
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedDeque
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min
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

    // this is fast but error can occur
    private val gameObjects = mutableListOf<GameObject>()
    private val viewObjects = mutableListOf<GameObject>()

    // this is slow
//    private val gameObjects = ConcurrentLinkedDeque<GameObject>()
//    private val viewObjects = ConcurrentLinkedDeque<GameObject>()
    private val xMultiplier: Double
        get() = (imageWidth.toDouble() / gameWidth.toDouble())
    private val yMultiplier: Double
        get() = (imageHeight.toDouble() / gameHeight.toDouble())

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
    private val imageWidth = 500
    private val imageHeight = 500
    private val gameImage = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
    private val imageGraphics = gameImage.createGraphics()
    override fun paint(g: Graphics?) {
        if (g != null && g is Graphics2D) {
            imageGraphics.background = defaultColors.background
            imageGraphics.clearRect(0, 0, imageWidth, imageHeight)
            gameObjects.forEach {
                val realX = (it.position.x - camera.position.x).toDouble() * xMultiplier * camera.zoom
                val realY = (it.position.y - camera.position.y).toDouble() * yMultiplier * camera.zoom
                val realWidth = it.width.toDouble() * xMultiplier * camera.zoom
                val realHeight = it.height.toDouble() * yMultiplier * camera.zoom
                it.paint(
                        imageGraphics,
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
                        imageGraphics,
                        Point(
                                realX.roundToInt() - (realWidth / 2).roundToInt(),
                                realY.roundToInt() - (realHeight / 2).roundToInt()
                        ),
                        realWidth.roundToInt(),
                        realHeight.roundToInt()
                )
            }
            g.drawImage(gameImage.getScaledInstance(width, height, SCALE_SMOOTH), 0, 0, null)
        }
    }

    private fun Color.isColorBright(): Boolean {
        // Calculate the luminance value
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255

        // Check if the luminance value is above a threshold
        val brightnessThreshold = 0.8 // Adjust the threshold as desired
        return luminance > brightnessThreshold
    }

    private fun applyOldTVFilter(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height

        val graphics = image.createGraphics()

        // Apply scan lines effect
        val scanLineHeight = 1 // Adjust the height of scan lines as desired

        val scanLineSpacing = 7 // Adjust the spacing between scan lines as desired

        if (defaultColors.background.isColorBright().not()) {
            var y = 0
            while (y < height) {
                graphics.color = defaultColors.background.darker().darker()
                graphics.fillRect(0, y, width, scanLineHeight)
                y += scanLineHeight + scanLineSpacing
            }
        }
        // Apply noise effect
        val noiseIntensity = 0.1 // Adjust the intensity of noise as desired

        for (y in 0 until height) {
            for (x in 0 until width) {
                val rgb = image.getRGB(x, y)
                val alpha = rgb shr 24 and 0xFF
                var red = rgb shr 16 and 0xFF
                var green = rgb shr 8 and 0xFF
                var blue = rgb and 0xFF
                val noise = (Math.random() * 256 * noiseIntensity).toInt()
                red = clamp(red + noise)
                green = clamp(green + noise)
                blue = clamp(blue + noise)
                val filteredRGB = alpha shl 24 or (red shl 16) or (green shl 8) or blue
                image.setRGB(x, y, filteredRGB)
            }
        }
        graphics.dispose()
        return image
    }

    private fun clamp(value: Int): Int {
        return max(0, min(255, value))
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



