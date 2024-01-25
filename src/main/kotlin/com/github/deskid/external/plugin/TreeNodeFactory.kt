package com.github.deskid.external.plugin

import com.android.utils.Pair
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory


object TreeNodeFactory {
    fun createExternalPluginsNode(project: Project, settings: ViewSettings?): AbstractTreeNode<*> {
        return ExternalPluginsNode(project, settings)
    }

    fun createNamedLibraryNode(project: Project, value: Pair<String?, String?>?, settings: ViewSettings?): AbstractTreeNode<*> {
        return NamedLibraryNode(project, value, settings)
    }

    fun createNamedLibraryElementNode(project: Project?, value: PsiDirectory?, settings: ViewSettings?): AbstractTreeNode<*> {
        return NamedLibraryElementNode(project, value, settings)
    }
}
