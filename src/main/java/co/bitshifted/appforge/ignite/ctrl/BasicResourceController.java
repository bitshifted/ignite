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
import co.bitshifted.appforge.ignite.model.ui.BasicResourceUIModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class BasicResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicResourceController.class);

    @FXML
    private TextField sourceField;
    @FXML
    private TextField targetField;
    @FXML
    private Button browseButton;

    private final BasicResourceUIModel resource;

    public BasicResourceController(BasicResourceUIModel resource) {
        this.resource = resource;
    }

    @FXML
    public void initialize() {
        sourceField.textProperty().bindBidirectional(resource.getSourceProperty());
        targetField.textProperty().bindBidirectional(resource.getTargetProperty());
    }

    @FXML
    public void onBrowseButton(ActionEvent event) {
        var parent = ((Parent)event.getTarget()).getScene().getWindow();
        var fileChooser = new FileChooser();
        var projectLocation = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getLocation();
        LOGGER.debug("Project directory: {}", projectLocation);
        fileChooser.setInitialDirectory(new File(projectLocation));
        var selection = fileChooser.showOpenDialog(parent);
        if(selection != null) {
            var dirPath = Path.of(projectLocation);
            var relPath = dirPath.relativize(Path.of(selection.getPath()));
            sourceField.setText(relPath.toString());
            targetField.setText(relPath.toString());
        }

    }
}
