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


import co.bitshifted.appforge.ignite.logger
import co.bitshifted.appforge.ignite.maven.DependencySyncTask
import co.bitshifted.appforge.ignite.model.*
import co.bitshifted.appforge.ignite.ui.ProjectTreeItem
import co.bitshifted.appforge.ignite.ui.fillMavenDependencyTable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox


class DependencyController : ListChangeListener<Project>, ChangeListener<ProjectTreeItem> {

    private val logger by logger(DependencyController::class.java)

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
        RuntimeData.projectList.addListener(this)
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

        RuntimeData.fileChangeQueue.addListener(ListChangeListener {
            it.next()
            if(it.wasAdded()) {
                it.addedSubList.forEach {
                    it.synced = false
                    syncDependencies(it)
                }
            }
        })
    }

    override fun onChanged(change: ListChangeListener.Change<out Project>?) {
        while (change?.next() == true && change.wasAdded() == true) {
            for(project in (change.addedSubList ?: emptyList())) {
                logger.debug("Syncing dependencies for project ${project.name}")
              syncDependencies(project)
            }
        }
    }

    override fun changed(
        observable: ObservableValue<out ProjectTreeItem>?,
        oldItem: ProjectTreeItem?,
        newItem: ProjectTreeItem?
    ) {
        val project = newItem?.project ?: return
        if (newItem.type == ProjectItemType.DEPENDENCIES && project.synced) {
            logger.debug("Dependencies for project ${project.name}: ${project.jvm.dependenciesProperty}")
            fillMavenDependencyTable(project.jvm.dependenciesProperty, dependencyTable)
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
                        dependenciesPane.toFront()
                        dependenciesPane.isVisible = true
                        buttonBar.isVisible = true
                        project.synced = true
                    }
                    else -> { println("Do nothing")}

                }
            }
        }
    }


    private fun markDependencyAsPlatform(os : OperatingSystem) {
        val selectedItems = dependencyTable.selectionModel.selectedItems
        RuntimeData.selectedProjectItem.get().project?.jvm?.platformDependencies?.addPlatformSpecificDependency(selectedItems, os)
        dependencyTable.items.removeAll(selectedItems)
    }

    private fun processDependencyChangeNotification() {
        while(true) {

        }
    }
}