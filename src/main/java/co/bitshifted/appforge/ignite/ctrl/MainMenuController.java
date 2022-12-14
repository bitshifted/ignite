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
import co.bitshifted.appforge.common.dto.jdk.InstalledJdkDTO;
import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.Deployment;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.persist.UserDataPersister;
import co.bitshifted.appforge.ignite.ui.DialogBuilder;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenuController.class);

    private final ResourceBundle bundle;

    @FXML
    private MenuItem newDeploymentMenuItem;
    @FXML
    private MenuItem serverConfigMenuItem;
    @FXML
    private MenuItem appConfigMenuItem;
    @FXML
    private MenuItem javaPlatformsMenuItem;

    public MainMenuController() {
        this.bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
    }

    @FXML
    public void newDeploymentAction() {
        var dialog = DialogBuilder.newBuilder(Deployment.class)
            .withTitle(bundle.getString("deployment.dialog.title"))
            .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .withContent(UIRegistry.instance().getComponent(UIRegistry.DEPLOYMENT_INFO_DLG))
            .withResultConverter(ControllerRegistry.instance().getController(DeploymentInfoDlgController.class).getResultConverter())
            .build();
        ControllerRegistry.instance().getController(DeploymentInfoDlgController.class).clear();

        var result = dialog.showAndWait();
        if(result.isPresent()) {
            RuntimeData.getInstance().addDeployment(result.get());
            try {
                UserDataPersister.instance().save(RuntimeData.getInstance().getUserData());
            } catch(IOException ex) {
                LOGGER.error("Failed to save deployment list", ex);
            }
        }
    }

    @FXML
    public void openDeploymentAction() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("chooser.file.deployment"));
        var extFilter = new FileChooser.ExtensionFilter("YAML files (*.yaml, *.yml)", "*.yaml", "*.yml");
        fileChooser.getExtensionFilters().add(extFilter);
        var selectedFile = fileChooser.showOpenDialog(UIRegistry.instance().getMainWindow());
       if (selectedFile != null) {
           var deployment = new Deployment(selectedFile.getParent());
           deployment.setConfigFileName(selectedFile.getName());
           deployment.initConfiguration();
           RuntimeData.getInstance().addDeployment(deployment);
           try {
               UserDataPersister.instance().save(RuntimeData.getInstance().getUserData());
           } catch(IOException ex) {
               LOGGER.error("Failed to save deployment list", ex);
           }
       }
    }

    @FXML
    public void closeDeploymentAction() {
        LOGGER.debug("Removing deployment");
        var deployment = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment();
        RuntimeData.getInstance().removeDeployment(deployment);
        try {
            UserDataPersister.instance().save(RuntimeData.getInstance().getUserData());
        } catch(IOException ex) {
            LOGGER.error("Failed to save deployment list", ex);
        }
    }

    @FXML
    public void onExitAction() {
        System.exit(0);
    }

    @FXML
    public void serverConfigMenuAction() {
        var dialog = DialogBuilder.newBuilder(Server.class)
            .withTitle(bundle.getString("server.mgmt.dialog.title"))
            .withContent(UIRegistry.instance().getComponent(UIRegistry.SERVER_MANAGEMENT))
            .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .build();
         dialog.showAndWait();

    }

    @FXML
    public void appConfigMenuAction() {
        try {
            var dialog = DialogBuilder.newBuilder(ApplicationDTO.class)
                .withTitle(bundle.getString("app.mgmt.dialog.title"))
                .withFxmlContent("/fxml/app-list-pane.fxml", bundle, new AppManagementController())
                .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
                .build();
            dialog.showAndWait();
        } catch(IOException ex) {
            LOGGER.error("Failed to open dialog", ex);
        }
    }

    @FXML
    public void onJavaPlatformAction() {
        try {
            var dialog = DialogBuilder.newBuilder(InstalledJdkDTO.class)
                .withTitle(bundle.getString("java.dlg.title"))
                .withFxmlContent("/fxml/java-platforms.fxml", bundle, new JavaPlatformsController())
                .withButtonTypes(ButtonType.CLOSE)
                .build();
            dialog.showAndWait();
        } catch(Exception ex) {
            LOGGER.error("Failed to open dialog", ex);
        }
    }
}
