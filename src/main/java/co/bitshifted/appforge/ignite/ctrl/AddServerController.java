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
import co.bitshifted.appforge.ignite.model.Server;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddServerController {

    @FXML
    private TextField serverNameField;
    @FXML
    private TextField baseUrlField;
    @FXML
    private Label serverNameErrorLabel;
    @FXML
    private Label serverUrlErrorLabel;

    private ResourceBundle bundle;
    private Server currentServer;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle(IgniteConstants.MESSAGE_BUNDLE_NAME);
    }

    public Callback<ButtonType, Server> getResultConverter() {
        return buttonType -> {
            if(buttonType == ButtonType.OK) {
                if(currentServer == null) {
                    currentServer = new Server();
                }
                currentServer.setName(serverNameField.getText());
                currentServer.setBaseUrl(baseUrlField.getText());
                return currentServer;
            }
            return null;
        };
    }

    public void clear() {
        serverNameField.clear();
        baseUrlField.clear();
        serverUrlErrorLabel.setVisible(false);
        serverNameErrorLabel.setVisible(false);
        this.currentServer = null;
    }

    public void setObject(Server server) {
        serverNameField.setText(server.getName());
        baseUrlField.setText(server.getBaseUrl());
        serverUrlErrorLabel.setVisible(false);
        serverNameErrorLabel.setVisible(false);
        this.currentServer = server;
    }

    public boolean validateInput() {
        if(serverNameField.getText().isEmpty() || serverNameField.getText().isBlank()) {
            serverNameErrorLabel.setText(bundle.getString("error.server.name.required"));
            serverNameErrorLabel.setVisible(true);
            return false;
        } else {
            serverNameErrorLabel.setVisible(false);
        }
        if(baseUrlField.getText().isEmpty() || baseUrlField.getText().isBlank()) {
            serverUrlErrorLabel.setText(bundle.getString("error.server.url.required"));
            serverUrlErrorLabel.setVisible(true);
            return false;
        } else {
            try {
                var url = new URL(baseUrlField.getText());
                serverUrlErrorLabel.setVisible(false);
            } catch(MalformedURLException ex) {
                serverUrlErrorLabel.setText(bundle.getString("error.server.url.invalid"));
                return false;
            }
        }
        return true;
    }

}
