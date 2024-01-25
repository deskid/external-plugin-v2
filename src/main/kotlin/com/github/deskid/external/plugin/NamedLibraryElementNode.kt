package com.github.deskid.external.plugin

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileSystemItemFilter
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory


class NamedLibraryElementNode : PsiDirectoryNode {
    private val mChildren: MutableSet<AbstractTreeNode<*>> = HashSet()

    constructor(project: Project?, value: PsiDirectory?, viewSettings: ViewSettings?) : super(project, value!!, viewSettings)

    constructor(project: Project?, value: PsiDirectory?, viewSettings: ViewSettings?, filter: PsiFileSystemItemFilter?) : super(project, value!!, viewSettings, filter)

    fun addChildren(node: AbstractTreeNode<*>?) {
        if (node == null) return
        mChildren.add(node)
    }

    override fun getChildrenImpl(): Collection<AbstractTreeNode<*>> {
        mChildren.clear()
        TreeStructureBuilder.buildTreeFromDirectory(project, this, value, settings)
        return mChildren
    }

    override fun updateImpl(data: PresentationData) {
        super.updateImpl(data)
        data.presentableText = value.name
    }

}
