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

import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.ListAvailableJdksTask;
import co.bitshifted.appforge.ignite.http.ListInstalledJdksTask;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.ui.JdkTreeCellFactory;
import co.bitshifted.appforge.ignite.ui.JdkTreeItem;
import co.bitshifted.appforge.ignite.util.Helpers;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;


public class JavaPlatformsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaPlatformsController.class);

    private enum Mode {INSTALLED, AVAILABLE};

    @FXML
    private ComboBox<Server> serversCombo;
    @FXML
    private ProgressIndicator serverCommProgress;
    @FXML
    private TreeView<JdkTreeItem> jdkTreeView;
    @FXML
    private Button removeJdkButton;
    @FXML
    private Button installNewJdkButton;
    @FXML
    private ProgressIndicator jdkProgressIndicator;
    @FXML
    private VBox buttonsContainer;
    @FXML
    private HBox availableJdkButtons;
    @FXML
    private HBox installedJdkButtons;
    @FXML
    private Button backToInstalledButton;
    @FXML
    private Button installSelectedJdkButton;

    private SimpleStringProperty currentServerUrl;
    private RuntimeData runtimeData;
    private SimpleObjectProperty<Mode> currentMode = new SimpleObjectProperty<>();

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        this.currentServerUrl = new SimpleStringProperty();
        this.runtimeData = RuntimeData.getInstance();
        backToInstalledButton.setGraphic(Helpers.getIcon(BootstrapIcons.SKIP_BACKWARD));
        buttonsContainer.getChildren().clear();
        currentMode.addListener((observableValue, oldMode, newMode) -> {
            buttonsContainer.getChildren().clear();
            switch (newMode) {
                case INSTALLED -> {
                    buttonsContainer.getChildren().add(installedJdkButtons);
                }
                case AVAILABLE -> {
                    buttonsContainer.getChildren().add(availableJdkButtons);
                }
            }
        });

        serversCombo.getItems().addAll(runtimeData.getServersList());
        serversCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            this.currentServerUrl.setValue(newValue.getBaseUrl());
            var task = new ListInstalledJdksTask(newValue.getBaseUrl());
            task.valueProperty().addListener(new TreeViewChangeListener("java.tree.root.installed"));
            task.stateProperty().addListener((observable, oldVal, newVal) -> {
                switch (newVal) {
                    case RUNNING -> {
                        serverCommProgress.setVisible(true);
//                        serverErrorLabel.setVisible(false);
                    }
                    case FAILED -> {
                        LOGGER.debug("task failed: {}", task.getException());
//                        serverErrorLabel.setVisible(true);
//                        serverErrorLabel.setText(task.processErrorMessage(":"));
//                        appLoadProgress.setVisible(false);
                    }
                    default -> {
                        LOGGER.debug("task state: {}", newVal);
                        serverCommProgress.setVisible(false);
                        currentMode.set(Mode.INSTALLED);
//                        serverErrorLabel.setVisible(false);
                    }
                }
            });
            TaskExecutor.getInstance().start(task);
        });
    }

    @FXML
    public void onRemoveJdk() {
        LOGGER.debug("Remove JDK button activated");
    }

    @FXML
    public void onInstallNewJdk() {
        LOGGER.debug("Install JDK button activated");
        jdkTreeView.getSelectionModel().clearSelection();
        var task = new ListAvailableJdksTask(currentServerUrl.getValue());
        task.valueProperty().addListener(new TreeViewChangeListener("java.tree.root.available"));
        task.stateProperty().addListener(((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                case RUNNING -> {
                    jdkProgressIndicator.setVisible(true);
                    installNewJdkButton.disableProperty().unbind();
                    installNewJdkButton.setDisable(true);
                }
                case SUCCEEDED -> {
                    jdkProgressIndicator.setVisible(false);
//                    buttonsContainer.getChildren().remove(installedJdkButtons);
//                    buttonsContainer.getChildren().add(availableJdkButtons);
                    currentMode.set(Mode.AVAILABLE);
                }
                case FAILED -> LOGGER.error("Task failed", task.getException());
            }
        }));
        TaskExecutor.getInstance().start(task);
    }

    @FXML
    public void onBackToInstalled() {
        jdkTreeView.getSelectionModel().clearSelection();
        var task = new ListInstalledJdksTask(serversCombo.getValue().getBaseUrl());
        task.valueProperty().addListener(new TreeViewChangeListener("java.tree.root.installed"));
        task.stateProperty().addListener((obsValue, oldVal, newVal) -> {
            switch (newVal) {
                case SUCCEEDED -> {
//                    buttonsContainer.getChildren().remove(availableJdkButtons);
//                    buttonsContainer.getChildren().add(installedJdkButtons);
                    currentMode.set(Mode.INSTALLED);
                    installNewJdkButton.disableProperty().bind(currentServerUrl.isEmpty());
                }
            }
        });
        TaskExecutor.getInstance().start(task);
    }

    @FXML
    public void onInstallSelectedJdk() {

    }

    private class TreeViewChangeListener implements ChangeListener<List<JdkTreeItem>> {

        private final String rootStringKey;

        TreeViewChangeListener(String rootStringKey) {
            this.rootStringKey = rootStringKey;
        }

        @Override
        public void changed(ObservableValue<? extends List<JdkTreeItem>> observableValue, List<JdkTreeItem> jdkTreeItem, List<JdkTreeItem> newValue) {
            jdkTreeView.getSelectionModel().selectedItemProperty().addListener(((obs, oldValue, newVal) -> {
                if(newVal != null && newVal.getValue().getType() == JdkTreeItemType.RELEASE) {
                    removeJdkButton.setDisable(false);
                    installNewJdkButton.setDisable(false);
                    installSelectedJdkButton.setDisable(false);
                } else {
                    removeJdkButton.setDisable(true);
                    installNewJdkButton.setDisable(true);
                    installSelectedJdkButton.setDisable(true);
                }
            }));
            var jdkTreeRoot = new TreeItem<>(JdkTreeItem.builder().type(JdkTreeItemType.ROOT).build());
            jdkTreeView.setRoot(jdkTreeRoot);
            jdkTreeView.setCellFactory(new JdkTreeCellFactory(ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME), rootStringKey));
            newValue.stream().forEach(vendor -> {
                var vendorTreeItem = new TreeItem<>(vendor);
                vendor.getChildren().stream().forEach(version -> {
                    var versionTreeItem = new TreeItem<>(version);
                    versionTreeItem.getChildren().addAll(version.getChildren().stream().map(TreeItem::new).toList());
                    vendorTreeItem.getChildren().add(versionTreeItem);
                });
                jdkTreeView.getRoot().getChildren().add(vendorTreeItem);
            });
            jdkTreeView.getRoot().setExpanded(true);

        }
    }
}
