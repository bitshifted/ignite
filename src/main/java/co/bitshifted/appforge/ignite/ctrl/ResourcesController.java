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
import co.bitshifted.appforge.ignite.model.ui.ResourcesUiModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ResourcesController implements ChangeListener<DeploymentTreeItem>,  ResourceNotificationReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);

    private ResourcesUiModel currentResourcesUiModel;
    private ResourceBundle bundle;
    @FXML
    private VBox resourceContainer;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }


    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.RESOURCES) {
            var selectedDeployment = newValue.deployment();
            currentResourcesUiModel = selectedDeployment.getConfiguration().resourcesProperty().get();
            if(currentResourcesUiModel == null) {
                currentResourcesUiModel = new ResourcesUiModel(new ArrayList<>());
                selectedDeployment.getConfiguration().resourcesProperty().set(currentResourcesUiModel);
            }
            var resourceList = currentResourcesUiModel.getResourceList().stream().map(ui -> createIconResourceView(ui)).collect(Collectors.toList());
            resourceContainer.getChildren().subList(1, resourceContainer.getChildren().size()).clear();
            resourceContainer.getChildren().addAll(resourceList);
        }
    }

    @FXML
    public void onAddItem() {
        var resource = new BasicResourceUIModel(new BasicResource());
        resourceContainer.getChildren().add(createIconResourceView(resource));
        currentResourcesUiModel.getResourceList().add(resource);
    }

    @Override
    public Pane getResourceViewParent() {
        return resourceContainer;
    }

    @Override
    public void resourceRemoved(BasicResourceUIModel resource) {
        currentResourcesUiModel.getResourceList().remove(resource);
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
