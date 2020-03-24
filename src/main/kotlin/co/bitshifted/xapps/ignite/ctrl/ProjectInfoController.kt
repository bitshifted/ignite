/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.DependencyManagementType
import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.ProjectItemType
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import co.bitshifted.xapps.ignite.ui.UIRegistry
import javafx.beans.binding.Bindings
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.util.Callback


class ProjectInfoController : ChangeListener<ProjectTreeItem> {

    private val log by logger(ProjectInfoController::class.java)

    @FXML
    private lateinit var projectNameField : TextField
    @FXML
    private lateinit var projectLocationField : TextField
    @FXML
    private lateinit var   dependencyCombo : ComboBox<DependencyManagementType>

    private var boundProject : Project? = null


    @FXML
    fun initialize() {
        RuntimeData.selectedProjectItem.addListener(this)
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


    override fun changed(observable: ObservableValue<out ProjectTreeItem>?, oldValue: ProjectTreeItem?, newValue: ProjectTreeItem?) {

        if(oldValue?.type == ProjectItemType.PROJECT ) {
            unbindData(oldValue?.project ?: return)
        }
        if(newValue?.type == ProjectItemType.PROJECT) {
            log.debug("Selected project: ${newValue.project?.name}")
            bindData(newValue.project ?: return)
        }

    }


    @FXML
    fun chooseProjectDirectory(event : ActionEvent) {
        log.debug("Choose project directory")
        val parent = (event.target as Node).scene.window
        val dirChooser = DirectoryChooser()
        val selectedDir = dirChooser.showDialog(parent)
        if(selectedDir != null) {
            projectLocationField.text = selectedDir.absolutePath
        }
    }

    private fun createProject() : Project {
        val project = Project()
        project.name = projectNameField.text
        project.location = projectLocationField.text
        project.dependencyManagementType = dependencyCombo.value
        return project
    }

    private fun bindData(project : Project) {
        projectNameField.textProperty().bindBidirectional(project.nameProperty)
        projectLocationField.textProperty().bindBidirectional(project.locationProperty)
        dependencyCombo.valueProperty().bindBidirectional(project.dependencyManagementTypeProperty)
        boundProject = project

    }

    private fun unbindData(project: Project) {
        projectNameField.textProperty().unbindBidirectional(project.nameProperty)
        projectLocationField.textProperty().unbindBidirectional(project.locationProperty)
        dependencyCombo.valueProperty().unbindBidirectional(project.dependencyManagementTypeProperty)
        boundProject = null
        projectNameField.text = null
        projectLocationField.text = null
    }
}