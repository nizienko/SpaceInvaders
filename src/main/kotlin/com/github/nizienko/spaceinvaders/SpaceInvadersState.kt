package com.github.nizienko.spaceinvaders

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.nizienko.spaceinvaders.SpaceInvadersState",
    storages = [Storage("AutoKeySettingsPlugin.xml")]
)
class SpaceInvadersState: PersistentStateComponent<SpaceInvadersState> {
    companion object {
        fun getInstance(): SpaceInvadersState {
            return ApplicationManager.getApplication().getService(SpaceInvadersState::class.java)
        }
    }
    var recordKills: Int = 0
    override fun getState(): SpaceInvadersState? {
        return this
    }

    override fun loadState(state: SpaceInvadersState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}