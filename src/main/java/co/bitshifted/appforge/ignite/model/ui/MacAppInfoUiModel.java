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
import co.bitshifted.appforge.common.model.MacApplicationInfo;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MacAppInfoUiModel {

    private final MacApplicationInfo source;
    private final ObservableList<BasicResourceUIModel> iconsUiModel;
    private final SimpleBooleanProperty archX86SupportedProperty;
    private final SimpleBooleanProperty archArmSupportedProperty;

    public MacAppInfoUiModel(MacApplicationInfo source) {
        if(source == null) {
            this.source = new MacApplicationInfo();
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
        this.archX86SupportedProperty = new SimpleBooleanProperty(source != null && source.getSupportedCpuArchitectures().contains(CpuArch.X64));
        this.archArmSupportedProperty = new SimpleBooleanProperty(source != null && source.getSupportedCpuArchitectures().contains(CpuArch.AARCH64));
    }

    public ObservableList<BasicResourceUIModel> getIconsUiModel() {
        return iconsUiModel;
    }

    public MacApplicationInfo getSource() {
        source.setIcons(iconsUiModel.stream().map(ui -> ui.getResource()).collect(Collectors.toList()));
        source.setSupportedCpuArchitectures(getSupportedCpuArchitectures());
        return source;
    }

    private Set<CpuArch> getSupportedCpuArchitectures() {
        var set = new HashSet<CpuArch>();
        if(archX86SupportedProperty.get()) {
            set.add(CpuArch.X64);
        }
        if(archArmSupportedProperty.get()) {
            set.add(CpuArch.AARCH64);
        }
        return set;
    }

    public SimpleBooleanProperty getArchX86SupportedProperty() {
        return archX86SupportedProperty;
    }

    public SimpleBooleanProperty getArchArmSupportedProperty() {return archArmSupportedProperty; }
}
