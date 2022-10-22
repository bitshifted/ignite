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
import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.ListAppsTask;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.ui.ApplicationListCellFactory;
import co.bitshifted.appforge.ignite.ui.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public class AppManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppManagementController.class);

    private ResourceBundle bundle;
    @FXML
    private ComboBox<Server> serversCombo;
    @FXML
    private Label serverErrorLabel;
    @FXML
    private ListView<ApplicationDTO> appsList;
    @FXML
    private ProgressIndicator appLoadProgress;
    @FXML
    private Button newAppButton;
    @FXML
    private Button editAppButton;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        serversCombo.itemsProperty().bind(RuntimeData.getInstance().getServersList());
        appsList.setCellFactory(new ApplicationListCellFactory());
        appsList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> editAppButton.setDisable(newVal == null));
        serversCombo.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldServer, newServer) -> {
            LOGGER.debug("server selection changed");
            if (newServer != null) {
                newAppButton.setDisable(false);
            }
            var task = new ListAppsTask(newServer.getBaseUrl());
            task.valueProperty().addListener((obs, oldVal, newVal) -> {
                appsList.getItems().clear();
                appsList.getItems().addAll(newVal);
            });
            task.stateProperty().addListener((obs, oldVal, newVal) -> {
                switch (newVal) {
                    case RUNNING -> {
                        appLoadProgress.setVisible(true);
                        serverErrorLabel.setVisible(false);
                    }
                    case FAILED -> {
                        serverErrorLabel.setVisible(true);
                        serverErrorLabel.setText(task.processErrorMessage(":"));
                        appLoadProgress.setVisible(false);
                    }
                    default -> {
                        appLoadProgress.setVisible(false);
                        serverErrorLabel.setVisible(false);
                    }
                }
            });
            TaskExecutor.getInstance().start(task);
        }));
    }

    @FXML
    public void onNewApplication() {
        try {
            var controller = new AppDetailsController(serversCombo.getSelectionModel().getSelectedItem().getBaseUrl());
            var dialog = DialogBuilder.newBuilder(ApplicationDTO.class)
                .withTitle(bundle.getString("app.details.dialog.title"))
                .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
                .withFxmlContent("/fxml/app-details-pane.fxml", bundle, controller)
                .build();
            dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ANY, event -> {
                LOGGER.debug("Running dialog event filter");
                var app = controller.submitApplication();
                if (app == null) {
                    event.consume();
                } else {
                    appsList.getItems().add(app);
                }
            });

            dialog.showAndWait();
        } catch (IOException ex) {
            LOGGER.error("Failed to create dialog", ex);
        }
    }

    @FXML
    public void onEditApplication() {
        var selectedApp = appsList.getSelectionModel().getSelectedItem();
        var controller = new AppDetailsController(selectedApp, serversCombo.getSelectionModel().getSelectedItem().getBaseUrl());
        try {
            var dialog = DialogBuilder.newBuilder(ApplicationDTO.class)
                .withTitle(bundle.getString("app.details.dialog.title"))
                .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
                .withFxmlContent("/fxml/app-details-pane.fxml", bundle, controller)
                .build();
            dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ANY, event -> {
                LOGGER.debug("Running dialog event filter");
                var app = controller.submitApplication();
                if (app == null) {
                    event.consume();
                } else {
                    appsList.getItems().add(app);
                }
            });
            dialog.showAndWait();
        } catch(IOException ex) {
            LOGGER.error("failed to create dialog", ex);
        }
    }
}
