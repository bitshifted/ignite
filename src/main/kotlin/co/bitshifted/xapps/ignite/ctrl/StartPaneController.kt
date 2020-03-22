/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.persist.XMLPersister
import co.bitshifted.xapps.ignite.ui.UIRegistry
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
        dialog.dialogPane.content = UIRegistry.getComponent(UIRegistry.NEW_PROJECT_DLG)
        dialog.resultConverter = ControllerRegistry.getController(NewProjectDialogController::class.java).getResultConverter()
        dialog.dialogPane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION,  {
            if(!ControllerRegistry.getController(NewProjectDialogController::class.java).validateInput()) {
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
            val project = XMLPersister.loadProject(selectedDir.absolutePath)
            RuntimeData.projectList.add(project)
        }
    }

}