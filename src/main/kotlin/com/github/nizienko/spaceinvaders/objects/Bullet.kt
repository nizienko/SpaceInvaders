package com.github.nizienko.spaceinvaders.objects

class Bullet(
        override var x: Int,
        override var y: Int,
        private val isDownDirection: Boolean = false,
        private val maxY: Int = 0,
        private val speed: Int = 30
) : GameObject() {
    override val width: Int = 5
    override val height: Int = 30
    override val pictureSizeX: Int = 1
    override val pictureSizeY: Int = 1

    override val picture: Painter.() -> Unit = {
        color(defaultColours.gameObject)
        rect(0, 0, 1, 1)
    }

    override fun process() {

    }

    override fun isExpired(): Boolean = isOut

    var isOut = false
    fun move() {
        if (isDownDirection) {
            y += speed
            if (y > maxY) {
                isOut = true
            }
        } else {
            y -= speed
            if (y < 0) {
                isOut = true
            }
        }
    }

    fun isMatchInvader(invader: Invader): Boolean {
        return invader.x - invader.width / 2 <= x
                && invader.x + invader.width / 2 >= x
                && invader.y - invader.height / 2 <= y
                && invader.y + invader.height / 2 >= y
    }
}
