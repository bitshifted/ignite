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

import co.bitshifted.appforge.common.dto.JvmConfigurationDTO;
import co.bitshifted.appforge.common.model.JavaVersion;
import co.bitshifted.appforge.common.model.JvmVendor;
import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.ListInstalledJdksTask;
import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.ui.DirtyChangeListener;
import co.bitshifted.appforge.ignite.model.ui.JvmConfigUiModel;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.JdkTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class JvmInfoController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JvmInfoController.class);

    @FXML
    private ComboBox<JvmVendor> vendorComboBox;
    @FXML
    private ComboBox<JavaVersion> versionComboBox;
    @FXML
    private ComboBox<String> jvmReleaseCombo;
    @FXML
    private TextField mainClassTextField;
    @FXML
    private TextField moduleNameTextField;
    @FXML
    private TextField argumentsTextField;
    @FXML
    private TextArea jvmOptionsField;
    @FXML
    private TextArea systemPropertiesField;
    @FXML
    private ProgressIndicator jvmLoadingProgress;
    @FXML
    private Label jvmLoadErrorLabel;

    private ObservableList<JdkTreeItem> jdkTreeItems;

    @FXML
    public void initialize() {
        vendorComboBox.setCellFactory(lv -> createVendorListCell());
        vendorComboBox.setButtonCell(createVendorListCell());
        versionComboBox.setCellFactory(lv -> createJavaVersionListCell());
        versionComboBox.setButtonCell(createJavaVersionListCell());
        jdkTreeItems = FXCollections.observableArrayList();
        jdkTreeItems.addListener((ListChangeListener<JdkTreeItem>) change -> {
            var vendors = jdkTreeItems.stream().filter(ti -> ti.getType() == JdkTreeItemType.VENDOR).map(ti -> ti.getVendor()).collect(Collectors.toList());
            vendorComboBox.getItems().clear();
            vendorComboBox.getItems().addAll(vendors);
        });
        vendorComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, jvmVendor, newValue) -> {
            LOGGER.debug("Vendor selection changed");
            var vendorItem = jdkTreeItems.stream()
                .filter(jti -> jti.getType() == JdkTreeItemType.VENDOR && jti.getVendor() == newValue)
                .findFirst();
            if(vendorItem.isEmpty()) {
                return;
            }
            var versions = vendorItem.get().getChildren().stream()
                .map(ch -> ch.getMajorVersion()).collect(Collectors.toList());
            LOGGER.debug("versions: {}", versions);
            versionComboBox.getItems().clear();
            versionComboBox.getItems().addAll(versions);
            if(!versions.isEmpty()) {
                versionComboBox.getSelectionModel().select(0);
            }
        });
        versionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            var vendor = vendorComboBox.getSelectionModel().getSelectedItem();
            var vendorItem = jdkTreeItems.stream().filter(jti -> jti.getType() == JdkTreeItemType.VENDOR && jti.getVendor() == vendor).findFirst();
            if(vendorItem.isEmpty()) {
                return;
            }
            var versionItem = vendorItem.get().getChildren().stream().filter(vi -> vi.getMajorVersion() == newVal).findFirst();
            if(versionItem.isEmpty()) {
                return;
            }
            var releases = versionItem.get().getChildren().stream().map(JdkTreeItem::getRelease).collect(Collectors.toList());
            var hasLatest = versionItem.get().getChildren().stream().anyMatch(ch -> ch.isLatest());
            jvmReleaseCombo.getItems().clear();
            if(hasLatest) {
                jvmReleaseCombo.getItems().add("latest");
            }
            jvmReleaseCombo.getItems().addAll(releases);
            if(!releases.isEmpty()) {
                jvmReleaseCombo.getSelectionModel().select(0);
            }
        });

        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.JVM) {
            LOGGER.debug("JVM option selected");
            var selectedDeployment = newValue.deployment();
            var task = new ListInstalledJdksTask(selectedDeployment.getConfiguration().getServerUrl());
            task.stateProperty().addListener((obs, oldVal, newVal) -> {
                switch (newVal) {
                    case RUNNING -> {
                        jvmLoadingProgress.setVisible(true);
                    }
                    case SUCCEEDED -> {
                        jvmLoadingProgress.setVisible(false);
                        jvmLoadErrorLabel.setVisible(false);
                    }
                    case FAILED -> {
                        jvmLoadingProgress.setVisible(false);
                        jvmLoadErrorLabel.setVisible(true);
                        jvmLoadErrorLabel.setText(task.processErrorMessage(":"));
                    }
                }
            });
            task.valueProperty().addListener((obs, old, newVal) -> {
                jdkTreeItems.clear();
                jdkTreeItems.addAll(newVal);
            });
            TaskExecutor.getInstance().start(task);
            var jvmConfigUiModel = selectedDeployment.getConfiguration().jvmConfigurationPropertyProperty().get();
            if(jvmConfigUiModel == null) {
                jvmConfigUiModel = new JvmConfigUiModel(new JvmConfigurationDTO());
                RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().jvmConfigurationPropertyProperty().set(jvmConfigUiModel);
            }
            vendorComboBox.valueProperty().bindBidirectional(jvmConfigUiModel.jvmVendorProperty());
            versionComboBox.valueProperty().bindBidirectional(jvmConfigUiModel.javaVersionProperty());
            jvmReleaseCombo.valueProperty().bindBidirectional(jvmConfigUiModel.releaseProperty());
            mainClassTextField.textProperty().bindBidirectional(jvmConfigUiModel.mainClassProperty());
            moduleNameTextField.textProperty().bindBidirectional(jvmConfigUiModel.moduleNameProperty());
            argumentsTextField.textProperty().bindBidirectional(jvmConfigUiModel.argumentsProperty());
            jvmOptionsField.textProperty().bindBidirectional(jvmConfigUiModel.jvmOptionsProperty());
            systemPropertiesField.textProperty().bindBidirectional(jvmConfigUiModel.systemPropertiesProperty());
            // setup change listeners
            vendorComboBox.getSelectionModel().selectedItemProperty().addListener(new DirtyChangeListener<>());
            versionComboBox.getSelectionModel().selectedItemProperty().addListener(new DirtyChangeListener<>());
            mainClassTextField.textProperty().addListener(new DirtyChangeListener<>());
            moduleNameTextField.textProperty().addListener(new DirtyChangeListener<>());
            argumentsTextField.textProperty().addListener(new DirtyChangeListener<>());
            jvmOptionsField.textProperty().addListener(new DirtyChangeListener<>());
            systemPropertiesField.textProperty().addListener(new DirtyChangeListener<>());
        }
    }

    private ListCell<JvmVendor> createVendorListCell() {
        return new ListCell<>(){
            @Override
            protected void updateItem(JvmVendor jvmVendor, boolean empty) {
                super.updateItem(jvmVendor, empty);
                if(jvmVendor == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(jvmVendor.getDisplay());
                }
            }
        };
    }

    private ListCell<JavaVersion> createJavaVersionListCell() {
        return new ListCell<>(){
            @Override
            protected void updateItem(JavaVersion version, boolean empty) {
                super.updateItem(version, empty);
                if(version == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(version.getDisplay());
                }
            }
        };
    }
}
