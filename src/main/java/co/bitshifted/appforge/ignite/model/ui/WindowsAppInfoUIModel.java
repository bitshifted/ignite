/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model.ui;

import co.bitshifted.appforge.common.model.ApplicationInfoPlatform;
import co.bitshifted.appforge.common.model.LinuxApplicationInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class WindowsAppInfoUIModel {

    private final ApplicationInfoPlatform source;
    private final ObservableList<BasicResourceUIModel> iconsUiModel;

    public WindowsAppInfoUIModel(ApplicationInfoPlatform source) {
        if(source == null) {
            this.source = new ApplicationInfoPlatform();
            this.iconsUiModel = FXCollections.observableArrayList();
        } else {
            this.source = source;
            this.iconsUiModel = FXCollections.observableList(
                source.getIcons().stream().map(i -> new BasicResourceUIModel(i)).collect(Collectors.toList()));
        }
    }

    public ObservableList<BasicResourceUIModel> getIconsUiModel() {
        return iconsUiModel;
    }

    public ApplicationInfoPlatform getSource() {
        source.setIcons(iconsUiModel.stream().map(ui -> ui.getResource()).collect(Collectors.toList()));
        return source;
    }
}
