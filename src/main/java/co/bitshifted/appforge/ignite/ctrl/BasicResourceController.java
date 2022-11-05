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
import co.bitshifted.appforge.ignite.model.ui.DirtyChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BasicResourceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicResourceController.class);

    @FXML
    private TextField sourceField;
    @FXML
    private TextField targetField;
    @FXML
    private Button addFileButton;
    @FXML
    private Button addFolderButton;
    @FXML
    private Button removeButton;
    @FXML
    private GridPane rootNode;

    private final BasicResourceUIModel resource;
    private final ResourceNotificationReceiver receiver;
    private final List<FileChooser.ExtensionFilter> filters;

    public BasicResourceController(BasicResourceUIModel resource, FileChooser.ExtensionFilter... filters) {
        this(resource, null, filters);
    }

    public BasicResourceController(BasicResourceUIModel resource, ResourceNotificationReceiver receiver, FileChooser.ExtensionFilter... filters) {
        this.resource = resource;
        this.receiver = receiver;
        if(filters != null) {
            this.filters = List.of(filters);
        } else {
            this.filters = List.of();
        }
    }

    @FXML
    public void initialize() {
        sourceField.textProperty().bindBidirectional(resource.getSourceProperty());
        targetField.textProperty().bindBidirectional(resource.getTargetProperty());
        sourceField.textProperty().addListener(new DirtyChangeListener<>());
        targetField.textProperty().addListener(new DirtyChangeListener<>());
    }

    @FXML
    public void onAddFileButton(ActionEvent event) {
        var parent = ((Parent)event.getTarget()).getScene().getWindow();
        var fileChooser = new FileChooser();
        var projectLocation = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getLocation();
        LOGGER.debug("Project directory: {}", projectLocation);
        fileChooser.setInitialDirectory(new File(projectLocation));
        if(!filters.isEmpty()) {
            fileChooser.getExtensionFilters().addAll(filters);
        }
        var selection = fileChooser.showOpenDialog(parent);
        if(selection != null) {
            var dirPath = Path.of(projectLocation);
            var relPath = dirPath.relativize(Path.of(selection.getPath()));
            sourceField.setText(relPath.toString());
            targetField.setText(relPath.toString());
        }
    }

    @FXML
    public void onAddFolder(ActionEvent event) {
        var parent = ((Parent)event.getTarget()).getScene().getWindow();
        var dirChooser = new DirectoryChooser();
        var projectLocation = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getLocation();
        dirChooser.setInitialDirectory(new File(projectLocation));
        var selection = dirChooser.showDialog(parent);
        if(selection != null) {
            var dirPath = Path.of(projectLocation);
            var relPath = dirPath.relativize(Path.of(selection.getPath()));
            sourceField.setText(relPath.toString());
            targetField.setText(relPath.toString());
        }
    }

    @FXML
    public void onRemove() {
        if(receiver != null) {
            receiver.getResourceViewParent().getChildren().remove(rootNode);
            receiver.resourceRemoved(resource);
        }
    }
}
