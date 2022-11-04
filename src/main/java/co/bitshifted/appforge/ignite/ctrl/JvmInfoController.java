/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ctrl;

import co.bitshifted.appforge.common.dto.JvmConfigurationDTO;
import co.bitshifted.appforge.common.model.JavaVersion;
import co.bitshifted.appforge.common.model.JvmVendor;
import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.ui.JvmConfigUiModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JvmInfoController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JvmInfoController.class);

    @FXML
    private ComboBox<JvmVendor> vendorComboBox;
    @FXML
    private ComboBox<JavaVersion> versionComboBox;
    @FXML
    private TextField mainClassTextField;
    @FXML
    private TextField moduleNameTextField;
    @FXML
    private TextField argumentsTextField;
    @FXML
    private TextArea jvmOptionsField;
    @FXML
    private TextArea systemPropertiesField;

    @FXML
    public void initialize() {
        vendorComboBox.getItems().addAll(JvmVendor.values());
        versionComboBox.getItems().addAll(JavaVersion.values());

        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.JVM) {
            LOGGER.debug("JVM option selected");
            var selectedDeployment = newValue.deployment();
            var jvmConfigUiModel = selectedDeployment.getConfiguration().jvmConfigurationPropertyProperty().get();
            if(jvmConfigUiModel == null) {
                jvmConfigUiModel = new JvmConfigUiModel(new JvmConfigurationDTO());
                RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().jvmConfigurationPropertyProperty().set(jvmConfigUiModel);
            }
            vendorComboBox.valueProperty().bindBidirectional(jvmConfigUiModel.jvmVendorProperty());
            versionComboBox.valueProperty().bindBidirectional(jvmConfigUiModel.javaVersionProperty());
            mainClassTextField.textProperty().bindBidirectional(jvmConfigUiModel.mainClassProperty());
            moduleNameTextField.textProperty().bindBidirectional(jvmConfigUiModel.moduleNameProperty());
            argumentsTextField.textProperty().bindBidirectional(jvmConfigUiModel.argumentsProperty());
            jvmOptionsField.textProperty().bindBidirectional(jvmConfigUiModel.jvmOptionsProperty());
            systemPropertiesField.textProperty().bindBidirectional(jvmConfigUiModel.systemPropertiesProperty());
        }
    }
}
