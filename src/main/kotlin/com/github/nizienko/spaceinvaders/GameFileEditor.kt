package com.github.nizienko.spaceinvaders

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class GameFileEditor(private val file: VirtualFile): FileEditor {

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return file.getUserData(key)
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        file.putUserData(key, value)
    }

    override fun dispose() {
        game.exitGame()
    }

    private val game = Game().apply {
        startGame()
    }

    override fun getComponent(): JComponent {
        return game.display
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return game.display
    }

    override fun getName(): String {
        return "Space Invaders"
    }

    override fun setState(state: FileEditorState) {

    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {

    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {

    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun getFile(): VirtualFile {
        return file
    }
}