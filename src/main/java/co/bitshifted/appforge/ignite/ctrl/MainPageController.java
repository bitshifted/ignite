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

import co.bitshifted.appforge.ignite.model.Deployment;
import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.persist.DeploymentDataPersister;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeCellFactory;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class MainPageController implements ListChangeListener<Deployment> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
    private static final int treeIconsSIze = 17;

    @FXML
    private TreeView<DeploymentTreeItem> deploymentTree;

    @FXML
    public void initialize() {
        createDeploymentTree();
        RuntimeData.getInstance().getDeploymentsList().addListener(this);
    }

    private void createDeploymentTree() {
        deploymentTree.setCellFactory(new DeploymentTreeCellFactory());
        var root = new TreeItem<>(new DeploymentTreeItem(null, DeploymentItemType.ROOT), getIcon(BootstrapIcons.KANBAN));
        root.expandedProperty().set(true);
        deploymentTree.setRoot(root);
        Platform.runLater(() -> {
            try {
                var deployments = DeploymentDataPersister.instance().load();
                Stream.of(deployments).forEach(d -> RuntimeData.getInstance().addDeployment(d));
            } catch(Exception ex) {
                LOGGER.error("Failed to laod deployment list", ex);
            }

        });
    }

    private FontIcon getIcon(Ikon font) {
        var icon = new FontIcon(font);
        icon.setIconSize(treeIconsSIze);
        return icon;
    }

    private TreeItem<DeploymentTreeItem> createDeploymentNode(Deployment deployment) {
        var node = new TreeItem(new DeploymentTreeItem(deployment, DeploymentItemType.DEPLYOMENT), getIcon(BootstrapIcons.BRIEFCASE));
        return node;
    }

    @Override
    public void onChanged(Change<? extends Deployment> change) {
        LOGGER.debug("Deployment list changed");
        while(change.next() && change.wasAdded()) {
            change.getAddedSubList().forEach(d -> {
                deploymentTree.getRoot().getChildren().add(createDeploymentNode(d));
            });
            try {
                DeploymentDataPersister.instance().save(RuntimeData.getInstance().getDeploymentsList());
            } catch(IOException ex) {
                LOGGER.error("Failed to save deployment list", ex);
            }
        }
    }
}
