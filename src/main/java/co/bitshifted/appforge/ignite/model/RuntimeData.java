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

import co.bitshifted.appforge.ignite.persist.DeploymentDataPersister;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RuntimeData {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeData.class);
    private static final RuntimeData INSTANCE;

    static {
        INSTANCE = new RuntimeData();
    }

    private ObservableList<Deployment> deploymentsList;
    private SimpleObjectProperty<Deployment> selectedDeployment;

    private RuntimeData() {
        this.deploymentsList = FXCollections.observableArrayList();
        this.selectedDeployment = new SimpleObjectProperty<>();
    }

    public ObservableList<Deployment> getDeploymentsList() {
        return deploymentsList;
    }

    public void addDeployment(Deployment deployment) {
        this.deploymentsList.add(deployment);
    }

    public SimpleObjectProperty<Deployment> getSelectedDeployment() {
        return selectedDeployment;
    }

    public static RuntimeData getInstance() {
        return INSTANCE;
    }
}
