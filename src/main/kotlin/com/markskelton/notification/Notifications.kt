package com.markskelton.notification

import com.intellij.ide.plugins.PluginManagerCore.getPlugin
import com.intellij.ide.plugins.PluginManagerCore.getPluginOrPlatformByClassName
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.ui.IconManager
import org.intellij.lang.annotations.Language

@Language("HTML")
val UPDATE_MESSAGE: String = """
      What's New?<br>
      <ul>
        <li>2021.1 EAP Support</li>
        <li>Updated Windows 10 Title pane foreground</li>
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

  private val notificationGroup = NotificationGroup(
    "One Dark Theme",
    NotificationDisplayType.BALLOON,
    false,
    "One Dark Theme",
    NOTIFICATION_ICON
  )

  fun displayUpdateNotification(versionNumber: String) {
    val pluginName =
      getPlugin(
        getPluginOrPlatformByClassName(Notifications::class.java.canonicalName)
      )?.name
    notificationGroup.createNotification(
      "$pluginName updated to v$versionNumber",
      UPDATE_MESSAGE,
      NotificationType.INFORMATION,
      NotificationListener.UrlOpeningListener(false)
    )
      .setIcon(NOTIFICATION_ICON)
      .notify(null)
  }
}
