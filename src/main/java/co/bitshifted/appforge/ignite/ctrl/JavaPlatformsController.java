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

import co.bitshifted.appforge.ignite.TaskExecutor;
import co.bitshifted.appforge.ignite.http.ListInstalledJdksTask;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.ui.JdkTreeCellFactory;
import co.bitshifted.appforge.ignite.ui.JdkTreeItem;
import co.bitshifted.appforge.ignite.ui.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.devicons.Devicons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JavaPlatformsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaPlatformsController.class);

    @FXML
    private ComboBox<Server> serversCombo;
    @FXML
    private ProgressIndicator serverCommProgress;
    @FXML
    private TreeView<JdkTreeItem> jdkTreeView;
    @FXML
    private Button removeJdkButton;

    private RuntimeData runtimeData;

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        this.runtimeData = RuntimeData.getInstance();
        var jdkTreeRoot = new TreeItem<>(new JdkTreeItem(null,null, false,"", false, JdkTreeItemType.ROOT));
        jdkTreeView.setRoot(jdkTreeRoot);
        jdkTreeView.setCellFactory(new JdkTreeCellFactory());
        jdkTreeView.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            LOGGER.debug("Tree item selected");
            if(newValue.getValue().type() == JdkTreeItemType.RELEASE) {
                removeJdkButton.setDisable(false);
            }
        }));
        serversCombo.getItems().addAll(runtimeData.getServersList());
        serversCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            var task = new ListInstalledJdksTask(newValue.getBaseUrl());
            task.valueProperty().addListener((observable, oldVal, newVal) -> {
                var vendorTreeItems = newVal.stream().filter(distinctByKey(jdk -> jdk.getVendor())).map(jdk -> new JdkTreeItem(jdk.getVendor(), null, false,"", false, JdkTreeItemType.VENDOR)).collect(Collectors.toList());
                vendorTreeItems.stream().forEach(vendorTreeItem -> {
                    var curVendorItem = new TreeItem<>(vendorTreeItem);
                    jdkTreeView.getRoot().getChildren().add(curVendorItem);
                    var majorVersions = newVal.stream().filter(jdk -> jdk.getVendor() == vendorTreeItem.vendor()).map(jdk -> new JdkTreeItem(jdk.getVendor(), jdk.getMajorVersion(), jdk.getAutoUpdate(), "", false, JdkTreeItemType.MAJOR_VERSION)).collect(Collectors.toList());
                    majorVersions.stream().forEach(majorVersionItem -> {
                        var curVersionItem = new TreeItem<>(majorVersionItem);
                        curVendorItem.getChildren().add(curVersionItem);
                        var jdkReleases = newVal.stream().filter(jdk -> jdk.getVendor() == vendorTreeItem.vendor() && jdk.getMajorVersion() == majorVersionItem.majorVersion()).findFirst().get().getReleases();
                        var releaseItems = jdkReleases.stream().map(rl -> new JdkTreeItem(null, null, false, rl.getRelease(), rl.getLatest(), JdkTreeItemType.RELEASE)).sorted(UiUtils.jdkVersionComparator()).collect(Collectors.toList());
                        releaseItems.stream().forEach(releaseItem -> {
                            var curReleaseItem = new TreeItem<>(releaseItem);
                            curVersionItem.getChildren().add(curReleaseItem);
                        });
                    });
                });
            });
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

    public static <T> Predicate<T> distinctByKey(
        Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
