/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.deploy

import co.bitshifted.xapps.ignite.*
import co.bitshifted.xapps.ignite.maven.MavenHandler
import co.bitshifted.xapps.ignite.model.DependencyManagementType
import co.bitshifted.xapps.ignite.model.JvmDependencyScope
import co.bitshifted.xapps.ignite.model.MavenDependency
import co.bitshifted.xapps.ignite.model.Project
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.*

class Packer {

    private val logger by logger(Packer::class.java)

    fun createDeploymentPackage(project: Project): Path {
        logger.info("Creating deployment package for project ${project.name}")
        val rootDir = createPackageDirectory(project)
        copyConfig(project, Path.of(rootDir.toAbsolutePath().toString(), CONFIG_FILE_NAME))
        copyDependencies(project, rootDir)
        copyApplicationData(project, rootDir)
        return createPackage(rootDir)
    }

    private fun createPackageDirectory(project: Project): Path {
        val tmpDir = System.getProperty("java.io.tmpdir")
        logger.debug("Temporary directory: $tmpDir")
        val rootDir = Paths.get(tmpDir, project.application.appId)
        rootDir.toFile().mkdir()
        logger.debug("Successfully created package root directory")
        val modulesDir = Path.of(rootDir.toAbsolutePath().toString(), "modules")
        modulesDir.toFile().mkdir()
        val classpathDir = Path.of(rootDir.toAbsolutePath().toString(), "classpath")
        classpathDir.toFile().mkdir()
        return rootDir
    }

    private fun copyConfig(project: Project, target: Path) {
        val configFilePath = Paths.get(project.location, CONFIG_FILE_NAME)
        Files.copy(configFilePath, target, StandardCopyOption.REPLACE_EXISTING)
        logger.debug("Configuration file copy completed")
    }

    private fun copyDependencies(project: Project, packageRoot: Path) {
        val dependencies = project.jvm.dependencies
        if (project.dependencyManagementType == DependencyManagementType.MAVEN) {
            dependencies.map { it as MavenDependency }.forEach {
                val file = MavenHandler.getDependencyFile(it)
                val target = if (it.scope == JvmDependencyScope.MODULEPATH) {
                    Path.of(packageRoot.toAbsolutePath().toString(), PACKAGE_MODULES_DIR, file.name)
                } else {
                    Path.of(packageRoot.toAbsolutePath().toString(), PACKAGE_CLASSPATH_DIR, file.name)
                }
                Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING)
                logger.debug("Copied file ${file.name}")
            }
        }
    }

    private fun copyApplicationData(project: Project, packageRoot: Path) {
        val icons = project.application.info.icons
        icons.forEach {
            copyWithDirectoryCreation(project, it.path, packageRoot)
        }
        val splashScreen = project.jvm.splashScreen ?: return
        copyWithDirectoryCreation(project, splashScreen.path, packageRoot)
    }

    private fun copyWithDirectoryCreation(project : Project, targetPath: String, packageRoot: Path) {
        val source = Path.of(project.location, targetPath)
        val target = Path.of(packageRoot.toAbsolutePath().toString(), targetPath)
        Files.createDirectories(target.parent)
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
        logger.debug("Copied file: ${source.toAbsolutePath()}")
    }

    private fun createPackage(packageRoot : Path) : Path {
        val parent = packageRoot.parent
        val archiveFile = Path.of(parent.toAbsolutePath().toString(), packageRoot.toFile().name + ".zip").toFile()
        val os = ZipArchiveOutputStream(FileOutputStream(archiveFile))
        os.use {
            Files.walk(packageRoot).forEach {
                val file = it.toFile()
                if(!file.isDirectory) {
                    logger.debug("Zipping file ${file.name}")
                    val entry = ZipArchiveEntry(file, filePathRelative(file.absolutePath, packageRoot.toAbsolutePath().toString()))
                    FileInputStream(file).use {
                        os.putArchiveEntry(entry)
                        IOUtils.copy(it, os)
                        os.closeArchiveEntry()
                    }
                }
            }
            os.finish()
        }
        return archiveFile.toPath()
    }
}