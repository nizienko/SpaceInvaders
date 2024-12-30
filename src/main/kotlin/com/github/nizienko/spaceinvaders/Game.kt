package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.*
import com.github.nizienko.spaceinvaders.objects.bonuses.Bonus
import com.github.nizienko.spaceinvaders.objects.bonuses.Healer
import com.github.nizienko.spaceinvaders.objects.bonuses.HealerFull
import java.awt.Component
import java.awt.Point
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.random.Random

class Game {
    companion object {
        const val FRAME_RATE = 30
        fun millisToFrames(millis: Long) = millis * (FRAME_RATE.toDouble() / 1000.0)
    }

    val display: GameDisplay = GameDisplay(1500, 1500).apply {
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
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_LEFT) {
                    println("KEY_PRESSED")
                    if (gameState == GameState.PLAY || gameState == GameState.WIN) {
                        spaceShip.moveLeft()
                    }
                }
                if (e.keyCode == KeyEvent.VK_RIGHT) {
                    if (gameState == GameState.PLAY || gameState == GameState.WIN) {
                        spaceShip.moveRight()
                    }
                }
                if (e.keyCode == KeyEvent.VK_UP || e.keyCode == KeyEvent.VK_X) {
                    if (gameState == GameState.PLAY) {
                        spaceShip.fire(true)
                    }
                }
                if (e.keyCode == KeyEvent.VK_SPACE) {
                    spacePressed()
                }
            }

            override fun keyReleased(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_LEFT) {
                    spaceShip.stopLeft()
                }
                if (e.keyCode == KeyEvent.VK_RIGHT) {
                    spaceShip.stopRight()
                }
                if (e.keyCode == KeyEvent.VK_UP || e.keyCode == KeyEvent.VK_X) {
                    spaceShip.fire(false)
                }
            }
        })
    }

    private var gameState: GameState = GameState.NEW
    private var invadersMovements = InvadersMovements()
    private val invaders = GameObjects<Invader>(display)
    private val spaceShipBullets = GameObjects<Bullet>(display)
    private val invadersBullets = GameObjects<Bullet>(display)
    private val bonuses = GameObjects<Bonus>(display)
    private val animations = GameObjects<Animation>(display)
    private lateinit var health: ProgressLevel
//    private lateinit var bulletSpeed: ProgressLevel

    private lateinit var spaceShip: SpaceShip

    private var gameOverTime: Long = 0L
    private var level = 1
    private lateinit var colors: Colors
    private val centerText = Text(display.gameWidth / 2, 400, 100, 170)
    private val bottomText = Text(display.gameWidth / 2, 600, 100, 90)
    private val bottomText2 = Text(display.gameWidth / 2, 800, 100, 90)
    private val levelText = Text(display.gameWidth / 2, 40, 100, 40)
    private val killedCounterText = Text(display.gameWidth - (display.gameWidth / 12) * 4, 40, 100, 40)
    private val killedRecordText = Text(display.gameWidth - (display.gameWidth / 12) * 1, 40, 100, 40)
    private var killedCount = 0
    private var killedRecord = 0

    inner class GameModifiers {
        var spaceShipHealth: Int
            get() = health.value
            set(value) {
                health.value = value
            }

//        var spaceShipBulletSpeed
//            get() = bulletSpeed.value
//            set(value) {
//                bulletSpeed.value = value
//            }

        fun shakeCamera(value: Double) {
            display.camera.zoom -= value
        }
    }

    private val gameModifiers = GameModifiers()


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
        health = ProgressLevel("health", Point(300, 20), 100, 0, 100)
//        bulletSpeed = ProgressLevel("bullet speed", Point(300, 20), 20, 20, 60)
        level = 0
        killedCount = 0
        killedRecord = SpaceInvadersState.getInstance().recordKills
        prepareNewLevel()
    }

    private fun prepareNewLevel() {
        level++
        display.clean()
        display.addObject(health, false)
//        display.addObject(bulletSpeed, false)
        display.addObject(killedCounterText, false)
        display.addObject(killedRecordText, false)
        with(display.camera) {
            zoom = 1.8
            zoomTarget = 1.0
        }
        colors = Colors(level)
        health.defaultColours = colors
//        bulletSpeed.defaultColours = colors
        centerText.defaultColours = colors
        levelText.defaultColours = colors
        bottomText.defaultColours = colors
        bottomText2.defaultColours = colors
        killedCounterText.defaultColours = colors
        killedRecordText.defaultColours = colors
        display.addObject(levelText, false)
        levelText.text = "level $level"
        bottomText.text = "press space to start"
        bottomText2.text = "move: ← →  fire: ↑ or x"
        killedCounterText.text = "killed: $killedCount"
        killedRecordText.text = "record: $killedRecord"
        invadersMovements = InvadersMovements()
        invaders.apply {
            clear()
            var n = 1
            (0..10).forEach { c ->
                (0..4).forEach { r ->
                    val invader = Invader(c * 100, r * 100 + 100, n)
                    n++
                    invader.move(invadersMovements.getPoint())
                    invader.totalInvadersLeft = invaders.size
                    invader.defaultColours = colors
                    invader.fire = { x, y ->
                        val bullet = Bullet(x, y, true, display.gameHeight, 20)
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
        bonuses.clear()
        spaceShip = SpaceShip(750, 1400, display.gameWidth).apply {
            fireFunction = { x, y ->
                val bullet = Bullet(x, y - spaceShip.height / 2)
//                bullet.defaultColours = colors
                spaceShipBullets.add(bullet)
            }
            defaultColours = colors
        }
        display.camera.process(spaceShip.position)

        display.addObject(spaceShip)
        display.defaultColors = colors
        display.addObject(centerText, false)
        display.addObject(bottomText, false)
        display.addObject(bottomText2, false)
        gameState = GameState.NEW
    }

    private var lastTimeBulletSpeedDecreased = System.currentTimeMillis()
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
        bonuses.forEach { it.process() }
        spaceShip.process()
        display.camera.process(spaceShip.position)
        // check collisions
        spaceShipBullets.forEach { b ->
            invaders.forEach { i ->
                if (b.isMatchInvader(i)) {
                    b.isOut = true
                    i.isKilled = true
                    killedCount++
                    val invadersDeathAnimation = InvadersDeathAnimation(b.position.x, b.position.y)
                    invadersDeathAnimation.defaultColours = colors
                    animations.add(invadersDeathAnimation)
                    display.camera.zoom += 0.7 / invaders.size.toDouble() / 15.0
                    killedCounterText.text = "killed: $killedCount"
                    if (killedCount > killedRecord) {
                        SpaceInvadersState.getInstance().recordKills = killedCount
                    }
                    if (Random.nextInt(55) == 1) {
                        bonuses.add(Healer(i.position.x, i.position.y, display.gameHeight))
                    } else if ((Random.nextInt(90) == 1)) {
                        bonuses.add(HealerFull(i.position.x, i.position.y, display.gameHeight))
                    }
//                    if (Random.nextInt(45) == 1) {
//                        bonuses.add(BulletSpeed(i.position.x, i.position.y, display.gameHeight))
//                    }
                }
            }
        }
        invadersBullets.forEach { b ->
            if (spaceShip.isObjectHit(b)) {
                val spaceShipExplosion = SpaceShipExplosionAnimation(b.position.x, b.position.y)
                animations.add(spaceShipExplosion)
                display.camera.zoom += 0.01
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
        bonuses.forEach { h ->
            if (spaceShip.isObjectHit(h)) {
                h.applyBonus(gameModifiers)
                h.setOut()
            }
        }
        animations.forEach { it.process() }

//        if (lastTimeBulletSpeedDecreased + 10_000 < System.currentTimeMillis()) {
//            lastTimeBulletSpeedDecreased = System.currentTimeMillis()
//            bulletSpeed.value -= 2
//        }

        // clean
        invadersBullets.deleteExpired()
        invaders.deleteExpired()
        animations.deleteExpired()
        bonuses.deleteExpired()
        spaceShipBullets.deleteExpired()

        // set speed
        invadersMovements.xSpeed = 5 - (invaders.count().toDouble() / 10) + level
        spaceShip.shootLag = getShootLag(level)
        // win?
        if (invaders.isEmpty()) {
            gameState = GameState.WIN
            display.camera.zoomTarget = 1.8
        }
        // lost?
        if (health.value <= 0) gameOver()
    }

    fun startGame() {
        thread(name = "SpaceInvaders-Game") {
            while (gameState != GameState.EXITED) {
                when (gameState) {
                    GameState.PLAY, GameState.GAME_OVER, GameState.WIN -> process()
                    else -> {}
                }
                Thread.sleep(1000L / (FRAME_RATE))
            }
        }
        thread(name = "SpaceInvaders-UI") {
            while (gameState != GameState.EXITED) {
                when (gameState) {
                    GameState.NEW -> {
                        centerText.text = "level $level"
                        bottomText.text = "press SPACE"
                        display.repaint()
                    }

                    GameState.PLAY -> {
                        centerText.text = ""
                        bottomText.text = ""
                        bottomText2.text = ""
                        display.repaint()
                    }

                    GameState.PAUSE -> {
                        bottomText2.text = "pause"
                        display.repaint()
                    }

                    GameState.GAME_OVER -> {
                        centerText.text = "game over"
                        bottomText.text = "$killedCount killed"
                        display.repaint()
                    }

                    GameState.WIN -> {
                        centerText.text = "win!"
                        bottomText.text = "press SPACE for next level"
                        display.repaint()
                    }

                    GameState.EXITED -> break
                }
                val sleep = 1000L / FRAME_RATE
                Thread.sleep(sleep)
            }
        }
    }

    fun exitGame() {
        gameState = GameState.EXITED
    }

    private fun gameOver() {
        gameState = GameState.GAME_OVER
        display.camera.zoomTarget = 0.6
    }

    private fun getShootLag(level: Int): Int {
        if (level < 5) return 28
        if (level < 8) return 20
        if (level < 10) return 15
        if (level < 13) return 10
        if (level < 15) return 13
        if (level < 30) return 10
        return 5
    }
}

fun main() {
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
    private val list = mutableSetOf<T>()

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