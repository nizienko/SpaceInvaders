package com.github.nizienko.spaceinvaders.widget

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
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
        return InvadersWidget()
    }

}

internal class InvadersWidget : CustomStatusBarWidget {
    override fun ID(): String {
        return "invaders.widget"
    }

    override fun getComponent(): JComponent {
        return IconLabelButton(MyIcons.Monster) {
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess { dataContext ->
                ActionManager.getInstance().getAction("com.github.nizienko.spaceinvaders.OpenGameAction")
                    .actionPerformed(
                        AnActionEvent(
                            null,
                            dataContext,
                            ActionPlaces.UNKNOWN,
                            Presentation(),
                            ActionManager.getInstance(),
                            0
                        )
                    )
            }
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