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

import co.bitshifted.appforge.common.dto.ApplicationDTO;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ApplicationListCellFactory implements Callback<ListView<ApplicationDTO>, ListCell<ApplicationDTO>> {

    @Override
    public ListCell<ApplicationDTO> call(ListView<ApplicationDTO> applicationDTOListView) {
        return  new ListCell<>() {
            @Override
            protected void updateItem(ApplicationDTO applicationDTO, boolean empty) {
                super.updateItem(applicationDTO, empty);
                if(!empty && applicationDTO != null) {
                    setGraphic(createApplicationDetails(applicationDTO));
                    setText(null);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        };
    }

    private BorderPane createApplicationDetails(ApplicationDTO applicationDTO) {
        var container = new BorderPane();
        var vbox = new VBox();
        var titleLabel = new Label(applicationDTO.getName());
        titleLabel.getStyleClass().add("app-title-label");
        var headlineLabel = new Label(applicationDTO.getHeadline());
        headlineLabel.getStyleClass().add("app-headline-label");
        vbox.getChildren().addAll(titleLabel, headlineLabel);
        container.setCenter(vbox);
        return container;
    }
}
