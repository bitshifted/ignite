/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ctrl

import co.bitshifted.appforge.ignite.model.Project
import co.bitshifted.appforge.ignite.model.RuntimeData
import co.bitshifted.appforge.ignite.persist.ProjectPersister
import co.bitshifted.appforge.ignite.ui.UIRegistry
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.DirectoryChooser


class StartPaneController {

    @FXML
    fun createProject() {

        val dialog = Dialog<Project?>()
        dialog.title = "Create New Project"
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialog.dialogPane.content = UIRegistry.getComponent(UIRegistry.PROJECT_INFO_PANE)
        dialog.resultConverter = ControllerRegistry.getController(ProjectInfoController::class.java).getResultConverter()
        dialog.dialogPane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION,  {
            if(!ControllerRegistry.getController(ProjectInfoController::class.java).validateInput()) {
                it.consume()
            }
        })


        val result = dialog.showAndWait()
        if (result.isPresent) {
            RuntimeData.projectList.add(result.get())
        }
    }

    @FXML
    fun openProject() {
        val dirChooser = DirectoryChooser()
        val selectedDir = dirChooser.showDialog(UIRegistry.getMainWindow())

        if(selectedDir != null) {
            val project = ProjectPersister.loadProject(selectedDir.absolutePath)
            RuntimeData.projectList.add(project)
        }
    }

}