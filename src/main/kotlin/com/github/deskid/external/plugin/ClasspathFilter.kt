package com.github.deskid.external.plugin

import com.android.utils.Pair


object ClasspathFilter {
    private const val SOURCES_DOT_JAR = "-sources.jar"

    fun select(paths: Set<String>?): String? {
        if (paths.isNullOrEmpty()) return null

        var pair: Pair<Boolean, String>? = null

        var firstPath: String? = null

        for (path in paths) {
            if (firstPath == null) {
                firstPath = path
            }
            if (path.endsWith(SOURCES_DOT_JAR)) {
                pair = Pair.of(true, path)
                break
            }
        }

        val path = if (pair != null && pair.first) {
            pair.second
        } else {
            firstPath
        }
        return path
    }
}
