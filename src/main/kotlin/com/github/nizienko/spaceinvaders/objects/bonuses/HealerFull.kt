package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.Painter
import java.awt.Color

class HealerFull(x: Int, y: Int, maxY: Int) : Bonus(x, y, maxY) {
    override fun applyBonus(gameModifiers: Game.GameModifiers) {
        gameModifiers.spaceShipHealth = 100
        gameModifiers.shakeCamera(0.01)
    }
    override val width: Int = 45
    override val height: Int = 45
    override val pictureSizeX: Int = 3
    override val pictureSizeY: Int = 3
    override val picture: Painter.() -> Unit = {
        color(Color.GREEN)
        rect(0, 1, 3, 1)
        rect(1, 0, 1, 3)
    }
}