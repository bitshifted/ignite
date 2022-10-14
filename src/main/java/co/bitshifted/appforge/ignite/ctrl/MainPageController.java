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
import co.bitshifted.appforge.ignite.ui.DeploymentTreeCellFactory;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kordamp.ikonli.devicons.Devicons;

public class MainPageController implements ListChangeListener<Deployment> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
    private static final int treeIconsSIze = 17;

    @FXML
    private TreeView<DeploymentTreeItem> deploymentTree;
    @FXML
    private AnchorPane detailsPane;
    @FXML
    private BorderPane detailsBorderPane;

    @FXML
    public void initialize() {
        createDeploymentTree();
        detailsBorderPane.setBottom(UIRegistry.instance().getComponent(UIRegistry.PROJECT_BUTTONS_BAR));
        RuntimeData.getInstance().getDeploymentsList().addListener(this);
    }

    private void createDeploymentTree() {
        deploymentTree.setCellFactory(new DeploymentTreeCellFactory());
        var root = new TreeItem<>(new DeploymentTreeItem(null, DeploymentItemType.ROOT), getIcon(BootstrapIcons.KANBAN));
        root.expandedProperty().set(true);
        deploymentTree.setRoot(root);
        Platform.runLater(() -> {
            try {
                var deployments = RuntimeData.getInstance().getDeploymentsList();
                deployments.stream().forEach(d ->
                    deploymentTree.getRoot().getChildren().add(createDeploymentNode(d)));
            } catch(Exception ex) {
                LOGGER.error("Failed to load deployment list", ex);
            }

        });
        deploymentTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            RuntimeData.getInstance().selectedDeploymentTreeITemProperty().set(newValue.getValue());
            LOGGER.debug("Deployment selection changed");
            switch (newValue.getValue().type()) {
                case DEPLOYMENT -> setupDetailsPane(UIRegistry.DEPLOYMENT_INFO);
                case APPLICATION_INFO -> setupDetailsPane(UIRegistry.APPLICATION_INFO_UI);
            }
        });
    }

    private FontIcon getIcon(Ikon font) {
        var icon = new FontIcon(font);
        icon.setIconSize(treeIconsSIze);
        return icon;
    }

    private TreeItem<DeploymentTreeItem> createDeploymentNode(Deployment deployment) {
        var node = new TreeItem(new DeploymentTreeItem(deployment, DeploymentItemType.DEPLOYMENT), getIcon(BootstrapIcons.BRIEFCASE));
        node.getChildren().add(new TreeItem(new DeploymentTreeItem(deployment, DeploymentItemType.APPLICATION_INFO), getIcon(BootstrapIcons.INFO_SQUARE)));
        node.getChildren().add(new TreeItem(new DeploymentTreeItem(deployment, DeploymentItemType.JVM), getIcon(Devicons.JAVA)));
        node.getChildren().add(new TreeItem(new DeploymentTreeItem(deployment, DeploymentItemType.RESOURCES), getIcon(BootstrapIcons.ARCHIVE)));
        return node;
    }

    private void setupDetailsPane(String name) {
        detailsPane.getChildren().clear();
        detailsPane.getChildren().add(UIRegistry.instance().getComponent(name));
    }

    @Override
    public void onChanged(Change<? extends Deployment> change) {
        LOGGER.debug("Deployment list changed");
        while(change.next() && change.wasAdded()) {
            change.getAddedSubList().forEach(d -> {
                deploymentTree.getRoot().getChildren().add(createDeploymentNode(d));
            });

        }
    }
}
