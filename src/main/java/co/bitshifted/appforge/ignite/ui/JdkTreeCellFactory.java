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

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.devicons.Devicons;

public class JdkTreeCellFactory implements Callback<TreeView<JdkTreeItem>, TreeCell<JdkTreeItem>> {
    @Override
    public TreeCell<JdkTreeItem> call(TreeView<JdkTreeItem> jdkTreeItemTreeView) {
        return new JdkTreeCell();
    }

  public static class JdkTreeCell extends TreeCell<JdkTreeItem> {
      @Override
      protected void updateItem(JdkTreeItem item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
              setText(null);
              setGraphic(null);
          } else {
              switch (item.type()) {
                  case ROOT -> setText("Installed JDKs");
                  case VENDOR -> setGraphic(getVendorCellNode(item));
                  case MAJOR_VERSION -> setGraphic(getVersionCellNode(item));
                  case RELEASE -> setGraphic(getReleaseCellNode(item));
              }
          }

      }

      private Node getVendorCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(UiUtils.getIcon(BootstrapIcons.BUILDING));
          hbox.getChildren().add(new Label(item.vendor().getDisplay()));
          return hbox;
      }

      private Node getVersionCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(UiUtils.getIcon(Devicons.JAVA));
          hbox.getChildren().add(new Label(item.majorVersion().getDisplay()));
          hbox.getChildren().add(new CheckBox("Auto update"));
          return hbox;
      }

      private Node getReleaseCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(UiUtils.getIcon(BootstrapIcons.FILE_DIFF));
          hbox.getChildren().add(new Label(item.release()));
          if(item.latest()) {
              var tagLabel = new Label("latest");
              tagLabel.getStyleClass().add("jdk-tag-latest");
              hbox.getChildren().add(tagLabel);
          }
          return hbox;
      }
  }
}
