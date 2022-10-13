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

import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.DependencyManagementType;
import co.bitshifted.appforge.ignite.model.Deployment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentInfoDlgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentInfoDlgController.class);

    @FXML
    private ComboBox<DependencyManagementType> dependencyCombo;
    @FXML
    private TextField projectLocationField;
    @FXML
    private TextField configFileNameField;

    @FXML
    public void initialize() {
        dependencyCombo.getItems().addAll(DependencyManagementType.values());
        dependencyCombo.getSelectionModel().select(DependencyManagementType.MAVEN);
        configFileNameField.setText(IgniteAppConstants.DEFAULT_CONFIG_FILE_NAME);
    }

    @FXML
    public void chooseProjectDirectory(ActionEvent event) {
        var parent = ((Parent)event.getTarget()).getScene().getWindow();
        var dirChooser = new DirectoryChooser();
        var selectedDir = dirChooser.showDialog(parent);
        if(selectedDir != null) {
            projectLocationField.setText(selectedDir.getAbsolutePath());
        }
    }

    public Callback<ButtonType, Deployment> getResultConverter() {
        return buttonType -> {
            if(buttonType == ButtonType.OK) {
                var deployment = new Deployment(projectLocationField.getText());
                deployment.setConfigFileName(configFileNameField.getText());
                LOGGER.debug("Config file name: {}", configFileNameField.getText());
                deployment.setDependencyManagementType(dependencyCombo.getSelectionModel().getSelectedItem());
                return deployment;
            }
            return null;
        };

    }

    public void clear() {
        projectLocationField.clear();
        configFileNameField.setText(IgniteAppConstants.DEFAULT_CONFIG_FILE_NAME);
    }

}
