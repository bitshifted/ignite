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

import co.bitshifted.appforge.ignite.filePathRelative
import co.bitshifted.appforge.ignite.iconExtensionFilters
import co.bitshifted.appforge.ignite.model.*
import co.bitshifted.appforge.ignite.ui.ProjectTreeItem
import co.bitshifted.appforge.ignite.ui.UIRegistry
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.Callback
import java.io.File
import java.nio.file.Paths

class JvmDataController : ChangeListener<ProjectTreeItem> {

    private val SPLASH_PREVIEW_WIDTH = 100.0
    private val SPLASH_PREVIEW_HEIGHT = 80.0

    @FXML
    private lateinit var mainClassField : TextField
    @FXML
    private lateinit var moduleNameField : TextField
    @FXML
    private lateinit var jvmOptionsTextArea : TextArea
    @FXML
    private lateinit var sysPropertiesTextArea: TextArea
    @FXML
    private lateinit var argumentsTextField : TextField
    @FXML
    private lateinit var splashScreenImgPane : BorderPane
    @FXML
    private lateinit var providerCombo : ComboBox<JdkProvider>
    @FXML
    private lateinit var jvmImplementationCombo : ComboBox<JvmImplementation>
    @FXML
    private lateinit var jdkVersionCombo : ComboBox<JdkVersion>



    @FXML
    fun initialize() {
        RuntimeData.selectedProjectItem.addListener(this)
        providerCombo.items = FXCollections.observableList(JdkProvider.values().toList())
        jvmImplementationCombo.items = FXCollections.observableList(JvmImplementation.values().toList())

        jdkVersionCombo.items = FXCollections.observableList(JdkVersion.values().toList())
        jdkVersionCombo.buttonCell = JdkVersionListCell()
        jdkVersionCombo.cellFactory = Callback {  JdkVersionListCell() }
    }

    override fun changed(observable: ObservableValue<out ProjectTreeItem>?, oldValue: ProjectTreeItem?, newValue: ProjectTreeItem?) {
        if(oldValue?.type == ProjectItemType.JVM) {
            mainClassField.textProperty().unbindBidirectional(oldValue.project?.jvm?.mainClassProperty)
            moduleNameField.textProperty().unbindBidirectional(oldValue.project?.jvm?.moduleNameProperty)
            jvmOptionsTextArea.textProperty().unbindBidirectional(oldValue.project?.jvm?.jvmOptionsProperty)
            sysPropertiesTextArea.textProperty().unbindBidirectional(oldValue.project?.jvm?.sysPropertiesProperty)
            argumentsTextField.textProperty().unbindBidirectional(oldValue.project?.jvm?.argumentsProperty)
            splashScreenImgPane.center = null
            providerCombo.valueProperty().unbindBidirectional(oldValue.project?.jvm?.jdkProviderProperty)
            jvmImplementationCombo.valueProperty().unbindBidirectional(oldValue.project?.jvm?.jvmImplementationProperty)
            jdkVersionCombo.valueProperty().unbindBidirectional(oldValue.project?.jvm?.jdkVersionProperty)
        }
        if(newValue?.type == ProjectItemType.JVM) {
            mainClassField.textProperty().bindBidirectional(newValue.project?.jvm?.mainClassProperty)
            moduleNameField.textProperty().bindBidirectional(newValue.project?.jvm?.moduleNameProperty)
            jvmOptionsTextArea.textProperty().bindBidirectional(newValue.project?.jvm?.jvmOptionsProperty)
            sysPropertiesTextArea.textProperty().bindBidirectional(newValue.project?.jvm?.sysPropertiesProperty)
            argumentsTextField.textProperty().bindBidirectional(newValue.project?.jvm?.argumentsProperty)
            val projectLocation = newValue.project?.location ?: return
            val splashScreenPath = newValue.project?.jvm?.splashScreen?.path ?: return
            splashScreenImgPane.center = ImageView(Image(getIconFile(splashScreenPath, projectLocation).inputStream(), SPLASH_PREVIEW_WIDTH, SPLASH_PREVIEW_HEIGHT, false, false))
            providerCombo.valueProperty().bindBidirectional(newValue.project?.jvm?.jdkProviderProperty)
            jvmImplementationCombo.valueProperty().bindBidirectional(newValue.project?.jvm?.jvmImplementationProperty)
            jdkVersionCombo.valueProperty().bindBidirectional(newValue.project?.jvm?.jdkVersionProperty)
        }
    }

    @FXML
    fun selectSplashScreenImage() {
        val fileChooser = FileChooser()
        fileChooser.title = "Choose splash screen image"
        fileChooser.initialDirectory = File(RuntimeData.selectedProjectItem.get().project?.location)
        fileChooser.extensionFilters.addAll(iconExtensionFilters())
        val selectedFile = fileChooser.showOpenDialog(UIRegistry.getMainWindow())
        val projectLocation = RuntimeData.selectedProjectItem.get().project?.location ?: ""

        val splashScreenView = ImageView(Image(getIconFile(selectedFile.absolutePath, projectLocation).inputStream(), SPLASH_PREVIEW_WIDTH, SPLASH_PREVIEW_HEIGHT, false, false))
        splashScreenImgPane.center = splashScreenView
        RuntimeData.selectedProjectItem.get().project?.jvm?.splashScreen = BinaryData(filePathRelative(selectedFile.absolutePath, projectLocation), selectedFile.name,  selectedFile.length())
    }

    @FXML
    fun removeSplashScreen() {
        splashScreenImgPane.center = null
        RuntimeData.selectedProjectItem.get().project?.jvm?.splashScreen = null
    }


    private fun getIconFile(path : String, projectLocation: String) : File {
        val p = Paths.get(path)
        if(p.isAbsolute) {
            return p.toFile()
        } else {
            return File(projectLocation, path)
        }
    }


    class JdkVersionListCell : ListCell<JdkVersion>() {
        override fun updateItem(item: JdkVersion?, empty: Boolean) {
            super.updateItem(item, empty)
            if(item != null && !empty) {
                text = item.display
            } else {
                text = null
            }
        }
    }

}