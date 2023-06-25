package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.GameObject
import java.awt.Point

abstract class Bonus(protected var x: Int, protected var y: Int, private val maxY: Int) : GameObject() {
    private val speed = 13
    override fun process() {
        y += speed
        if (y >= maxY) isOut = true
    }

    override val position: Point
        get() = Point(x, y)
    override fun isExpired(): Boolean = isOut

    var isOut = false

    abstract fun applyBonus(gameModifiers: Game.GameModifiers)
}