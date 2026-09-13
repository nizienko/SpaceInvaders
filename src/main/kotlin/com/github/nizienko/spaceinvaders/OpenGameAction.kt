package com.github.nizienko.spaceinvaders

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.testFramework.BinaryLightVirtualFile


class OpenGameAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        openGame(e.project)
    }
}

internal fun openGame(project: Project?) {
    val vFile = BinaryLightVirtualFile("space.invaders")
    vFile.putUserData(GAME_KEY, "space is here!")
    project?.let {
        invokeLater {
            FileEditorManager.getInstance(it).openFile(vFile, true)
        }
    }
}
