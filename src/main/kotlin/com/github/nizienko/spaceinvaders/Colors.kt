package com.github.nizienko.spaceinvaders

import com.intellij.util.ui.UIUtil
import java.awt.Color
import javax.swing.UIManager

class Colors(private val level: Int) {
    val gameObject: Color
        get() = themeBasedColor(
            level - 1,
            listOf(
                Color.DARK_GRAY,
                Color(65, 65, 120),
                Color(70, 120, 60),
                Color(16, 170, 185),
                Color(16, 120, 85),
                Color(120, 80, 65),
                Color(255, 103, 103)
            ),
            listOf(
                Color.LIGHT_GRAY,
                Color(147, 65, 120),
                Color(190, 245, 80),
                Color(16, 170, 185),
                Color(240, 80, 50),
                Color(255, 103, 103),
                Color(255, 110, 24)
            )
        ) {
            UIUtil.getLabelInfoForeground()
        }

    val background: Color
        get() = themeBasedColor(
            Color.WHITE,
            Color(43, 43, 43)
        ) {
            UIUtil.getLabelBackground()
        }
}

private fun themeBasedColor(light: Color, dark: Color, elseColor: () -> Color): Color {
    val theme = UIManager.getLookAndFeel().name
    return when {
        theme == "IntelliJ" || theme.contains("Light", true) -> light
        theme.contains("Darcula", true) || theme.contains("Dark", true) -> dark
        else -> elseColor()
    }
}

private fun themeBasedColor(level: Int, lights: List<Color>, darks: List<Color>, elseColor: () -> Color): Color {
    val theme = UIManager.getLookAndFeel().name
    return when {
        theme == "IntelliJ" || theme.contains("Light", true)  -> lights[level % lights.size]
        theme.contains("Darcula", true) || theme.contains("Dark", true) -> darks[level % darks.size]
        else -> elseColor()
    }
}