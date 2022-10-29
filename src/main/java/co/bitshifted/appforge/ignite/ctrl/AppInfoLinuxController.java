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

import co.bitshifted.appforge.common.model.BasicResource;
import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.ui.BasicResourceUIModel;
import co.bitshifted.appforge.ignite.model.ui.LinuxAppInfoUIModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppInfoLinuxController implements ChangeListener<DeploymentTreeItem>, ResourceNotificationReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoLinuxController.class);
    private ResourceBundle bundle;

    @FXML
    private VBox iconsContainer;

    private LinuxAppInfoUIModel currentLinuxAppInfoModel;

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.APPLICATION_INFO_LINUX) {
            var selectedDeployment = newValue.deployment();
            var appInfoUiModel = selectedDeployment.getConfiguration().applicationInfoProperty().get();
            this.currentLinuxAppInfoModel = appInfoUiModel.getLinuxAppInfoUIModel();
            var iconsList = currentLinuxAppInfoModel.getIconsUiModel().stream().map(ui -> createIconResourceView(ui)).collect(Collectors.toList());
            iconsContainer.getChildren().subList(1, iconsContainer.getChildren().size()).clear();
            iconsContainer.getChildren().addAll(iconsList);
        }
    }

    @FXML
    public void onAddIcon() {
            var resource = new BasicResourceUIModel(new BasicResource());
            iconsContainer.getChildren().add(createIconResourceView(resource));
            currentLinuxAppInfoModel.getIconsUiModel().add(resource);
    }

    @Override
    public Pane getResourceViewParent() {
        return iconsContainer;
    }

    @Override
    public void resourceRemoved(BasicResourceUIModel resource) {
        currentLinuxAppInfoModel.getIconsUiModel().remove(resource);
    }

    private Parent createIconResourceView(BasicResourceUIModel resourceUIModel)  {
        var iconController = new BasicResourceController(resourceUIModel, this);
        try {
            return UIRegistry.instance().createView("/fxml/basic-resource-view.fxml", bundle, iconController);
        } catch(Exception ex) {
            LOGGER.error("Failed to load view", ex);
            return new Label("Failed to load view: " + ex.getMessage());
        }
    }
}
