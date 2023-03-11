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
import co.bitshifted.appforge.common.model.CpuArch;
import co.bitshifted.appforge.common.model.LinuxApplicationInfo;
import co.bitshifted.appforge.common.model.WindowsApplicationInfo;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WindowsAppInfoUIModel {

    private final WindowsApplicationInfo source;
    private final ObservableList<BasicResourceUIModel> iconsUiModel;
    private final SimpleBooleanProperty archX86Supported;

    public WindowsAppInfoUIModel(WindowsApplicationInfo source) {
        if(source == null) {
            this.source = new WindowsApplicationInfo();
            this.iconsUiModel = FXCollections.observableArrayList();
        } else {
            this.source = source;
            var icons = source.getIcons();
            if(icons == null) {
                icons = List.of();
            }
            this.iconsUiModel = FXCollections.observableList(
                icons.stream().map(i -> new BasicResourceUIModel(i)).collect(Collectors.toList()));
        }
        this.archX86Supported = new SimpleBooleanProperty(this.source.getSupportedCpuArchitectures().contains(CpuArch.X64));
    }

    public ObservableList<BasicResourceUIModel> getIconsUiModel() {
        return iconsUiModel;
    }

    public WindowsApplicationInfo getSource() {
        source.setIcons(iconsUiModel.stream().map(ui -> ui.getResource()).collect(Collectors.toList()));
        // only x64 supported for Windows
        source.setSupportedCpuArchitectures(Set.of(CpuArch.X64));
        return source;
    }

    public SimpleBooleanProperty getArchX86SupportedProperty() {
        return archX86Supported;
    }
}
