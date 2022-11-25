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

import co.bitshifted.appforge.common.dto.jdk.JdkInstallRequestDTO;
import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.InstallNewJdkTask;
import co.bitshifted.appforge.ignite.http.ListAvailableJdksTask;
import co.bitshifted.appforge.ignite.http.ListInstalledJdksTask;
import co.bitshifted.appforge.ignite.http.RemoveInstalledJdkTask;
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
    private ProgressIndicator jdkInstallationprogress;
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
    @FXML
    private Label serverErrorLabel;

    private Label jdkErrorLabel;
    private Label jdkInstallStatusLabel;

    private SimpleStringProperty currentServerUrl;
    private RuntimeData runtimeData;
    private SimpleObjectProperty<Mode> currentMode = new SimpleObjectProperty<>();
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        this.currentServerUrl = new SimpleStringProperty();
        this.runtimeData = RuntimeData.getInstance();
        this.bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);
        this.jdkErrorLabel = new Label();
        this.jdkErrorLabel.getStyleClass().add("error-label");
        this.jdkInstallStatusLabel = new Label();
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
                        serverErrorLabel.setVisible(false);
                    }
                    case SUCCEEDED -> {
                        currentMode.set(Mode.INSTALLED);
                        serverErrorLabel.setVisible(false);
                        serverCommProgress.setVisible(false);
                    }
                    case FAILED -> {
                        LOGGER.debug("task failed: {}", task.getException());
                        serverErrorLabel.setVisible(true);
                        serverErrorLabel.setText(task.processErrorMessage(":"));
                        serverCommProgress.setVisible(false);
                    }
                    default -> {
                        LOGGER.debug("task state: {}", newVal);
                        serverCommProgress.setVisible(false);
                    }
                }
            });
            TaskExecutor.getInstance().start(task);
        });
    }

    @FXML
    public void onRemoveJdk() {
        LOGGER.debug("Remove JDK button activated");
        var selected = jdkTreeView.getSelectionModel().getSelectedItem();
        var releaseId = "";
        if(selected.getValue().getType() == JdkTreeItemType.RELEASE) {
            releaseId = selected.getValue().getReleaseId();
        }
        LOGGER.debug("release ID: {}", releaseId);
        var parent = selected.getParent();
        var jdkId = "";
        if(parent.getValue().getType() == JdkTreeItemType.MAJOR_VERSION) {
            jdkId = parent.getValue().getJdkId();
        }
        LOGGER.debug("JDK ID: {}", jdkId);
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(bundle.getString("java.dlg.alert.message"));
        var result = alert.showAndWait();
        if(result.get() == ButtonType.CANCEL) {
            return;
        }
        var task = new RemoveInstalledJdkTask(serversCombo.getSelectionModel().getSelectedItem().getBaseUrl(), jdkId, releaseId);
        task.stateProperty().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case RUNNING -> {
                    jdkProgressIndicator.setVisible(true);
                    buttonsContainer.getChildren().remove(jdkErrorLabel);
//                    jdkErrorLabel.setVisible(false);
                }
                case SUCCEEDED -> {
                    parent.getChildren().remove(selected);
                    jdkProgressIndicator.setVisible(false);
                    buttonsContainer.getChildren().remove(jdkErrorLabel);
//                    jdkErrorLabel.setVisible(false);
                }
                case FAILED -> {
                    LOGGER.error("Failed to remove jdk: {}", task.processErrorMessage(":"));
                    jdkProgressIndicator.setVisible(false);
                    buttonsContainer.getChildren().add(jdkErrorLabel);
//                    jdkErrorLabel.setVisible(true);
                }
            }
        });
        TaskExecutor.getInstance().start(task);
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
                    currentMode.set(Mode.INSTALLED);
                    installNewJdkButton.disableProperty().bind(currentServerUrl.isEmpty());
                }
            }
        });
        TaskExecutor.getInstance().start(task);
    }

    @FXML
    public void onInstallSelectedJdk() {
        LOGGER.debug("Install JDk activated");
        var selected = jdkTreeView.getSelectionModel().getSelectedItem();
        var release = "";
        var autoUpdateEnabled = false;
        if (selected.getValue().getType() == JdkTreeItemType.RELEASE ) {
            var confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.getButtonTypes().clear();
            confirm.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            if(selected.getValue().isLatest()) {
                confirm.setContentText(bundle.getString("java.dlg.alert.auto_update"));
                var decision = confirm.showAndWait();
                if(decision.get() == ButtonType.YES) {
                    autoUpdateEnabled = true;
                }
            } else {
                confirm.setContentText(String.format(bundle.getString("java.dlg.alert.release.newer"), selected.getValue().getRelease()));
                var decision = confirm.showAndWait();
                if(decision.get() == ButtonType.NO) {
                    LOGGER.debug("Decision: do not process with install");
                    return;
                }
            }
            release = selected.getValue().getRelease();
        }
        var majorVersionItem = selected.getParent();
        var vendorItem = majorVersionItem.getParent();
        var installRequest = new JdkInstallRequestDTO();
        installRequest.setVendor(vendorItem.getValue().getVendor());
        installRequest.setMajorVersion(majorVersionItem.getValue().getMajorVersion());
        installRequest.setRelease(release);
        installRequest.setAutoUpdate(autoUpdateEnabled);

        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        var sb = new StringBuilder("Please confirm the installation configuration:\n");
        sb.append("Vendor: ").append(installRequest.getVendor().getDisplay()).append("\n");
        sb.append("Version: ").append(installRequest.getMajorVersion().getDisplay()).append("\n");
        sb.append("Release: ").append(installRequest.getRelease()).append("\n");
        sb.append("Auto-update: ").append(installRequest.isAutoUpdate() ? "Yes" : "No" );
        alert.setContentText(sb.toString());
        var decision = alert.showAndWait();
        if(decision.get() == ButtonType.YES) {
            var task = new InstallNewJdkTask(serversCombo.getSelectionModel().getSelectedItem().getBaseUrl(), installRequest);
            task.valueProperty().addListener((obs, oldVal, newVal) -> {
                jdkInstallStatusLabel.setText(newVal.getStatus().toString());
            });
            task.stateProperty().addListener((obs, oldVal, newVal) -> {
                switch (newVal) {
                    case RUNNING -> {
                        jdkInstallationprogress.setVisible(true);
                        buttonsContainer.getChildren().add(jdkInstallStatusLabel);
                    }
                    case SUCCEEDED -> {
                        jdkInstallationprogress.setVisible(false);
                        buttonsContainer.getChildren().remove(jdkInstallStatusLabel);
                    }
                    case FAILED -> {
                        jdkInstallationprogress.setVisible(false);
                        LOGGER.error(task.processErrorMessage(":"));
                    }
                }
            });
            TaskExecutor.getInstance().start(task);
        }
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
