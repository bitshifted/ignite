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

import co.bitshifted.appforge.ignite.model.DependencyManagementType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentInfoController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentInfoController.class);

    @FXML
    private TextField projectLocationField;
    @FXML
    private TextField configFileNameField;
    @FXML
    private ComboBox<DependencyManagementType> dependencyCombo;
    @FXML
    private ComboBox<Server> serverCombo;

    @FXML
    public void initialize() {
        dependencyCombo.getItems().addAll(DependencyManagementType.values());
        serverCombo.itemsProperty().bindBidirectional(RuntimeData.getInstance().getServersList());
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        LOGGER.debug("DeploymentTreeItem changed");
        var deployment = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment();
        if(deployment != null) {
            projectLocationField.textProperty().bind(deployment.getLocationProperty());
            configFileNameField.textProperty().bindBidirectional(deployment.getConfigFileNameProperty());
            dependencyCombo.valueProperty().bindBidirectional(deployment.getDependencyManagementTypeProperty());
        }

    }
}
