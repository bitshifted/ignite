/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeData {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeData.class);
    private static final RuntimeData INSTANCE;

    static {
        INSTANCE = new RuntimeData();
    }

    private SimpleObjectProperty<UserData> userData;
    private SimpleListProperty<Server> serversList;
    private ObservableList<Deployment> deploymentsList;
    private SimpleObjectProperty<Deployment> selectedDeployment;

    private RuntimeData() {
        this.userData = new SimpleObjectProperty<>();
        this.serversList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.deploymentsList = FXCollections.observableArrayList();
        this.selectedDeployment = new SimpleObjectProperty<>();
    }

    public void setUserData(UserData userData) {
        this.userData.set(userData);
        deploymentsList.addAll(userData.getDeployments());
        serversList.addAll(userData.getServers());
        LOGGER.debug("servers list: {}", userData.getServers());
    }

    public UserData getUserData() {
        userData.get().setDeployments(deploymentsList);
        userData.get().setServers(serversList);
        return userData.get();
    }

    public ObservableList<Deployment> getDeploymentsList() {
        return deploymentsList;
    }

    public SimpleListProperty<Server> getServersList() {
        return serversList;
    }

    public void addDeployment(Deployment deployment) {
        this.userData.get().getDeployments().add(deployment);
        this.deploymentsList.add(deployment);
    }


    public SimpleObjectProperty<Deployment> getSelectedDeployment() {
        return selectedDeployment;
    }

    public static RuntimeData getInstance() {
        return INSTANCE;
    }
}
