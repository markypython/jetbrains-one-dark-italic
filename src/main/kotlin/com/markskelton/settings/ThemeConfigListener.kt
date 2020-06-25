package com.markskelton.settings

import com.intellij.util.messages.Topic
import java.util.*

val THEME_CONFIG_TOPIC: Topic<ThemeConfigListener> =
  Topic(ThemeConfigListener::class.java)

interface ThemeConfigListener : EventListener {
  fun themeConfigUpdated(themeSettings: ThemeSettings)
}