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

import co.bitshifted.appforge.common.dto.ApplicationDTO;
import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.SimpleHttpClient;
import co.bitshifted.appforge.ignite.model.*;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeploymentInfoController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentInfoController.class);

    private final SimpleHttpClient httpClient = new SimpleHttpClient();

    @FXML
    private TextField projectLocationField;
    @FXML
    private TextField configFileNameField;
    @FXML
    private ComboBox<DependencyManagementType> dependencyCombo;
    @FXML
    private ComboBox<Server> serverCombo;
    @FXML
    private ComboBox<ApplicationDTO> applicationsCombo;
    @FXML
    private ProgressIndicator appLoadProgress;
    @FXML
    private Label appListErrorLabel;

    @FXML
    public void initialize() {
        dependencyCombo.getItems().addAll(DependencyManagementType.values());
        serverCombo.itemsProperty().bindBidirectional(RuntimeData.getInstance().getServersList());
        applicationsCombo.setButtonCell(new ApplicationListCell());
        applicationsCombo.setCellFactory(applicationDTOListView -> new ApplicationListCell());
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        LOGGER.debug("DeploymentTreeItem changed");
        var deployment = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment();
        if(deployment != null && RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().type() == DeploymentItemType.DEPLOYMENT) {
            projectLocationField.textProperty().bind(deployment.getLocationProperty());
            configFileNameField.textProperty().bindBidirectional(deployment.getConfigFileNameProperty());
            dependencyCombo.valueProperty().bindBidirectional(deployment.getDependencyManagementTypeProperty());
            serverCombo.valueProperty().bindBidirectional(deployment.getConfiguration().serverProperty());
            serverCombo.getSelectionModel().selectedItemProperty().addListener((obsValue, old, newVal) -> {
                LOGGER.debug("Server selection changed");
            TaskExecutor.getInstance().start(new ListAppsWorker(deployment));
            });
            applicationsCombo.valueProperty().bindBidirectional(deployment.getConfiguration().applicationIdProperty());
            // load applications
            if(applicationsCombo.getItems().isEmpty()) {
                 TaskExecutor.getInstance().start(new ListAppsWorker(deployment));
            }

        }
    }

    private class ListAppsWorker extends Task<List<ApplicationDTO>> {

        private final SimpleHttpClient client;
        private final Deployment deployment;

        ListAppsWorker(Deployment deployment) {
            client = new SimpleHttpClient();
            this.deployment = deployment;
            valueProperty().addListener((observableValue, oldValue, newValue) -> {
                // update application based on ID
                var appOptional = newValue.stream().filter(d -> d.getId().equals(deployment.getConfiguration().getApplicationId())).findFirst();
                if(appOptional.isPresent()) {
                    LOGGER.debug("Found application with ID {}: {}", deployment.getConfiguration().getApplicationId(), appOptional.get());
                    DeploymentInfoController.this.applicationsCombo.setItems(FXCollections.observableList(newValue));
                    deployment.getConfiguration().applicationIdProperty().set(appOptional.get());
                }
            });
            stateProperty().addListener(((observableValue, oldValue, newValue) -> {
                LOGGER.debug("running property listener: {}", newValue);
                if(newValue == State.RUNNING) {
                    appListErrorLabel.setVisible(false);
                    Platform.runLater(() -> appLoadProgress.setVisible(true));
                } else {
                    Platform.runLater(() -> appLoadProgress.setVisible(false));
                }
                if(newValue == State.FAILED) {
                    LOGGER.error("list apps failed", getException());
                    var msg = processErrorMessage(getException());
                    appListErrorLabel.setText(msg);
                    appListErrorLabel.setVisible(true);
                }
            }));
        }

        @Override
        protected List<ApplicationDTO> call() throws Exception {
            return client.listApplication();
        }


    }

    private class ApplicationListCell extends ListCell<ApplicationDTO> {
        @Override
        protected void updateItem(ApplicationDTO applicationDTO, boolean empty) {
            super.updateItem(applicationDTO, empty);
            if(!empty && applicationDTO != null) {
                setText(applicationDTO.getName());
            } else {
                setText(null);
            }
        }
    }

    private String processErrorMessage(Throwable ex) {
        var sb = new StringBuilder();
        var msg = ex.getMessage();
        if (msg != null) {
            sb.append(msg);
        }
        var cause = ex.getCause();
        if(cause != null) {
            var causeMsg = cause.getMessage();
            sb.append("\n").append(causeMsg);
        }
        return sb.toString();
    }
}
