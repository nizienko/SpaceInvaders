package com.github.nizienko.spaceinvaders.objects.bonuses

import com.github.nizienko.spaceinvaders.Game
import com.github.nizienko.spaceinvaders.objects.Painter
import java.awt.Color

class BulletSpeed(x: Int, y: Int, maxY: Int) : Bonus(x, y, maxY) {
    companion object {
        private const val BULLET_SPEED_BONUS = 5
        private const val BONUS_WIDTH = 45
        private const val BONUS_HEIGHT = 55
        private const val PICTURE_SIZE_X = 5
        private const val PICTURE_SIZE_Y = 6
        val SYMBOL_POINTS = intArrayOf(0, 2, 2, 0, 4, 2, 3, 2, 3, 5, 1, 5, 1, 2)
    }

    override fun applyBonus(gameModifiers: Game.GameModifiers) {
//        gameModifiers.spaceShipBulletSpeed += BULLET_SPEED_BONUS
    }

    override val width: Int
        get() = BONUS_WIDTH
    override val height: Int
        get() = BONUS_HEIGHT
    override val pictureSizeX: Int
        get() = PICTURE_SIZE_X
    override val pictureSizeY: Int
        get() = PICTURE_SIZE_Y
    override val picture: Painter.() -> Unit = {
        drawBonusSymbol()
    }

    // Draw a polygon that represents the bonus symbol
    private fun Painter.drawBonusSymbol() {
        color(Color.ORANGE)
        polygon(SYMBOL_POINTS)
    }
}
