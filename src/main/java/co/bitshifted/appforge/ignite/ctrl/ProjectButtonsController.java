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

import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.persist.IgniteConfigPersister;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ProjectButtonsController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectButtonsController.class);

    private final IgniteConfigPersister persister;

    public ProjectButtonsController() {
        this.persister = new IgniteConfigPersister();
    }

    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        saveButton.setDisable(true);
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @FXML
    public void saveProject(){
        LOGGER.debug("save project");
        var deployment = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment();
        var dir = deployment.getLocation();
        var fileName = deployment.getConfigFileName();
        try {
            persister.save(deployment.getConfiguration(), Path.of(dir, fileName));
        } catch(IOException ex) {
            LOGGER.error("Failed to save deployment", ex);
        }

    }

    @FXML
    public void deployProject() {
        LOGGER.debug("deploy project");
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        LOGGER.debug("Deployment changed");
        var deployment = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment();
        if(deployment != null) {
            LOGGER.debug("deployment dirty: {}", deployment.getConfiguration().dirtyProperty().get());
            saveButton.disableProperty().bind(deployment.getConfiguration().dirtyProperty().not());
        }
    }
}
