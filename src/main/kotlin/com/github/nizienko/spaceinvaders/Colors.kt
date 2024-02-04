package com.github.nizienko.spaceinvaders

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.UIUtil
import java.awt.Color
import javax.swing.UIManager

class Colors(private val level: Int) {
    companion object {
        private val GameObjectColorsLight = listOf(
            Color.DARK_GRAY,
            Color(65, 65, 120),
            Color(70, 120, 60),
            Color(16, 170, 185),
            Color(16, 120, 85),
            Color(120, 80, 65),
            Color(255, 103, 103)
        )

        private val GameObjectColorsDark = listOf(
            Color.LIGHT_GRAY,
            Color(220, 120, 180),
            Color(190, 245, 80),
            Color(16, 170, 185),
            Color(240, 80, 50),
            Color(255, 103, 103),
            Color(255, 110, 24)
        )

        private val BackgroundColorsLight = Color.WHITE
        private val BackgroundColorsDark = Color(43, 43, 43)
    }

    val gameObject: Color
        get() = themeBasedColor(
            level - 1,
            GameObjectColorsLight,
            GameObjectColorsDark,
        ) {
            UIUtil.getLabelInfoForeground()
        }

    val background: Color
        get() = themeBasedColor(
            BackgroundColorsLight,
            BackgroundColorsDark
        ) {
            UIUtil.getLabelBackground()
        }
}

private val theme: String
    get() = EditorColorsManager.getInstance().globalScheme.displayName

private fun themeBasedColor(light: Color, dark: Color, elseColor: () -> Color): Color {
    return when {
        theme == "IntelliJ" || theme.contains("Light", true) -> light
        theme.contains("Darcula", true) || theme.contains("Dark", true) -> dark
        else -> elseColor()
    }
}

private fun themeBasedColor(level: Int, lights: List<Color>, darks: List<Color>, elseColor: () -> Color): Color {
    return when {
        theme == "IntelliJ" || theme.contains("Light", true) -> lights[level % lights.size]
        theme.contains("Darcula", true) || theme.contains("Dark", true) -> darks[level % darks.size]
        else -> elseColor()
    }
}