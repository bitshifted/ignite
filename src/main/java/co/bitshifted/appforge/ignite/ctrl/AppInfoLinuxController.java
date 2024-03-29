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
import co.bitshifted.appforge.ignite.model.ui.LinuxAppInfoUIModel;
import co.bitshifted.appforge.ignite.model.ui.LinuxDesktopCategory;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckTreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppInfoLinuxController implements ChangeListener<DeploymentTreeItem>, ResourceNotificationReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoLinuxController.class);
    private ResourceBundle bundle;
    private FileChooser.ExtensionFilter[] iconFilters = new FileChooser.ExtensionFilter[]{
        new FileChooser.ExtensionFilter("PNG image (*.png)", "*.png")
    };

    @FXML
    private VBox iconsContainer;
    @FXML
    private CheckTreeView<LinuxDesktopCategory> categoryTreeView;
    @FXML
    private CheckBox archX86CheckBox;
    @FXML
    private CheckBox archArmCheckBox;
    @FXML
    private CheckBox debPackageCheckBox;
    @FXML
    private CheckBox rpmPackageCheckBox;
    @FXML
    private CheckBox tarGzPackageCheckBox;

    private LinuxAppInfoUIModel currentLinuxAppInfoModel;
    private final ListChangeListener<TreeItem<LinuxDesktopCategory>> categoryChangeListener;

    public AppInfoLinuxController() {
        this.categoryChangeListener = change -> {
            while (change.next()) {
                if(change.wasAdded()) {
                    currentLinuxAppInfoModel.getCategoriesUiModel().addAll(change.getAddedSubList().stream().map(ti -> ti.getValue()).collect(Collectors.toList()));
                }
                if(change.wasRemoved()) {
                    currentLinuxAppInfoModel.getCategoriesUiModel().removeAll(change.getRemoved().stream().map(ti -> ti.getValue()).collect(Collectors.toList()));
                }
            }
        };
    }

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        categoryTreeView.setRoot(new CheckBoxTreeItem<>(new LinuxDesktopCategory("Categories", "", List.of())));
        categoryTreeView.getRoot().setExpanded(true);
        var categories = new ArrayList<CheckBoxTreeItem<LinuxDesktopCategory>>();
        RuntimeData.getInstance().getLinuxDesktopCategories().stream().forEach(cat -> {
            var item = new CheckBoxTreeItem<>(cat);
            item.setIndependent(true);
            cat.additionalCategories().stream().forEach(c -> {
                item.getChildren().add(new CheckBoxTreeItem<>(c));
            });
            categories.add(item);
        });
        categoryTreeView.getRoot().getChildren().setAll(categories);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
        archX86CheckBox.selectedProperty().addListener((obsVal, oldVal, newVal) -> checkSelectedArchitecture(archX86CheckBox, newVal));
        archArmCheckBox.selectedProperty().addListener((obsVal, oldVal, newVal) -> checkSelectedArchitecture(archArmCheckBox, newVal));
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

            categoryTreeView.getCheckModel().clearChecks();
           var currentCategories = currentLinuxAppInfoModel.getCategoriesUiModel();
           var allitems = allTreeItems();
           categoryTreeView.getCheckModel().getCheckedItems().removeListener(categoryChangeListener);
           currentCategories.stream().forEach(cat -> {
               var match = allitems.stream().filter(ti -> ti.getValue().name().equals(cat.name())).findFirst();
               if(match.isPresent()) {
                   categoryTreeView.getCheckModel().check(match.get());
               }
           });
           categoryTreeView.getCheckModel().getCheckedItems().addListener(categoryChangeListener);
           archX86CheckBox.selectedProperty().bindBidirectional(currentLinuxAppInfoModel.getArchX86SupportedProperty());
           archArmCheckBox.selectedProperty().bindBidirectional(currentLinuxAppInfoModel.getArchArmSupportedProperty());
           debPackageCheckBox.selectedProperty().bindBidirectional(currentLinuxAppInfoModel.getDebPackageProperty());
           rpmPackageCheckBox.selectedProperty().bindBidirectional(currentLinuxAppInfoModel.getRpmPackageProperty());
           tarGzPackageCheckBox.selectedProperty().bindBidirectional(currentLinuxAppInfoModel.getTarGzPackagePropety());
           // setup change listeners
            archArmCheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
            archX86CheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
            debPackageCheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
            rpmPackageCheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
            tarGzPackageCheckBox.selectedProperty().addListener(new DirtyChangeListener<>());
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
        var iconController = new BasicResourceController(resourceUIModel, this, iconFilters);
        try {
            return UIRegistry.instance().createView("/fxml/basic-resource-view.fxml", bundle, iconController);
        } catch(Exception ex) {
            LOGGER.error("Failed to load view", ex);
            return new Label("Failed to load view: " + ex.getMessage());
        }
    }

    private List<TreeItem<LinuxDesktopCategory>> allTreeItems() {
        var out = new ArrayList<TreeItem<LinuxDesktopCategory>>();
        categoryTreeView.getRoot().getChildren().stream().forEach(ch -> {
            out.add(ch);
            if(ch.getChildren() != null) {
                out.addAll(ch.getChildren());
            }
        });
        return out;
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
