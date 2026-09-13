package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.objects.*
import com.github.nizienko.spaceinvaders.objects.bonuses.Bonus
import com.github.nizienko.spaceinvaders.objects.bonuses.Healer
import com.github.nizienko.spaceinvaders.objects.bonuses.HealerFull
import java.awt.Component
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.Timer
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
    private var gameTimer: Timer? = null
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
        display.onThemeChanged = ::refreshColorsForCurrentTheme
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
        health = ProgressLevel("health", 300, 20, 100, 0, 100)
//        bulletSpeed = ProgressLevel("bullet speed", 300, 20, 20, 20, 60)
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
        refreshColorsForCurrentTheme()
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
                    invader.move(invadersMovements.x, invadersMovements.y)
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
                bullet.defaultColours = colors
                spaceShipBullets.add(bullet)
            }
            defaultColours = colors
        }
        display.camera.process(spaceShip.x, spaceShip.y)

        display.addObject(spaceShip)
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
        val invadersOriginX = invadersMovements.x
        val invadersOriginY = invadersMovements.y
        invaders.forEach {
            it.move(invadersOriginX, invadersOriginY)
            it.process()
            it.totalInvadersLeft = invaders.size
        }
        bonuses.forEach { it.process() }
        spaceShip.process()
        display.camera.process(spaceShip.x, spaceShip.y)
        // check collisions
        for (bullet in spaceShipBullets) {
            for (invader in invaders) {
                if (!invader.isKilled && bullet.isMatchInvader(invader)) {
                    bullet.isOut = true
                    invader.isKilled = true
                    killedCount++
                    val invadersDeathAnimation = InvadersDeathAnimation(bullet.x, bullet.y)
                    invadersDeathAnimation.defaultColours = colors
                    animations.add(invadersDeathAnimation)
                    display.camera.zoom += 0.7 / invaders.size.toDouble() / 15.0
                    killedCounterText.text = "killed: $killedCount"
                    if (killedCount > killedRecord) {
                        SpaceInvadersState.getInstance().recordKills = killedCount
                    }
                    if (Random.nextInt(55) == 1) {
                        bonuses.add(Healer(invader.x, invader.y, display.gameHeight).apply { defaultColours = colors })
                    } else if ((Random.nextInt(90) == 1)) {
                        bonuses.add(HealerFull(invader.x, invader.y, display.gameHeight).apply { defaultColours = colors })
                    }
//                    if (Random.nextInt(45) == 1) {
//                        bonuses.add(BulletSpeed(invader.x, invader.y, display.gameHeight))
//                    }
                    break
                }
            }
        }
        invadersBullets.forEach { b ->
            if (spaceShip.isObjectHit(b)) {
                val spaceShipExplosion = SpaceShipExplosionAnimation(b.x, b.y)
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
        if (invaders.any { it.y > spaceShip.y - spaceShip.height / 2 }) {
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
        invadersMovements.xSpeed = 5 - (invaders.size.toDouble() / 10) + level
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
        runOnEdt {
            if (gameState == GameState.EXITED || gameTimer?.isRunning == true) return@runOnEdt

            gameTimer = Timer(1000 / FRAME_RATE) {
                processFrame()
            }.apply {
                isCoalesce = true
                start()
            }
        }
    }

    fun exitGame() {
        runOnEdt {
            gameState = GameState.EXITED
            gameTimer?.stop()
            gameTimer = null
            display.dispose()
        }
    }

    private fun refreshColorsForCurrentTheme() {
        if (level == 0) return

        colors = Colors(level)
        display.defaultColors = colors
        health.defaultColours = colors
        centerText.defaultColours = colors
        levelText.defaultColours = colors
        bottomText.defaultColours = colors
        bottomText2.defaultColours = colors
        killedCounterText.defaultColours = colors
        killedRecordText.defaultColours = colors
        invaders.forEach { it.defaultColours = colors }
        spaceShipBullets.forEach { it.defaultColours = colors }
        invadersBullets.forEach { it.defaultColours = colors }
        bonuses.forEach { it.defaultColours = colors }
        animations.forEach { it.defaultColours = colors }
        if (::spaceShip.isInitialized) spaceShip.defaultColours = colors
    }

    private fun processFrame() {
        check(SwingUtilities.isEventDispatchThread()) { "Game frames must run on the EDT" }

        when (gameState) {
            GameState.PLAY, GameState.GAME_OVER, GameState.WIN -> process()
            else -> {}
        }
        updateUi()
        display.repaint()
    }

    private fun updateUi() {
        when (gameState) {
            GameState.NEW -> {
                centerText.text = "level $level"
                bottomText.text = "press SPACE"
            }

            GameState.PLAY -> {
                centerText.text = ""
                bottomText.text = ""
                bottomText2.text = ""
            }

            GameState.PAUSE -> bottomText2.text = "pause"

            GameState.GAME_OVER -> {
                centerText.text = "game over"
                bottomText.text = "$killedCount killed"
            }

            GameState.WIN -> {
                centerText.text = "win!"
                bottomText.text = "press SPACE for next level"
            }

            GameState.EXITED -> {}
        }
    }

    private fun runOnEdt(action: () -> Unit) {
        if (SwingUtilities.isEventDispatchThread()) {
            action()
        } else {
            SwingUtilities.invokeLater(action)
        }
    }

    private fun gameOver() {
        gameState = GameState.GAME_OVER
        display.camera.zoomTarget = 0.6
    }

    private fun getShootLag(level: Int): Int {
        if (level < 5) return 28
        if (level < 8) return 20
        if (level < 10) return 15
        if (level < 13) return 12
        if (level < 15) return 10
        if (level < 22) return 8
        return 4
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val game = Game()
        createDemoUI(game.display)
        game.startGame()
    }
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
    private var rawX: Double = 101.0
    private var rawY: Double = 101.0
    val x: Int
        get() = rawX.roundToInt()
    val y: Int
        get() = rawY.roundToInt()
    var xSpeed: Double = 4.0
    private val ySpeed: Int = 25

    private val minX = 100
    private val maxX = 400

    private var direction = 1

    fun move() {
        if (rawX >= maxX) {
            direction = -1
            rawY += ySpeed
        }
        if (rawX <= minX) {
            direction = 1
            rawY += ySpeed
        }
        rawX += direction * xSpeed
    }
}

enum class GameState {
    NEW, PLAY, PAUSE, GAME_OVER, WIN, EXITED
}

interface CanExpired {
    fun isExpired(): Boolean
}

class GameObjects<T : GameObject>(private val display: GameDisplay) : Iterable<T> {
    private val list = ArrayList<T>()

    fun clear() {
        list.forEach(display::removeObject)
        list.clear()
    }
    val size
        get() = list.size

    fun add(obj: T) {
        if (obj !in list) {
            list.add(obj)
            display.addObject(obj)
        }
    }

    fun deleteExpired() {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val obj = iterator.next()
            if (obj.isExpired()) {
                iterator.remove()
                display.removeObject(obj)
            }
        }
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
