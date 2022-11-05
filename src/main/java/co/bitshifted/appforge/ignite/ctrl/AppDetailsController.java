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
import co.bitshifted.appforge.ignite.http.CreateAppTask;
import co.bitshifted.appforge.ignite.http.SimpleHttpClient;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDetailsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDetailsController.class);

    @FXML
    private TextField appNameField;
    @FXML
    private TextArea headlineField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField homePageField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField publisherEmailField;
    @FXML
    private ProgressIndicator operationProgress;
    @FXML
    private Label errorLabel;

    private final ApplicationDTO applicationDTO;
    private final String serverUrl;

    public AppDetailsController(String serverUrl) {
        this.applicationDTO = new ApplicationDTO();
        this.serverUrl = serverUrl;
    }

    public AppDetailsController(ApplicationDTO applicationDTO, String serverUrl) {
        this.applicationDTO = applicationDTO;
        this.serverUrl = serverUrl;
    }

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing AppDetailsController");
        appNameField.setText(applicationDTO.getName());
        headlineField.setText(applicationDTO.getHeadline());
        descriptionField.setText(applicationDTO.getDescription());
        homePageField.setText(applicationDTO.getHomePageUrl());
        publisherField.setText(applicationDTO.getPublisher());
        publisherEmailField.setText(applicationDTO.getPublisherEmail());
    }

    public Callback<ButtonType, ApplicationDTO> getResultConverter() {
        return  buttonType -> {
            if(buttonType == ButtonType.OK) {
                applicationDTO.setId("122");
                return applicationDTO;
            }
            return null;
        };
    }

    public ApplicationDTO submitApplication() {
        applicationDTO.setName(appNameField.getText());
        applicationDTO.setHeadline(headlineField.getText());
        applicationDTO.setDescription(descriptionField.getText());
        applicationDTO.setHomePageUrl(homePageField.getText());
        applicationDTO.setPublisher(publisherField.getText());
        applicationDTO.setPublisherEmail(publisherEmailField.getText());
       var task = new CreateAppTask(applicationDTO, serverUrl);
       task.stateProperty().addListener((obs, oldVal, newVal) -> {
           switch (newVal) {
               case RUNNING -> {
                   operationProgress.setVisible(true);
               }
               case FAILED -> {
                   operationProgress.setVisible(false);
                   errorLabel.setVisible(true);
                   errorLabel.setText(task.processErrorMessage(":"));
               }
               default -> {
                   operationProgress.setVisible(false);
                   errorLabel.setVisible(false);
               }
           }
       });
        TaskExecutor.getInstance().start(task);
        try {
            var result =  task.get();
            applicationDTO.setId(result.getId());
            return applicationDTO;
        } catch(Exception ex) {
            LOGGER.error("failed to create application", ex);
            return null;
        }

    }

}
