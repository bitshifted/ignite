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

import co.bitshifted.appforge.common.model.ApplicationInfo;
import co.bitshifted.appforge.common.model.BasicResource;
import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.ui.ApplicationInfoUIModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public class AppInfoController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoController.class);
    private static final String SPLASH_VBOX_ID = "#splashVbox";
    private ResourceBundle bundle;

    @FXML
    private CheckBox windowsSupportCheckBox;
    @FXML
    private CheckBox macSupportCheckBox;
    @FXML
    private CheckBox linuxSupportCheckBox;
    @FXML
    private TitledPane licensePane;
    @FXML
    private TextField execNameField;
    @FXML
    private TitledPane splashScreenPane;

    @FXML
    public void initialize() {
        LOGGER.debug("Init controller");
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.APPLICATION_INFO) {
            var selectedDeployment = newValue.deployment();
            var appInfoUiModel = selectedDeployment.getConfiguration().applicationInfoProperty().get();
            if(appInfoUiModel == null) {
                var appInfo = new ApplicationInfo();
                appInfo.setLicense(new BasicResource());
                appInfoUiModel = new ApplicationInfoUIModel(appInfo);
                RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().applicationInfoProperty().set(appInfoUiModel);
            }
            windowsSupportCheckBox.selectedProperty().bindBidirectional(appInfoUiModel.windowsSupportedProperty());
            macSupportCheckBox.selectedProperty().bindBidirectional(appInfoUiModel.macSupportedProperty());
            linuxSupportCheckBox.selectedProperty().bindBidirectional(appInfoUiModel.linuxSupportedProperty());
            execNameField.textProperty().bindBidirectional(appInfoUiModel.execNameProperty());
            var exeName = appInfoUiModel.execNameProperty().get();
            if(exeName == null || exeName.isBlank() || exeName.isEmpty()) {
                appInfoUiModel.execNameProperty().set(RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getName());
            }
            try{
                var licenseController = new BasicResourceController(appInfoUiModel.getLicenseUiModel());
                var content = UIRegistry.instance().createView("/fxml/basic-resource-view.fxml", bundle, licenseController);
                licensePane.setContent(content);
                // splash screen
                var splashController = new BasicResourceController(appInfoUiModel.getSplashScreenUiModel());
                var splashContent = UIRegistry.instance().createView("/fxml/basic-resource-view.fxml", bundle, splashController);
                var splashVbox = (VBox)splashScreenPane.getContent().lookup(SPLASH_VBOX_ID);
                splashVbox.getChildren().add(splashContent);
            } catch(IOException ex) {
                LOGGER.error("Failed to create view", ex);
            }

        }
    }
}
