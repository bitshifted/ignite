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

import co.bitshifted.appforge.ignite.model.DeploymentItemType;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInfoLinuxController implements ChangeListener<DeploymentTreeItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoLinuxController.class);

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing controller");
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends DeploymentTreeItem> observableValue, DeploymentTreeItem oldValue, DeploymentTreeItem newValue) {
        if(newValue.type() == DeploymentItemType.APPLICATION_INFO_LINUX) {
            LOGGER.debug("Application info linux selected");
            var selectedDeployment = newValue.deployment();
            var appInfoUiModel = selectedDeployment.getConfiguration().applicationInfoProperty().get();
            LOGGER.debug("appInfo UI model: {}", appInfoUiModel);
        }
    }
}
