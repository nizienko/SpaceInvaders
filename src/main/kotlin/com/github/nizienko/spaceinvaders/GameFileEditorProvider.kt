package com.github.nizienko.spaceinvaders

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile

class GameFileEditorProvider : FileEditorProvider, DumbAware {
    companion object {
        val GAME_KEY: Key<String> = Key.create("space.invaders")
    }
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.getUserData(GAME_KEY) != null
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return GameFileEditor(file)
    }

    override fun getEditorTypeId(): String {
        return "space-invaders"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}