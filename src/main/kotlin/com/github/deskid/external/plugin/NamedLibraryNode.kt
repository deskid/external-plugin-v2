package com.github.deskid.external.plugin

import com.android.utils.Pair
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import java.io.File


class NamedLibraryNode(project: Project?, pair: Pair<String?, String?>?, viewSettings: ViewSettings?) : ProjectViewNode<Pair<String?, String?>?>(project, pair!!, viewSettings) {
    override fun canNavigate(): Boolean {
        return true
    }

    override fun canNavigateToSource(): Boolean {
        return canNavigate()
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        return computeChildren(project)
    }

    override fun update(presentationData: PresentationData) {
        presentationData.presentableText = nodeText
        presentationData.setIcon(AllIcons.Nodes.PpLibFolder)
    }

    private fun computeChildren(project: Project?): Set<AbstractTreeNode<*>> {
        val children: MutableSet<AbstractTreeNode<*>> = HashSet()
        if (project == null) return children

        val path = value!!.second!!
        val virtualFile = VfsUtil.findFileByIoFile(File(path), true) ?: return children

        val libraryFile = JarFileSystem.getInstance()
            .getJarRootForLocalFile(virtualFile) ?: return children

        val psiManager = PsiManager.getInstance(project)
        val libraryPsiDirectory = psiManager.findDirectory(libraryFile)
        children.add(TreeNodeFactory.createNamedLibraryElementNode(project, libraryPsiDirectory, settings))

        return children
    }

    private val nodeText: String
        get() {
            val sb = StringBuilder()
            sb.append(PREFIX)
            val libraryName = value!!.first!!
            sb.append(libraryName)
            val path = value!!.second!!
            if (path.endsWith(DOT_JAR)) sb.append(SUFFIX)
            return sb.toString()
        }

    override fun contains(virtualFile: VirtualFile): Boolean {
        return false
    }

    companion object {
        private const val PREFIX = "Plugin: "

        private const val SUFFIX = "@jar"

        private const val DOT_JAR = ".jar"
    }
}
