/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ui;

import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DeploymentTreeCellFactory implements Callback<TreeView<DeploymentTreeItem>, TreeCell<DeploymentTreeItem>> {

    @Override
    public TreeCell<DeploymentTreeItem> call(TreeView<DeploymentTreeItem> deploymentTreeItemTreeView) {
        return new TextFieldTreeCell<>(new Converter());
    }

    private class Converter extends StringConverter<DeploymentTreeItem> {

        @Override
        public String toString(DeploymentTreeItem deploymentTreeItem) {
            return switch (deploymentTreeItem.type()) {
                case ROOT ->  "Deployments";
                case DEPLOYMENT -> deploymentTreeItem.deployment().getName();
                case APPLICATION_INFO -> "Application Info";
                case JVM -> "JVM";
                case RESOURCES -> "Resources";
            };
        }

        @Override
        public DeploymentTreeItem fromString(String s) {
            return new DeploymentTreeItem(null, DeploymentItemType.ROOT);
        }
    }
}
