package com.github.nizienko.spaceinvaders.objects

import com.github.nizienko.spaceinvaders.Game

abstract class Animation : GameObject() {
    protected var phase: Int = 0
    private var finished = false
    abstract val durationMs: Long

    override fun process() {
        nextPhase()
    }

    private fun nextPhase() {
        phase++
        if (phase > Game.millisToFrames(durationMs)) finished = true
    }

    override fun isExpired(): Boolean = finished
}