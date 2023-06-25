package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.Painter
import java.awt.Color
import java.awt.Point

class BulletSpeed(x: Int, y: Int, maxY: Int) : Bonus(x, y, maxY) {
    override fun applyBonus(gameModifiers: Game.GameModifiers) {
        gameModifiers.spaceShipBulletSpeed +=5
    }

    override val width: Int
        get() = 45
    override val height: Int
        get() = 55
    override val pictureSizeX: Int
        get() = 5
    override val pictureSizeY: Int
        get() = 6
    override val picture: Painter.() -> Unit = {
        color(Color.ORANGE)
        polygon(
                Point(0,2),
                Point(2,0),
                Point(4, 2),
                Point(3, 2),
                Point(3, 5),
                Point(1, 5),
                Point(1, 2),
        )
    }
}