/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.ProjectItemType
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.persist.ProjectPersistenceData
import co.bitshifted.xapps.ignite.persist.XMLPersister
import co.bitshifted.xapps.ignite.showAlert
import co.bitshifted.xapps.ignite.ui.ProjectTreeCellFactory
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import co.bitshifted.xapps.ignite.ui.UIRegistry
import co.bitshifted.xapps.ignite.watch.PomWatcher
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.AnchorPane
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.devicons.Devicons
import org.kordamp.ikonli.fontawesome.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon
import java.lang.Exception

class MainPageController : ListChangeListener<Project>  {

    private val log by logger(MainPageController::class.java)

    private val anchorDistance = 10.0
    private val treeIconsSIze = 17

    @FXML
    private lateinit var detailsPane : AnchorPane
    @FXML
    private lateinit var projectTree : TreeView<ProjectTreeItem>


    @FXML
    fun initialize() {
        createProjectTree();
        detailsPane.children.add(UIRegistry.getComponent(UIRegistry.START_PANE))
        RuntimeData.projectList.addListener(this)
    }

    private fun createProjectTree() {
        projectTree.cellFactory = ProjectTreeCellFactory()
        val root = TreeItem(ProjectTreeItem(ProjectItemType.ROOT), getIcon(FontAwesome.POWER_OFF))
        root.expandedProperty().set(true)
        projectTree.root = root

        Platform.runLater {
            for(location in ProjectPersistenceData.loadProjectLocations()) {
                try {
                    RuntimeData.projectList.addAll(XMLPersister.loadProject(location))
                } catch (ex : Exception) {
                    log.error("Failed to laod project file", ex)
                    showAlert(Alert.AlertType.ERROR, "Failed to load project", ex.message ?: "")
                }

            }
        }

        projectTree.selectionModel?.selectedItemProperty()?.addListener(object:
            ChangeListener<TreeItem<ProjectTreeItem>> {
            override fun changed(
                observable: ObservableValue<out TreeItem<ProjectTreeItem>>?,
                oldValue : TreeItem<ProjectTreeItem>?,
                newValue : TreeItem<ProjectTreeItem>?
            ) {
                RuntimeData.selectedProjectItem.value = newValue?.value
                when(newValue?.value?.type) {
                    ProjectItemType.ROOT -> {
                        detailsPane.children.clear()
                        detailsPane.children.add(UIRegistry.getComponent(UIRegistry.START_PANE))
                        AnchorPane.setTopAnchor(detailsPane.children[0], 0.0)
                        AnchorPane.setLeftAnchor(detailsPane.children[0], 0.0)
                        AnchorPane.setRightAnchor(detailsPane.children[0], 0.0)
//                        detailsPane.bottom = null
                    }
                    ProjectItemType.PROJECT -> {
                        setupDetailsPane(UIRegistry.PROJECT_INFO_PANE)
                    }
                    ProjectItemType.APPLICATION -> {
                        setupDetailsPane(UIRegistry.APP_INFO_PANE)
                    }
                    ProjectItemType.JVM -> {
                        setupDetailsPane(UIRegistry.JVM_PROPERTIES_PANE)
                    }
                    ProjectItemType.DEPENDENCIES -> {
                        setupDetailsPane(UIRegistry.DEPENDENCY_INFO_PANE)
                    }

                }
            }
        })
    }

    override fun onChanged(change: ListChangeListener.Change<out Project>?) {
        while (change?.next() == true && change.wasAdded() == true) {
            for(project in (change.addedSubList ?: emptyList())) {
                projectTree.root?.children?.add(createProjectNode(project))
                ProjectPersistenceData.saveProject(project)
                XMLPersister.writeProject(project);
            }
        }
    }

    private fun createProjectNode(project : Project) : TreeItem<ProjectTreeItem> {
        val projectNode = TreeItem(ProjectTreeItem(ProjectItemType.PROJECT, project), getIcon(FontAwesome.BRIEFCASE))
        projectNode.children.add(TreeItem(ProjectTreeItem(ProjectItemType.APPLICATION, project), getIcon(FontAwesome.FLASK)))
        projectNode.children.add(TreeItem(ProjectTreeItem(ProjectItemType.JVM, project), getIcon(Devicons.JAVA)))
        projectNode.children.add(TreeItem(ProjectTreeItem(ProjectItemType.DEPENDENCIES, project), getIcon(FontAwesome.FILE_CODE_O)))

        return projectNode
    }

    private fun setupDetailsPane(name : String) {
        detailsPane.children.clear()
        detailsPane.children.addAll(UIRegistry.getComponent(name), UIRegistry.getComponent(UIRegistry.PROJECT_BUTTON_BAR))
        AnchorPane.setTopAnchor(detailsPane.children[0], anchorDistance)
        AnchorPane.setLeftAnchor(detailsPane.children[0], anchorDistance)
        AnchorPane.setRightAnchor(detailsPane.children[0], anchorDistance)
        AnchorPane.setBottomAnchor(detailsPane.children[1], anchorDistance)
    }

    private fun getIcon(font : Ikon) : FontIcon {
        val icon = FontIcon(font);
        icon.iconSize = treeIconsSIze
        return icon
    }


}