package com.github.nizienko.spaceinvaders.objects

import com.github.nizienko.spaceinvaders.Game

abstract class Animation: GameObject() {
    protected var faze: Int = 0

    private var finished = false
    fun isFinished() = finished

    abstract val durationMs: Long

    override fun process() {
        faze++
        if (faze > Game.millisToFrames(durationMs)) finished = true
    }

    override fun isExpired(): Boolean  = finished
}