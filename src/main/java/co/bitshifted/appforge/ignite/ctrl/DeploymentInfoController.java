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

import co.bitshifted.appforge.ignite.Ignite;
import co.bitshifted.appforge.ignite.IgniteConstants;
import co.bitshifted.appforge.ignite.model.DependencyManagementType;
import co.bitshifted.appforge.ignite.model.Deployment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class DeploymentInfoController {

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
        configFileNameField.setText(IgniteConstants.DEFAULT_CONFIG_FILE_NAME);
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
                return new Deployment(projectLocationField.getText(), configFileNameField.getText(), dependencyCombo.getSelectionModel().getSelectedItem());
            }
            return null;
        };

    }
}
