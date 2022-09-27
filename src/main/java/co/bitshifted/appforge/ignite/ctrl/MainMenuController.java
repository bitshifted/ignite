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
import co.bitshifted.appforge.ignite.model.Deployment;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;

import java.util.ResourceBundle;

public class MainMenuController {

    private final ResourceBundle bundle;

    @FXML
    private MenuItem newDeploymentMenuItem;

    public MainMenuController() {
        this.bundle = ResourceBundle.getBundle(IgniteConstants.MESSAGE_BUNDLE_NAME);
    }

    @FXML
    public void newDeploymentAction() {

        var dialog = new Dialog<Deployment>();
        dialog.setTitle(bundle.getString("deployment.dialog.title"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(UIRegistry.instance().getComponent(UIRegistry.DEPLOYMENT_INFO));
        dialog.setResultConverter(ControllerRegistry.instance().getController(DeploymentInfoController.class).getResultConverter());
        var result = dialog.showAndWait();
        if(result.isPresent()) {
            RuntimeData.getInstance().addDeployment(result.get());
        }
    }
}
