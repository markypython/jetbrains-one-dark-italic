package com.markskelton

//import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
//import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
//import com.intellij.openapi.startup.StartupManager
import com.intellij.util.messages.MessageBusConnection
//import com.markskelton.notification.Notifications
import com.markskelton.settings.ThemeSettings
import java.util.*

enum class OneDarkThemes {
  REGULAR, ITALIC, VIVID, VIVID_ITALIC
}

object OneDarkThemeManager {
  private lateinit var messageBus: MessageBusConnection
  val THEMES = mapOf(
    "f92a0fa7-1a98-47cd-b5cb-78ff67e6f4f3" to OneDarkThemes.REGULAR,
    "1a92aa6f-c2f1-4994-ae01-6a78e43eeb24" to OneDarkThemes.ITALIC,
    "4b6007f7-b596-4ee2-96f9-968d3d3eb392" to OneDarkThemes.VIVID,
    "4f556d32-83cb-4b8b-9932-c4eccc4ce3af" to OneDarkThemes.VIVID_ITALIC
  )
  private const val PLUGIN_ID = "com.markskelton.one-dark-theme"

  fun registerStartup(project: Project) {
    if (!this::messageBus.isInitialized) {
      registerUser()

//      attemptToDisplayUpdates(project)

      subscribeToEvents()
    }
  }

  private fun registerUser() {
    if (ThemeSettings.instance.userId.isEmpty()) {
      ThemeSettings.instance.userId = UUID.randomUUID().toString()
    }
  }

//  private fun attemptToDisplayUpdates(project: Project) {
//    getVersion().ifPresent { currentVersion ->
//      if (ThemeSettings.instance.version != currentVersion) {
//        ThemeSettings.instance.version = currentVersion
//        StartupManager.getInstance(project).runWhenProjectIsInitialized {
//          Notifications.displayUpdateNotification(currentVersion)
//        }
//      }
//    }
//  }

//  private fun getVersion(): Optional<String> =
//    PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID)).toOptional()
//      .map { it.version }

  private fun subscribeToEvents() {
    messageBus = ApplicationManager.getApplication().messageBus.connect()
  }
}
