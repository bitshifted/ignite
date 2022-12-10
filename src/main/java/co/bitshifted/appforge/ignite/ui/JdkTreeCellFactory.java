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

import co.bitshifted.appforge.ignite.ctrl.JavaPlatformsController;
import co.bitshifted.appforge.ignite.util.Helpers;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.devicons.Devicons;

import java.util.ResourceBundle;

public class JdkTreeCellFactory implements Callback<TreeView<JdkTreeItem>, TreeCell<JdkTreeItem>> {

    private final ResourceBundle bundle;
    private final String rootStringKey;
    private final SimpleObjectProperty<JavaPlatformsController.Mode> mode;

    public JdkTreeCellFactory(ResourceBundle bundle, String rootStringKey, SimpleObjectProperty<JavaPlatformsController.Mode> mode) {
        this.bundle = bundle;
        this.rootStringKey = rootStringKey;
        this.mode = mode;
    }

    @Override
    public TreeCell<JdkTreeItem> call(TreeView<JdkTreeItem> jdkTreeItemTreeView) {
        return new JdkTreeCell();
    }

  public  class JdkTreeCell extends TreeCell<JdkTreeItem> {
      @Override
      protected void updateItem(JdkTreeItem item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
              setText(null);
              setGraphic(null);
          } else {
              switch (item.getType()) {
                  case ROOT -> setText(bundle.getString(rootStringKey));
                  case VENDOR -> setGraphic(getVendorCellNode(item));
                  case MAJOR_VERSION -> setGraphic(getVersionCellNode(item));
                  case RELEASE -> setGraphic(getReleaseCellNode(item));
              }
          }

      }

      private Node getVendorCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(Helpers.getIcon(BootstrapIcons.BUILDING));
          hbox.getChildren().add(new Label(item.getVendor().getDisplay()));
          return hbox;
      }

      private Node getVersionCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(Helpers.getIcon(Devicons.JAVA));
          var sb = new StringBuilder(item.getMajorVersion().getDisplay());
          if(mode.get() == JavaPlatformsController.Mode.INSTALLED) {
              if(item.isAutoUpdate()) {
                  sb.append(" (Auto  update enabled)");
              } else {
                  sb.append(" (Auto  update disabled)");
              }
          }

          hbox.getChildren().add(new Label(sb.toString()));
          return hbox;
      }

      private Node getReleaseCellNode(JdkTreeItem item) {
          var hbox = new HBox(5.0);
          hbox.getChildren().add(Helpers.getIcon(BootstrapIcons.FILE_DIFF));
          hbox.getChildren().add(new Label(item.getRelease()));
          if(item.isLatest()) {
              var tagLabel = new Label("latest");
              tagLabel.getStyleClass().add("jdk-tag-latest");
              hbox.getChildren().add(tagLabel);
          }
          return hbox;
      }
  }
}
