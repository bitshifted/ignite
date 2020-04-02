/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.maven

import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.MavenDependency
import co.bitshifted.xapps.ignite.model.Project
import javafx.concurrent.Task

class DependencySyncTask(val project : Project) : Task<Unit>() {

    private val logger by logger(DependencySyncTask::class.java)

    override fun call() {
        val result = MavenHandler.buildProject(project)
        if(!result) {
            logger.warn("Failed to build project $project. Dependencies might not be up to date.")
        }
        val depList = MavenHandler.listDependencies(project)
        val projectDeps = mutableListOf<MavenDependency>()
        depList.forEach {
            projectDeps.add(MavenHandler.resolveDependency(it, project) ?: return@forEach)
        }

        project.jvm.dependencies = projectDeps.filter { !project.jvm?.platformDependencies.isPlatformSpecificDependency(it) }
    }
}