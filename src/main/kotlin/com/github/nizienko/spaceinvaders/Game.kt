package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.*
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.Point
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.JFrame
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.random.Random

class Game {
    companion object {
        const val FRAME_RATE = 30
        fun millisToFrames(millis: Long) = millis * (FRAME_RATE.toDouble() / 1000.0)
    }

    val display: GameDisplay = GameDisplay(1600, 1400).apply {
        font = UIUtil.getLabelFont(UIUtil.FontSize.NORMAL)
        addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) {
            }

            override fun focusLost(e: FocusEvent?) {
                if (gameState == GameState.PLAY) {
                    gameState = GameState.PAUSE
                }
            }
        })
        isFocusable = true
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e != null) {
                    if (e.keyCode == KeyEvent.VK_LEFT) {
                        if (gameState == GameState.PLAY || gameState == GameState.WIN) {
                            spaceShip.moveLeft()
                        }
                    }
                    if (e.keyCode == KeyEvent.VK_RIGHT) {
                        if (gameState == GameState.PLAY || gameState == GameState.WIN) {
                            spaceShip.moveRight()
                        }
                    }
                    if (e.keyCode == KeyEvent.VK_UP) {
                        if (gameState == GameState.PLAY) {
                            spaceShip.fire()
                        }
                    }
                    if (e.keyCode == KeyEvent.VK_SPACE) {
                        spacePressed()
                    }
                }
            }
        })
    }

    private var gameState: GameState = GameState.NEW
    private var invadersMovements = InvadersMovements()
    private val invaders = GameObjects<Invader>(display)
    private val spaceShipBullets = GameObjects<Bullet>(display)
    private val invadersBullets = GameObjects<Bullet>(display)
    private val healers = GameObjects<Healer>(display)
    private val animations = GameObjects<Animation>(display)
    private lateinit var health: HealthLevel

    private lateinit var spaceShip: SpaceShip

    private var gameOverTime: Long = 0L
    private var level = 1
    private lateinit var colors: Colors
    private val centerText = Text(100, 400, 100, 170)
    private val bottomText = Text(100, 600, 100, 90)
    private val bottomText2 = Text(100, 800, 100, 90)
    private val levelText = Text(700, 40, 100, 40)
    private val killedCounterText = Text(1150, 40, 100, 40)
    private val killedRecordText = Text(1400, 40, 100, 40)
    private var killedCount = 0
    private var killedRecord = 0

    init {
        prepareNewGame()
    }

    private fun spacePressed() {
        when (gameState) {
            GameState.NEW -> gameState = GameState.PLAY
            GameState.PLAY -> gameState = GameState.PAUSE
            GameState.PAUSE -> gameState = GameState.PLAY
            GameState.GAME_OVER -> prepareNewGame()
            GameState.WIN -> prepareNewLevel()
            GameState.EXITED -> {}
        }
    }

    private fun prepareNewGame() {
        health = HealthLevel()
        level = 0
        killedCount = 0
        killedRecord = SpaceInvadersState.getInstance().recordKills
        prepareNewLevel()
    }

    private fun prepareNewLevel() {
        level++
        display.clean()
        display.addObject(health, false)
        display.addObject(killedCounterText, false)
        display.addObject(killedRecordText, false)
        with(display.camera) {
            zoom = 1.7
            zoomTarget = 1.0
        }
        colors = Colors(level)
        health.defaultColours = colors
        centerText.defaultColours = colors
        levelText.defaultColours = colors
        bottomText.defaultColours = colors
        bottomText2.defaultColours = colors
        killedCounterText.defaultColours = colors
        killedRecordText.defaultColours = colors
        display.addObject(levelText, false)
        levelText.text = "level $level"
        bottomText.text = "press space to start"
        bottomText2.text = "move: ??? ???  fire: ???"
        killedCounterText.text = "killed: $killedCount"
        killedRecordText.text = "record: $killedRecord"
        invadersMovements = InvadersMovements()
        invaders.apply {
            clear()
            (0..11).forEach { c ->
                (0..4).forEach { r ->
                    val invader = Invader(c * 100, r * 100)
                    invader.move(invadersMovements.getPoint())
                    invader.totalInvadersLeft = invaders.size
                    invader.defaultColours = colors
                    invader.fire = { x, y ->
                        val bullet = Bullet(x, y, true, display.gameHeight)
                        bullet.defaultColours = colors
                        invadersBullets.add(bullet)
                    }
                    add(invader)
                }
            }
        }
        spaceShipBullets.clear()
        invadersBullets.clear()
        animations.clear()
        healers.clear()
        spaceShip = SpaceShip(800, 1300, display.gameWidth).apply {
            fireFunction = { x, y ->
                val bullet = Bullet(x, y - spaceShip.height / 2)
                bullet.defaultColours = colors
                spaceShipBullets.add(bullet)
            }
            defaultColours = colors
            this.totalKilled = killedCount
        }
        display.camera.process(spaceShip.position)

        display.addObject(spaceShip)
        display.defaultColors = colors
        display.addObject(centerText, false)
        display.addObject(bottomText, false)
        display.addObject(bottomText2, false)
        gameState = GameState.NEW
    }


    private fun process() {
        // move objects
        spaceShipBullets.forEach {
            it.move()
            it.process()
        }
        invadersBullets.forEach {
            it.move()
            it.process()
        }
        invadersMovements.move()
        invaders.forEach {
            it.move(invadersMovements.getPoint())
            it.process()
            it.totalInvadersLeft = invaders.size
        }
        healers.forEach { it.process() }
        spaceShip.process()
        display.camera.process(spaceShip.position)
        // check collisions
        spaceShipBullets.forEach { b ->
            invaders.forEach { i ->
                if (b.isMatchInvader(i)) {
                    b.isOut = true
                    i.isKilled = true
                    killedCount++
                    spaceShip.totalKilled = killedCount
                    val invadersDeathAnimation = InvadersDeathAnimation(b.position.x, b.position.y)
                    invadersDeathAnimation.defaultColours = colors
                    animations.add(invadersDeathAnimation)
                    display.camera.zoom += 1.0 / invaders.size.toDouble() / 15.0
                    killedCounterText.text = "killed: $killedCount"
                    if (killedCount > killedRecord) {
                        SpaceInvadersState.getInstance().recordKills = killedCount
                    }
                    if (Random.nextInt(80) == 1) {
                        val heal = Healer(i.position.x, i.position.y, display.gameHeight)
                        healers.add(heal)
                    }
                }
            }
        }
        invadersBullets.forEach { b ->
            if (spaceShip.isObjectHit(b)) {
                val spaceShipExplosion = SpaceShipExplosionAnimation(b.position.x, b.position.y)
                animations.add(spaceShipExplosion)
                display.camera.zoom += 0.03
                b.isOut = true
                health.value -= 10
                if (health.value <= 0) {
                    spaceShip.isKilled = true
                    display.removeObject(spaceShip)
                    gameOverTime = System.currentTimeMillis()
                }
            }
        }
        if (invaders.any { it.position.y > spaceShip.position.y - spaceShip.height / 2 }) {
            if (gameState == GameState.GAME_OVER) {
                if (System.currentTimeMillis() - gameOverTime > 10000) {
                    prepareNewGame()
                }
            } else {
                gameOver()
                gameOverTime = System.currentTimeMillis()
            }
        }
        healers.forEach { h ->
            if (spaceShip.isObjectHit(h)) {
                health.value += 10
                display.camera.zoom -= 0.01
                if (health.value > 100) {
                    health.value = 100
                }
                h.isOut = true
            }
        }
        animations.forEach { it.process() }

        // clean
        invadersBullets.deleteExpired()
        invaders.deleteExpired()
        animations.deleteExpired()
        healers.deleteExpired()
        spaceShipBullets.deleteExpired()

        // set speed
        invadersMovements.xSpeed = 5 - (invaders.count().toDouble() / 10) + level

        // win?
        if (invaders.isEmpty()) {
            gameState = GameState.WIN
            display.camera.zoomTarget = 1.7
        }
        // lose?
        if (health.value <= 0) gameOver()
    }

    fun startGame() {
        thread {
            while (gameState != GameState.EXITED) {
                when (gameState) {
                    GameState.NEW -> {
                        centerText.text = "level $level"
                        bottomText.text = "press space to start"
                        display.repaint()
                        Thread.sleep(500)
                    }

                    GameState.PLAY -> {
                        centerText.text = ""
                        bottomText.text = ""
                        bottomText2.text = ""
                        process()
                        display.repaint()
                    }

                    GameState.PAUSE -> {
                        bottomText2.text = "pause"
                        display.repaint()
                        Thread.sleep(500)
                    }

                    GameState.GAME_OVER -> {
                        centerText.text = "game over"
                        bottomText.text = "$killedCount killed"
                        process()
                        display.repaint()
                    }

                    GameState.WIN -> {
                        centerText.text = "win!"
                        bottomText.text = "press space to next level"
                        process()
                        display.repaint()
                    }

                    GameState.EXITED -> break
                }
                Thread.sleep(1000L / FRAME_RATE)
            }
        }
    }

    fun exitGame() {
        gameState = GameState.EXITED
    }

    private fun gameOver() {
        gameState = GameState.GAME_OVER
        display.camera.zoomTarget = 0.8
    }
}

fun main(args: Array<String>) {
    val game = Game()
    createDemoUI(game.display)
    game.startGame()
}

fun createDemoUI(component: Component) {
    val frame = JFrame("Test")
    frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.setSize(800, 600)
    frame.add(component)
    frame.isVisible = true
}

class InvadersMovements {
    private var x: Double = 101.0
    private var y: Double = 101.0
    var xSpeed: Double = 4.0
    private val ySpeed: Int = 25

    private val minX = 100
    private val maxX = 400

    private var direction = 1

    fun getPoint() = Point(x.roundToInt(), y.roundToInt())

    fun move() {
        if (x >= maxX) {
            direction = -1
            y += ySpeed
        }
        if (x <= minX) {
            direction = 1
            y += ySpeed
        }
        x += direction * xSpeed
    }
}

enum class GameState {
    NEW, PLAY, PAUSE, GAME_OVER, WIN, EXITED
}

interface CanExpired {
    fun isExpired(): Boolean
}

class GameObjects<T : GameObject>(private val display: GameDisplay) : Iterable<T> {
    private val list = Collections.synchronizedList(mutableListOf<T>())

    fun clear() = list.clear()
    val size
        get() = list.size

    fun add(obj: T) {
        list.add(obj)
        display.addObject(obj)
    }

    fun deleteExpired() {
        val listToDelete = list.filter { it.isExpired() }
        listToDelete.forEach { remove(it) }
    }

    override fun iterator(): Iterator<T> {
        return list.iterator()
    }

    private fun remove(obj: T) {
        list.remove(obj)
        display.removeObject(obj)
    }

    fun isEmpty(): Boolean = list.isEmpty()
}