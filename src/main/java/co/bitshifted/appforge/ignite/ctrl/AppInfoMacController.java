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
import co.bitshifted.appforge.ignite.model.ui.DirtyChangeListener;
import co.bitshifted.appforge.ignite.model.ui.MacAppInfoUiModel;
import co.bitshifted.appforge.ignite.model.ui.WindowsAppInfoUIModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppInfoMacController implements ChangeListener<DeploymentTreeItem>, ResourceNotificationReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoWindowsController.class);
    private ResourceBundle bundle;
    private final FileChooser.ExtensionFilter[] iconFilters = new FileChooser.ExtensionFilter[]{
        new FileChooser.ExtensionFilter("Icon file (*.icns)", "*.icns")
    };

    private MacAppInfoUiModel currentMacAppInfoModel;

    @FXML
    private VBox iconsContainer;
    @FXML
    private CheckBox archX86CheckBox;
    @FXML
    private CheckBox archArmCheckBox;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
        archX86CheckBox.selectedProperty().addListener((obsVal, oldVal, newVal) -> checkSelectedArchitecture(archX86CheckBox, newVal));
        archArmCheckBox.selectedProperty().addListener((obsVal, oldVal, newVal) -> checkSelectedArchitecture(archArmCheckBox, newVal));
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        LOGGER.debug("deployment changed");
        if(newValue.type() == DeploymentItemType.APPLICATION_INFO_MAC) {
            var selectedDeployment = newValue.deployment();
            var appInfoUiModel = selectedDeployment.getConfiguration().applicationInfoProperty().get();
            this.currentMacAppInfoModel = appInfoUiModel.getMacAppInfoUiModel();
            var iconsList = currentMacAppInfoModel.getIconsUiModel().stream().map(ui -> createIconResourceView(ui)).collect(Collectors.toList());
            iconsContainer.getChildren().subList(1, iconsContainer.getChildren().size()).clear();
            iconsContainer.getChildren().addAll(iconsList);
            archX86CheckBox.selectedProperty().bindBidirectional(currentMacAppInfoModel.getArchX86SupportedProperty());
            archArmCheckBox.selectedProperty().bindBidirectional(currentMacAppInfoModel.getArchArmSupportedProperty());
            // setup change listeners
            archArmCheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
            archX86CheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
        }
    }

    @Override
    public Pane getResourceViewParent() {
        return iconsContainer;
    }

    @Override
    public void resourceRemoved(BasicResourceUIModel resource) {
        currentMacAppInfoModel.getIconsUiModel().remove(resource);
    }

    @FXML
    public void onAddIcon() {
        var resource = new BasicResourceUIModel(new BasicResource());
        iconsContainer.getChildren().add(createIconResourceView(resource));
        currentMacAppInfoModel.getIconsUiModel().add(resource);
    }

    private Parent createIconResourceView(BasicResourceUIModel resourceUIModel)  {
        var iconController = new BasicResourceController(resourceUIModel, this, iconFilters);
        try {
            return UIRegistry.instance().createView("/fxml/basic-resource-view.fxml", bundle, iconController);
        } catch(Exception ex) {
            LOGGER.error("Failed to load view", ex);
            return new Label("Failed to load view: " + ex.getMessage());
        }
    }

    private void checkSelectedArchitecture(CheckBox source, boolean value) {
        if(!archX86CheckBox.isSelected() && !archArmCheckBox.isSelected() && !value) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.OK);
            alert.setContentText("At least one CPU architecture must be selected!");
            alert.showAndWait();
            source.setSelected(true);
        }
    }
}
