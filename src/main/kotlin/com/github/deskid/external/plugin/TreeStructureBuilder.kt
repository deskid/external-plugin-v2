package com.github.deskid.external.plugin

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory


object TreeStructureBuilder {

    fun buildTreeFromDirectory(project: Project?, parentNode: NamedLibraryElementNode?, directory: PsiDirectory?, settings: ViewSettings?) {
        if (project == null || parentNode == null || directory == null || settings == null) return

        val psiDirChildrenFiles = directory.files

        if (psiDirChildrenFiles.isNotEmpty()) {
            for (psiFile in psiDirChildrenFiles) {
                parentNode.addChildren(PsiFileNode(project, psiFile!!, settings))
            }
        }

        val psiDirChildrenDirectories = directory.subdirectories

        if (psiDirChildrenDirectories.isNotEmpty()) {
            for (psiDirectory in psiDirChildrenDirectories) {
                val psiDirectoryNode = NamedLibraryElementNode(project, psiDirectory, settings)
                parentNode.addChildren(psiDirectoryNode)
                buildTreeFromDirectory(project, psiDirectoryNode, psiDirectory, settings)
            }
        }
    }
}
