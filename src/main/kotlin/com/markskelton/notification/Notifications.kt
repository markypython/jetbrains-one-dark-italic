package com.markskelton.notification

import com.intellij.ide.plugins.PluginManagerCore.getPlugin
import com.intellij.ide.plugins.PluginManagerCore.getPluginOrPlatformByClassName
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.ui.IconManager

val UPDATE_MESSAGE: String = """
      What's New?<br>
      <ul>
        <li>Fixed bug with file colors.</li>
        <li>Usability issue with IntelliJ Ultimate UML Diagram.</li>
        <li>Enhanced 2020.3 welcome screen styling.</li>
      </ul>
      <br>Please see the <a href="https://github.com/one-dark/jetbrains-one-dark-theme/blob/master/CHANGELOG.md">Changelog</a> for more details.
      <br>
      Thank you for choosing the One Dark Theme!<br>
""".trimIndent()

object Notifications {

  private val NOTIFICATION_ICON = IconManager.getInstance().getIcon(
    "/icons/one-dark-logo.svg",
    Notifications::class.java
  )


  private val notificationGroup = NotificationGroupManager.getInstance()
    .getNotificationGroup("One Dark Theme")

  fun displayUpdateNotification(versionNumber: String) {
    val pluginName =
      getPlugin(
        getPluginOrPlatformByClassName(Notifications::class.java.canonicalName)
      )?.name
    notificationGroup.createNotification(
      "$pluginName updated to v$versionNumber",
      UPDATE_MESSAGE,
      NotificationType.INFORMATION,
      NotificationListener.URL_OPENING_LISTENER
    )
      .setIcon(NOTIFICATION_ICON)
      .notify(null)
  }
}
