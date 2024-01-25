package com.github.deskid.external.plugin

import com.android.utils.Pair
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.gradle.service.GradleBuildClasspathManager


class ExternalPluginsNode(project: Project, viewSettings: ViewSettings?) : ProjectViewNode<String?>(project, NODE_NAME, viewSettings) {
    override fun contains(file: VirtualFile): Boolean {
        return false
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        val project = project
        val children: MutableList<AbstractTreeNode<*>> = ArrayList()

        if (project == null) return children

        val files = GradleBuildClasspathManager.getInstance(project).allClasspathEntries

        val dependencies: Set<String> = BuildFileDataProvider.getBuildScriptDependencies(project)

        val results: MutableSet<String> = HashSet()

        for (dependency in dependencies) {
            if (dependency.isEmpty()) continue

            results.clear()

            for (file in files) {
                if (file == null) continue

                val fileUrl = file.presentableUrl

                if (fileUrl.isEmpty()) continue

                if (fileUrl.contains(dependency.replace(":", "/"))) {
                    results.add(fileUrl)
                }
            }

            if (results.isEmpty()) continue

            val fileUrl: String = ClasspathFilter.select(results) ?: continue

            children.add(TreeNodeFactory.createNamedLibraryNode(project, Pair.of(dependency, fileUrl), settings))
            println("dependencies----->$fileUrl")
        }

        return children
    }

    override fun update(presentation: PresentationData) {
        presentation.presentableText = NODE_NAME
        presentation.setIcon(PluginIconLoader.Gradle)
    }

    companion object {
        private const val NODE_NAME = "External Plugins"
    }
}
