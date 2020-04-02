/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.watch

import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.RuntimeData
import java.nio.file.*

object PomWatcher {

    private val logger by logger(PomWatcher::class.java)
    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeyMap = mutableMapOf<WatchKey, Project>()

    fun registerProject(project : Project) {
        val projectPath = Paths.get(project.location)
        val watchKey = projectPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
        watchKeyMap[watchKey] = project
        logger.info("Registered file watch for path ${project.location}")
    }

    fun start() {
        Thread(Runnable {
            logger.info("Started directory watcher")
            while(true) {
                val key = watchService.take()
                for(event in key.pollEvents()) {
                    val kind = event.kind()
                    if(kind.name() == StandardWatchEventKinds.ENTRY_MODIFY.name() && event.context().toString() == "pom.xml") {
                        logger.debug("pom.xml modified, project: ${watchKeyMap[key]}")
                        RuntimeData.fileChangeQueue.push(watchKeyMap[key] ?: continue)
                    }
                }
                key.reset()
            }
        }).start()

    }
}