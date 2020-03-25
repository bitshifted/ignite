/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl


import co.bitshifted.xapps.ignite.filePathRelative
import co.bitshifted.xapps.ignite.iconExtensionFilters
import co.bitshifted.xapps.ignite.model.BinaryData
import co.bitshifted.xapps.ignite.model.ProjectItemType
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.model.Server
import co.bitshifted.xapps.ignite.persist.ProjectPersistenceData
import co.bitshifted.xapps.ignite.ui.IconBar
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import co.bitshifted.xapps.ignite.ui.UIRegistry
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.io.File
import java.util.*

class AppInfoController : ChangeListener<ProjectTreeItem> {

    private val digestUtil = DigestUtils(MessageDigestAlgorithms.SHA_1)

    @FXML
    private lateinit var appNameField : TextField
    @FXML
    private lateinit var appVersionField : TextField


    @FXML
    private lateinit var iconsBarArea : VBox
    @FXML
    private lateinit var appIdField : TextField




    @FXML
    fun initialize() {
        RuntimeData.selectedProjectItem.addListener(this)
    }

    override fun changed(observable: ObservableValue<out ProjectTreeItem>?, oldValue: ProjectTreeItem?, newValue: ProjectTreeItem?) {

        if(oldValue?.type == ProjectItemType.APPLICATION) {
            appNameField.textProperty().unbindBidirectional(oldValue.project?.application?.info?.appNameProperty)
            appIdField.textProperty().unbindBidirectional(oldValue.project?.application?.appIdProperty)
            appVersionField.textProperty().unbindBidirectional(oldValue.project?.application?.versionProperty)
            iconsBarArea.children.remove(1, iconsBarArea.children.size) // remove children except 'Add' button
        }
        if(newValue?.type == ProjectItemType.APPLICATION) {
            appNameField.textProperty().bindBidirectional(newValue.project?.application?.info?.appNameProperty)
            appIdField.textProperty().bindBidirectional(newValue.project?.application?.appIdProperty)
            appVersionField.textProperty().bindBidirectional(newValue.project?.application?.versionProperty)
            for(binData in newValue.project?.application?.info?.icons ?: return) {
                iconsBarArea.children.add(IconBar(binData.path, newValue.project?.location ?: "", iconsBarArea))
            }
        }
    }

    @FXML
    fun addIcon() {
        val fileChooser = FileChooser()
        fileChooser.title = "Choose icons"
        fileChooser.initialDirectory = File(RuntimeData.selectedProjectItem.get().project?.location)
        fileChooser.extensionFilters.addAll(iconExtensionFilters())
        val selectedFiles = fileChooser.showOpenMultipleDialog(UIRegistry.getMainWindow())
        val projectLocation = RuntimeData.selectedProjectItem.get().project?.location ?: ""


        for(file in selectedFiles ?: return) {
            iconsBarArea.children.add(IconBar(file.absolutePath, projectLocation, iconsBarArea))
            RuntimeData.selectedProjectItem.get().project?.application?.info?.addIcon(BinaryData(filePathRelative(file.absolutePath, projectLocation), file.name, file.length()))
        }
    }


}