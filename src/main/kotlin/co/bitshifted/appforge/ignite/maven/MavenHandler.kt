/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.maven

import co.bitshifted.appforge.ignite.dependencyDeploymentPath
import co.bitshifted.appforge.ignite.getLocalMavenRepoDir
import co.bitshifted.appforge.ignite.logger
import co.bitshifted.appforge.ignite.model.JvmDependencyScope
import co.bitshifted.appforge.ignite.model.MavenDependency
import co.bitshifted.appforge.ignite.model.Project
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.Invoker
import java.io.File
import java.lang.module.ModuleFinder
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object MavenHandler {

    private val logger by logger(MavenHandler::class.java)
    private val mavenInvoker : Invoker

    init {
        mavenInvoker = DefaultInvoker()
    }

    fun buildProject(project : Project) : Boolean {
        logger.info("Building project ${project.name}")
        val props = Properties()
        props.put("maven.test.skip", "true")
        val request = DefaultInvocationRequest()
        request.baseDirectory = File(project.location)
        request.goals = listOf("install")
        request.properties = props

        val result = mavenInvoker.execute(request)
        if(result.exitCode != 0) {
            logger.error("Failed to build project ${project.name}. Exit code: ${result.exitCode}", result.executionException)
        }
        return result.exitCode == 0
    }

    fun listDependencies(project: Project) : List<String> {
        val deps = mutableListOf(processMavenArtifact(project))
        mavenInvoker.setOutputHandler(DependencyOutputHandler(deps))

        val request = DefaultInvocationRequest()
        request.baseDirectory = File(project.location)
        request.goals = listOf("dependency:list")

        val result = mavenInvoker.execute(request)
        if(result.exitCode != 0) {
            logger.error("Failed to list dependencies for ${project.name}. Exit code: ${result.exitCode}", result.executionException)
        }
        logger.debug("Dependency list for project ${project.name}: {}", deps)

        return deps

    }

    fun resolveDependency(groupId : String, artifactId : String, version : String, classifier : String?, packaging : String, project: Project) : Boolean {
        logger.info("Resolving dependency {}:{}:{}", groupId, artifactId, version)
        val props = Properties()
        props.put("groupId", groupId)
        props.put("artifactId", artifactId)
        props.put("version", version)
        props.put("packaging", packaging)
        if(classifier != null) {
            props["classifier"] =  classifier
        }
        logger.debug("Maven execution properties: {}", props)
        val request = DefaultInvocationRequest()
        request.baseDirectory = File(project.location)
        request.goals = listOf("dependency:get")
        request.setProperties(props)
        request.setShowErrors(true)
        mavenInvoker.setErrorHandler(MavenErrorHandler())
        val returnCode = mavenInvoker.execute(request)
        if(returnCode.exitCode == 0) {
            logger.info("Successfuly resolved dependency {}:{}:{}", groupId, artifactId, version)
            return true
        } else {
            logger.error("Failed to resolve dependency {}:{}:{}, exit code: {}", groupId, artifactId, version, returnCode.exitCode, returnCode.executionException)
            return false
        }

    }

    fun resolveDependency(spec : String, project : Project) : MavenDependency? {
        val parts = spec.trim().split(":")
        val groupId = parts[0]
        val artifactId = parts[1]
        val packaging = parts[2]
        val classifier : String? = if(parts.size == 6) parts[3] else null
        val version = if(parts.size == 5) parts[3] else parts[4]

        val file = getDependencyFile(groupId, artifactId, version, classifier, packaging)
        var result = true
        if(!file.exists()) {
            result = resolveDependency(groupId, artifactId, version, classifier, packaging, project)
        }
        if(result) {
            val name = createFileName(artifactId, version, classifier, packaging)
            val scope = findDependencyScope(groupId, artifactId, version, packaging, classifier)
            logger.debug("Found dependency file at ${file.absolutePath}")
            return MavenDependency(groupId, artifactId, version, packaging, classifier, name ,
                dependencyDeploymentPath(name, scope), file.length(),
                scope,
                isMainArtifact(groupId, artifactId, packaging, version, project))
        }
        return null
    }


    private fun createFileName(artifactId: String, version: String, classifier: String?, type: String) : String {
        val sb = StringBuilder(artifactId)
        sb.append("-").append(version)
        if(classifier != null && classifier.isNotEmpty()) {
            sb.append("-").append(classifier)
        }
        sb.append(".").append(type)
        return sb.toString()
    }

    private fun getDependencyFile(groupId: String, artifactId: String, version: String, classifier: String?, packaging : String) : File {
        val repoDir = getLocalMavenRepoDir()
        val params = mutableListOf<String>()
        val parts = groupId.split(".")
        parts.forEach { params.add(it) }
        params.add(artifactId)
        params.add(version)
        params.add(createFileName(artifactId, version, classifier, packaging))

        return  Path.of(repoDir.absolutePath, *params.toTypedArray() ).toFile()
    }

    fun getDependencyFile(dependency: MavenDependency) : File {
        return getDependencyFile(dependency.groupId, dependency.artifactId, dependency.version, dependency.classifier, dependency.packaging)
    }

    private fun processMavenArtifact(project : Project) : String {
        val sb = StringBuilder()
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = docBuilder.parse(File(project.location, "pom.xml"))

        val xpath = XPathFactory.newInstance().newXPath()
        val groupId = xpath.evaluate("/project/groupId", document, XPathConstants.STRING) as String
        sb.append(groupId).append(":")

        val artifactId = xpath.evaluate("/project/artifactId", document, XPathConstants.STRING) as String
        sb.append(artifactId).append(":")

        val packaging = xpath.evaluate("/project/packaging", document, XPathConstants.STRING) as String
        if(packaging.isNotEmpty()) {
            sb.append(packaging)
        } else {
            sb.append("jar") // fallback to default packaging jar
        }
        sb.append(":")

        val version = xpath.evaluate("/project/version", document, XPathConstants.STRING) as String
        sb.append(version).append(":")
        sb.append("runtime")
        // cache maven artifact
        project.mainArtifact = sb.toString()

        return sb.toString()
    }

    private fun isMainArtifact(groupId: String, artifactId: String, packaging: String, version: String, project: Project) : Boolean {
        val sb = StringBuilder()
        sb.append(groupId).append(":").append(artifactId).append(":").append(packaging).append(":").append(version).append(":runtime")
        return project.mainArtifact == sb.toString()
    }

    private fun findDependencyScope(groupId : String, artifactId : String, version : String, packaging : String, classifier : String?) : JvmDependencyScope {
        val jarFile = getDependencyFile(groupId, artifactId, version, classifier, packaging)
        logger.debug("Looking up module for file $jarFile")
        val moduleFinder = ModuleFinder.of(jarFile.toPath())
        val modules = moduleFinder.findAll()
        modules.forEach { logger.debug("Found module: ${it.descriptor().name()}") }
        if(modules.isEmpty()) {
            return JvmDependencyScope.CLASSPATH
        }
        return JvmDependencyScope.MODULEPATH
    }
}