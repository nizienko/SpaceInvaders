package com.github.nizienko.spaceinvaders

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsListener
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.DumbService.DumbModeListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.IconLikeCustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.WindowManager
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskListener
import com.intellij.task.ProjectTaskManager
import com.intellij.ui.components.IconLabelButton
import icons.MyIcons
import javax.swing.JComponent


internal class RegisterInvaderShowingInStatusBarActivity : StartupActivity, DumbAware {
    override fun runActivity(project: Project) {
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        val widget = SpaceInvadersWidget()
        val show = {
            if (statusBar.getWidget(widget.ID()) == null) {
                statusBar.addWidget(
                    widget,
                    StatusBar.Anchors.before(StatusBar.StandardWidgets.POSITION_PANEL),
                    statusBar
                )
            }
        }
        val hide = {
            statusBar.removeWidget(widget.ID())
        }
        project.messageBus.connect().subscribe(DumbService.DUMB_MODE, object : DumbModeListener {
            override fun enteredDumbMode() {
                show()
            }

            override fun exitDumbMode() {
                hide()
            }
        })
        project.messageBus.connect().subscribe(ProjectTaskListener.TOPIC, object : ProjectTaskListener {
            override fun started(context: ProjectTaskContext) {
                show()
            }

            override fun finished(result: ProjectTaskManager.Result) {
                hide()
            }
        })
        project.messageBus.connect().subscribe(SMTRunnerEventsListener.TEST_STATUS, object : SMTRunnerEventsListener {
            override fun onTestingStarted(testsRoot: SMTestProxy.SMRootTestProxy) {
                show()
            }

            override fun onTestingFinished(testsRoot: SMTestProxy.SMRootTestProxy) {
                hide()
            }

            override fun onTestsCountInSuite(count: Int) {

            }

            override fun onTestStarted(test: SMTestProxy) {

            }

            override fun onTestFinished(test: SMTestProxy) {

            }

            override fun onTestFailed(test: SMTestProxy) {

            }

            override fun onTestIgnored(test: SMTestProxy) {

            }

            override fun onSuiteFinished(suite: SMTestProxy) {

            }

            override fun onSuiteStarted(suite: SMTestProxy) {

            }

            override fun onCustomProgressTestsCategory(categoryName: String?, testCount: Int) {

            }

            override fun onCustomProgressTestStarted() {

            }

            override fun onCustomProgressTestFailed() {

            }

            override fun onCustomProgressTestFinished() {

            }

            override fun onSuiteTreeNodeAdded(testProxy: SMTestProxy?) {

            }

            override fun onSuiteTreeStarted(suite: SMTestProxy?) {

            }
        })
    }
}

internal class SpaceInvadersWidget : IconLikeCustomStatusBarWidget {
    override fun dispose() {

    }

    override fun ID(): String = "SpaceInvaders"

    override fun install(statusBar: StatusBar) {

    }

    override fun getComponent(): JComponent {
        return IconLabelButton(MyIcons.Monster) {
            DataManager.getInstance().dataContextFromFocusAsync.then { dataContext ->
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
        }
    }
}