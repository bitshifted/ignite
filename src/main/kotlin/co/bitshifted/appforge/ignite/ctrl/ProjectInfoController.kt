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

import co.bitshifted.appforge.ignite.DEFAULT_CONFIG_FILE_NAME
import co.bitshifted.appforge.ignite.logger
import co.bitshifted.appforge.ignite.model.*
import co.bitshifted.appforge.ignite.persist.ProjectPersistenceData
import co.bitshifted.appforge.ignite.ui.ProjectTreeItem
import co.bitshifted.appforge.ignite.ui.ServerListCell
import co.bitshifted.appforge.ignite.ui.UIRegistry
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.util.Callback
import java.util.*


class ProjectInfoController : ChangeListener<ProjectTreeItem> {

    private val log by logger(ProjectInfoController::class.java)

    @FXML
    private lateinit var configFileNameField : TextField
    @FXML
    private lateinit var projectLocationField : TextField
    @FXML
    private lateinit var   dependencyCombo : ComboBox<DependencyManagementType>
    @FXML
    private lateinit var serverCombo : ComboBox<Server>

    private var boundProject : Project? = null
    private lateinit var resourceBundle: ResourceBundle


    @FXML
    fun initialize() {
        serverCombo.items.addAll(ProjectPersistenceData.loadServers())
        serverCombo.buttonCell = ServerListCell()
        serverCombo.cellFactory = Callback { ServerListCell() }
        resourceBundle = ResourceBundle.getBundle("i18n/strings")
        RuntimeData.selectedProjectItem.addListener(this)
        dependencyCombo.items?.addAll(DependencyManagementType.values())
        dependencyCombo.selectionModel?.selectFirst()
        configFileNameField.text = DEFAULT_CONFIG_FILE_NAME

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
        return (projectLocationField.text?.isNotEmpty() == true && configFileNameField.text?.isNotEmpty() == true && serverCombo.selectionModel.selectedItem != null)
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

    @FXML
    fun addServer() {
        val dialog = Dialog<Server?>()
        dialog.title = resourceBundle.getString("app.newServer")
        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialog.dialogPane.content = UIRegistry.getComponent(UIRegistry.ADD_SERVER_PANE)
        dialog.resultConverter = ControllerRegistry.getController(AddServerController::class.java).getResultConverter()
        dialog.dialogPane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION,  {
            if(!ControllerRegistry.getController(AddServerController::class.java).validateInput()) {
                it.consume()
            }
        })
        val optServer = dialog.showAndWait()
        if(optServer.isPresent) {
            serverCombo.items.add(optServer.get())
            serverCombo.selectionModel.select(optServer.get())
        }

    }

    private fun createProject() : Project {
        val project = Project(IgniteConfig(), projectLocationField.text, configFileNameField.text)
        project.name = configFileNameField.text
//        project.location = projectLocationField.text
        project.dependencyManagementType = dependencyCombo.value
        return project
    }

    private fun bindData(project : Project) {
        configFileNameField.textProperty().bindBidirectional(project.nameProperty)
//        projectLocationField.textProperty().bindBidirectional(project.locationProperty)
        dependencyCombo.valueProperty().bindBidirectional(project.dependencyManagementTypeProperty)
        serverCombo.valueProperty().bindBidirectional(project.serverProperty)
        boundProject = project

    }

    private fun unbindData(project: Project) {
        configFileNameField.textProperty().unbindBidirectional(project.nameProperty)
//        projectLocationField.textProperty().unbindBidirectional(project.locationProperty)
        dependencyCombo.valueProperty().unbindBidirectional(project.dependencyManagementTypeProperty)
        boundProject = null
        configFileNameField.text = null
        projectLocationField.text = null
    }
}