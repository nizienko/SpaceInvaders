package com.github.nizienko.spaceinvaders

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import icons.MyIcons
import javax.swing.Icon

class SpaceInvadersFileType: LanguageFileType(PlainTextLanguage.INSTANCE) {
    companion object {
        @JvmStatic
        val INSTANCE = SpaceInvadersFileType()
    }
    override fun getName(): String {
        return "Space Invaders"
    }

    override fun getDescription(): String {
        return "Space Invaders"
    }

    override fun getDefaultExtension(): String {
        return "invaders"
    }

    override fun getIcon(): Icon? {
        return MyIcons.Monster
    }
}