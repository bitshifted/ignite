/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl


import co.bitshifted.xapps.ignite.maven.DependencySyncTask
import co.bitshifted.xapps.ignite.model.*
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import co.bitshifted.xapps.ignite.ui.fillMavenDependencyTable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox


class DependencyController : ChangeListener<ProjectTreeItem> {

    @FXML
    private lateinit var progressPane: BorderPane
    @FXML
    private lateinit var dependenciesPane: AnchorPane
    @FXML
    private lateinit var syncProgressIndicator: ProgressIndicator
    @FXML
    private lateinit var dependencyTable: TableView<JvmDependency>
    @FXML
    private lateinit var buttonBar: HBox
    @FXML
    private lateinit var platformButton : MenuButton
    @FXML
    private lateinit var osxMenuItem : MenuItem
    @FXML
    private lateinit var windowsMenuItem : MenuItem
    @FXML
    private lateinit var linuxMenuItem : MenuItem

    private val taskStatusProperty = SimpleBooleanProperty()

    @FXML
    fun initialize() {
        RuntimeData.selectedProjectItem.addListener(this)
        progressPane.toBack()
        dependenciesPane.isVisible = true
        dependencyTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
        dependencyTable.isEditable = true

        dependencyTable.selectionModel.selectedItemProperty().addListener { _, _, newSel ->
            platformButton.disableProperty().value = (newSel == null)
        }

        osxMenuItem.setOnAction { _ -> markDependencyAsPlatform(OperatingSystem.MAC_OS_X) }
        windowsMenuItem.setOnAction { _ -> markDependencyAsPlatform(OperatingSystem.WINDOWS) }
        linuxMenuItem.setOnAction { _ -> markDependencyAsPlatform(OperatingSystem.LINUX) }
    }

    override fun changed(
        observable: ObservableValue<out ProjectTreeItem>?,
        oldItem: ProjectTreeItem?,
        newItem: ProjectTreeItem?
    ) {
        val project = newItem?.project ?: return
        if (newItem.type == ProjectItemType.DEPENDENCIES) {
            syncDependencies(project)
        }
    }

    private fun syncDependencies(project: Project) {
        if (!project.synced) {
            syncFromPom(project)
        } else {
            fillMavenDependencyTable(project.jvm.dependenciesProperty, dependencyTable)
        }
    }

    private fun syncFromPom(project: Project) {
        if (!taskStatusProperty.value) {
            val task = DependencySyncTask(project)
            taskStatusProperty.bind(task.runningProperty())
            syncProgressIndicator.progressProperty().bind(task.progressProperty())
            Thread(task).start()
            task.stateProperty().addListener { _, _, newState ->
                when (newState) {
                    Worker.State.RUNNING -> {
                        progressPane.toFront()
                        dependenciesPane.isVisible = false
                        buttonBar.isVisible = false
                    }
                    Worker.State.SUCCEEDED -> {
                        fillMavenDependencyTable(project.jvm.dependenciesProperty, dependencyTable)
                        dependenciesPane.toFront()
                        dependenciesPane.isVisible = true
                        buttonBar.isVisible = true
                        project.synced = true
                    }

                }
            }
        }
    }


    private fun markDependencyAsPlatform(os : OperatingSystem) {
        val selectedItems = dependencyTable.selectionModel.selectedItems
        RuntimeData.selectedProjectItem.get().project?.jvm?.platformDependencies?.addPlatformSpecificDependency(selectedItems, os)
        dependencyTable.items.removeAll(selectedItems)
    }
}