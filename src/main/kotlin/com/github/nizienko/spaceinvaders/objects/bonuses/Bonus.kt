package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.GameObject
import java.awt.Point

abstract class Bonus(protected var positionX: Int, protected var positionY: Int, private val maxY: Int) : GameObject() {
    companion object {
        const val SPEED = 13
    }

    private var out = false

    override fun process() {
        positionY += SPEED
        if (positionY >= maxY) out = true
    }

    override val position: Point
        get() = Point(positionX, positionY)

    override fun isExpired(): Boolean = isOut()

    fun isOut(): Boolean = out

    fun setOut() { out = true}

    abstract fun applyBonus(gameModifiers: Game.GameModifiers)
}