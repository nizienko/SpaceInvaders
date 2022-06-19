package com.github.nizienko.spaceinvaders

import com.github.nizienko.spaceinvaders.GameFileEditorProvider.Companion.GAME_KEY
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.testFramework.BinaryLightVirtualFile
import java.io.InputStream
import java.io.OutputStream

class OpenGameAction: AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val vFile = BinaryLightVirtualFile("space.invaders")
        vFile.putUserData(GAME_KEY, "space is here!")

        e.project?.let {
            invokeLater {
            FileEditorManager.getInstance(it).openFile(vFile, true)
        }
        }
    }
}

