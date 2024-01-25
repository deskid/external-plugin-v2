package com.github.deskid.external.plugin

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon


object PluginIconLoader {
    val Gradle: Icon = load("/icons/gradle-svg.svg")

    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, PluginIconLoader::class.java)
    }

}