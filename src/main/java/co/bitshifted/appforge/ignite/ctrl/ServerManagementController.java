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

import co.bitshifted.appforge.ignite.IgniteConstants;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.model.Server;
import co.bitshifted.appforge.ignite.persist.UserDataPersister;
import co.bitshifted.appforge.ignite.ui.DialogBuilder;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public class ServerManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManagementController.class);
    private ResourceBundle bundle;

    @FXML
    private ListView<Server> serverListView;
    @FXML
    private Button editServerButton;
    @FXML
    private Button deleteServerButton;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle(IgniteConstants.MESSAGE_BUNDLE_NAME);
        LOGGER.debug("server list: {}", RuntimeData.getInstance().getServersList().get());
        serverListView.getItems().addAll(RuntimeData.getInstance().getServersList().get());
        serverListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                editServerButton.setDisable(false);
                deleteServerButton.setDisable(false);
            }
        });
        serverListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        RuntimeData.getInstance().getServersList().bindBidirectional(serverListView.itemsProperty());
    }

    @FXML
    public void onNewServerAction() {
        var controller = ControllerRegistry.instance().getController(AddServerController.class);
        var dialog = DialogBuilder.newBuilder(Server.class)
            .withTitle(bundle.getString("newserver.dialog.title"))
            .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .withContent(UIRegistry.instance().getComponent(UIRegistry.ADD_SERVER_PANE))
            .withResultConverter(controller.getResultConverter())
            .build();
         controller.clear();
         dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
             if(!controller.validateInput()) {
                 event.consume();
             }
         });

        var result = dialog.showAndWait();
        if(result.isPresent()) {
            RuntimeData.getInstance().getServersList().add(result.get());
           persistData();
        }
    }

    @FXML
    public void onEditServerAction() {
        var selected = serverListView.getSelectionModel().getSelectedItem();
        var controller = ControllerRegistry.instance().getController(AddServerController.class);
        var dialog = DialogBuilder.newBuilder(Server.class)
            .withTitle(bundle.getString("edit.server.dialog.title"))
            .withButtonTypes(ButtonType.OK, ButtonType.CANCEL)
            .withContent(UIRegistry.instance().getComponent(UIRegistry.ADD_SERVER_PANE))
            .withResultConverter(controller.getResultConverter())
            .build();
        controller.setObject(selected);
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            if(!controller.validateInput()) {
                event.consume();
            }
        });

        var result = dialog.showAndWait();
        if(result.isPresent()) {
            var selectedIndex = serverListView.getSelectionModel().getSelectedIndex();
            serverListView.getItems().set(selectedIndex, result.get());
            deleteServerButton.setDisable(true);
            editServerButton.setDisable(true);
            persistData();
        }
    }

    @FXML
    public void onDeleteServerAction() {
        var selIndex = serverListView.getSelectionModel().getSelectedIndex();
        serverListView.getItems().remove(selIndex);
        serverListView.getSelectionModel().select(null);
        deleteServerButton.setDisable(true);
        editServerButton.setDisable(true);
        persistData();
    }

    private void persistData() {
        try {
            UserDataPersister.instance().save(RuntimeData.getInstance().getUserData());
        } catch(IOException ex) {
            LOGGER.error("Failed to save deployment list", ex);
        }
    }

}
