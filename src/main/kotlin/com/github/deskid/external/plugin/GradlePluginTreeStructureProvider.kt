package com.github.deskid.external.plugin

import com.android.tools.idea.gradle.project.GradleProjectInfo
import com.android.tools.idea.navigator.nodes.AndroidViewProjectNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ProjectViewProjectNode
import com.intellij.ide.util.treeView.AbstractTreeNode


class GradlePluginTreeStructureProvider : TreeStructureProvider {
    override fun modify(parent: AbstractTreeNode<*>, children: Collection<AbstractTreeNode<*>>, settings: ViewSettings?): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children

        if ((parent is ProjectViewProjectNode || parent is AndroidViewProjectNode) && GradleProjectInfo.getInstance(project)
                .isBuildWithGradle
        ) {
            val modifiedChildren: MutableList<AbstractTreeNode<*>> = ArrayList()
            modifiedChildren.addAll(children)
            modifiedChildren.add(TreeNodeFactory.createExternalPluginsNode(project, settings))
            return modifiedChildren
        }

        return children
    }

    override fun getData(selected: MutableCollection<AbstractTreeNode<*>>, dataId: String): Any? {
        return null
    }
}
