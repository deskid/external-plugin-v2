package com.github.deskid.external.plugin

import com.android.tools.idea.gradle.dsl.api.BuildScriptModel
import com.android.tools.idea.gradle.dsl.api.GradleBuildModel
import com.android.tools.idea.gradle.dsl.api.GradleModelProvider
import com.android.tools.idea.gradle.dsl.api.dependencies.ArtifactDependencyModel
import com.android.tools.idea.gradle.dsl.api.dependencies.DependenciesModel
import com.android.tools.idea.gradle.dsl.api.ext.ResolvedPropertyModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.lang.reflect.Method
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath


object BuildFileDataProvider {
    /**
     * 'classpath' is the configuration name used to specify buildscript dependencies.
     */
    private const val CLASSPATH_CONFIGURATION_NAME = "classpath"

    private fun getGradleBuildModels(project: Project): List<GradleBuildModel> {
        val modules: Array<Module> = ModuleManager.getInstance(project).modules

        val buildModels: MutableList<GradleBuildModel> = ArrayList()

        for (module in modules) {
            val moduleDir = module.externalProjectPath

            val buildFile = File(moduleDir, "build.gradle")

            if (!buildFile.exists() || !buildFile.isFile) continue

            val virtualFile: VirtualFile = VfsUtil.findFileByIoFile(buildFile, true) ?: throw RuntimeException("Failed to find " + buildFile.path)

            val buildModel: GradleBuildModel = try {
                GradleBuildModel.parseBuildFile(virtualFile, project)
            } catch (e: Exception) {
                GradleModelProvider.getInstance()
                    .parseBuildFile(virtualFile, project)
            }

            buildModels.add(buildModel)
        }

        return buildModels
    }

    private fun getBuildScriptModels(project: Project): List<BuildScriptModel> {
        val buildModels: List<GradleBuildModel> = getGradleBuildModels(project)

        val buildScriptModels: MutableList<BuildScriptModel> = ArrayList()

        for (model in buildModels) {
            buildScriptModels.add(model.buildscript())
        }

        return buildScriptModels
    }

    private fun getBuildScriptDependencyModels(project: Project): List<DependenciesModel> {
        val buildScriptModels: List<BuildScriptModel> = getBuildScriptModels(project)

        val buildScriptDependencyModels: MutableList<DependenciesModel> = ArrayList()

        for (model in buildScriptModels) {
            buildScriptDependencyModels.add(model.dependencies())
        }

        return buildScriptDependencyModels
    }

    fun getBuildScriptDependencies(project: Project): Set<String> {
        val dependenciesModels: List<DependenciesModel> = getBuildScriptDependencyModels(project)

        val dependencies: MutableSet<String> = HashSet()

        for (model in dependenciesModels) {
            val artifactDependencyModels: List<ArtifactDependencyModel> = model.artifacts(CLASSPATH_CONFIGURATION_NAME)

            for (artifactDependencyModel in artifactDependencyModels) {
                var value: String? = null
                try {
                    //compat AS-3.2
                    val method: Method = ArtifactDependencyModel::class.java.getDeclaredMethod("compactNotation")
                    method.isAccessible = true
                    val `object` = method.invoke(artifactDependencyModel)
                    if (`object` is String) {
                        value = `object`
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (value == null || value.trim { it <= ' ' }.length == 0) {
                    val builder = StringBuilder()

                    val group: Any = artifactDependencyModel.group()
                    val name: Any = artifactDependencyModel.name()
                    val version: Any = artifactDependencyModel.version()
                    //compat AS-3.2
                    if (group is ResolvedPropertyModel && name is ResolvedPropertyModel && version is ResolvedPropertyModel) {
                        try {
                            val method: Method = ResolvedPropertyModel::class.java.getDeclaredMethod("getResultModel")
                            method.isAccessible = true
                            builder.append(
                                method.invoke(group)
                                    .toString()
                            )
                            builder.append(":")
                            builder.append(
                                method.invoke(name)
                                    .toString()
                            )
                            builder.append(":")
                            builder.append(
                                method.invoke(version)
                                    .toString()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
//                    } else if (group is GradleNotNullValue && name is GradleNotNullValue && version is GradleNotNullValue) {
//                        builder.append((group as GradleNotNullValue).value())
//                        builder.append(":")
//                        builder.append((name as GradleNotNullValue).value())
//                        builder.append(":")
//                        builder.append((version as GradleNotNullValue).value())
//                    }

                    value = builder.toString()
                }

                dependencies.add(value)
            }
        }

        return dependencies
    }
}
