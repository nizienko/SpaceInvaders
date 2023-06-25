package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.Painter
import java.awt.Color

class Healer(x: Int, y: Int, maxY: Int) : Bonus(x, y, maxY) {
    override val width: Int = 35
    override val height: Int = 35
    override val pictureSizeX: Int = 3
    override val pictureSizeY: Int = 3
    override val picture: Painter.() -> Unit = {
        color(Color.RED)
        rect(0, 1, 3, 1)
        rect(1, 0, 1, 3)
    }

    override fun applyBonus(gameModifiers: Game.GameModifiers) {
        gameModifiers.spaceShipHealth += 5
        gameModifiers.shakeCamera(0.01)
    }
}