/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.deploy.Packer
import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.persist.XMLPersister
import javafx.fxml.FXML
import java.nio.file.Path

class ProjectButtonsController {

    val log by  logger(ProjectButtonsController::class.java)

    @FXML
    fun saveProject() {
        val project = RuntimeData.selectedProjectItem.value.project ?: return
        XMLPersister.writeProject(project)
    }

    @FXML
    fun deployProject() {
        val project = RuntimeData.selectedProjectItem.value.project ?: return
        val file = Path.of(project.location, "ignite-config.xml").toFile()
        log.info("Deploying project {} from configuration file {}", project.name, file.absolutePath)

        val packer = Packer()
        val deploymentPackage = packer.createDeploymentPackage(project)
        val statusUrl = packer.uploadDeplyomentPackage(deploymentPackage, project)
        log.info("Deployment status URL: $statusUrl")
    }
}