/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.model.DependencyManagementType
import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.ui.UIRegistry
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.util.Callback

class NewProjectDialogController {

    @FXML
    private lateinit  var projectNameField : TextField
    @FXML
    private lateinit var projectLocationField : TextField
    @FXML
    private lateinit var dependencyCombo : ComboBox<DependencyManagementType>
    @FXML
    private lateinit var browseButton : Button

    @FXML
    fun initialize() {
        dependencyCombo.items?.addAll(DependencyManagementType.values())
        dependencyCombo.selectionModel?.selectFirst()
    }



    fun getResultConverter() : Callback<ButtonType, Project?> {
        return object : Callback<ButtonType, Project?> {
            override fun call(btype: ButtonType?): Project? {
                if(btype == ButtonType.OK) {
                    return createProject()
                }
                return null
            }
        }
    }

    fun validateInput() : Boolean {
        return (projectLocationField.text?.isNotEmpty() == true && projectNameField.text?.isNotEmpty() == true)
    }

    private fun createProject() : Project {
         val project = Project()
        project.name = projectNameField.text
        project.location = projectLocationField.text
        project.dependencyManagementType = dependencyCombo.value
        return project
    }

    @FXML
    fun chooseProjectDirectory() {
        val dirChooser = DirectoryChooser()
        val selectedDir = dirChooser.showDialog(UIRegistry.getMainWindow())
    }


}
