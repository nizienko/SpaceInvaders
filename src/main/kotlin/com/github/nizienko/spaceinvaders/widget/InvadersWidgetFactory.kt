package com.github.nizienko.spaceinvaders.widget

import com.github.nizienko.spaceinvaders.openGame
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.util.bindVisible
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.ui.components.IconLabelButton
import icons.MyIcons
import javax.swing.JComponent

internal class InvadersWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String {
        return "invaders.widget.factory"
    }

    override fun getDisplayName(): String {
        return "Space Invaders"
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return InvadersWidget(project)
    }

}

internal class InvadersWidget(private val project: Project) : CustomStatusBarWidget {
    override fun ID(): String {
        return "invaders.widget"
    }

    override fun getComponent(): JComponent {
        return IconLabelButton(MyIcons.Monster) {
            openGame(project)
        }.bindVisible(service<SpaceInvadersWidgetModel>().showWidget)
    }
}

@Service(Service.Level.APP)
internal class SpaceInvadersWidgetModel {
    val showWidget = AtomicBooleanProperty(false)

    fun show() {
        showWidget.set(true)
    }

    fun hide() {
        showWidget.set(false)
    }
}
