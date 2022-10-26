/*
 *
 *  * Copyright (c) 2022-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model.ui;

import co.bitshifted.appforge.common.model.ApplicationInfo;
import co.bitshifted.appforge.common.model.OperatingSystem;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashSet;
import java.util.Set;

public class ApplicationInfoUIModel extends ApplicationInfo {

    private ApplicationInfo source;

    private final BasicResourceUIModel licenseUiModel;
    private final SimpleBooleanProperty windowsSupportedProperty;
    private final SimpleBooleanProperty macSupportedProperty;
    private final SimpleBooleanProperty linuxSupportedProperty;
    private final SimpleStringProperty execNameProperty;

    public ApplicationInfoUIModel(ApplicationInfo source) {
        this.source = source;
        this.licenseUiModel = new BasicResourceUIModel(source.getLicense());
        this.windowsSupportedProperty = new SimpleBooleanProperty(source != null && source.getSupportedOperatingSystems().contains(OperatingSystem.WINDOWS));
        this.macSupportedProperty = new SimpleBooleanProperty(source != null && source.getSupportedOperatingSystems().contains(OperatingSystem.MAC));
        this.linuxSupportedProperty = new SimpleBooleanProperty(source != null && source.getSupportedOperatingSystems().contains(OperatingSystem.LINUX));
        this.execNameProperty = new SimpleStringProperty(source.getExeName());
    }

    public BasicResourceUIModel getLicenseUiModel() {
        return licenseUiModel;
    }

    public SimpleBooleanProperty windowsSupportedProperty() {
        return windowsSupportedProperty;
    }

    public SimpleBooleanProperty macSupportedProperty() {
        return macSupportedProperty;
    }

    public SimpleBooleanProperty linuxSupportedProperty() {
        return linuxSupportedProperty;
    }

    public SimpleStringProperty execNameProperty() {
        return execNameProperty;
    }

    public ApplicationInfo getSource() {
        source.setLicense(licenseUiModel.getResource());
        source.setSupportedOperatingSystems(getSupportedSystems());
        source.setExeName(execNameProperty.get());
        return source;
    }

    private Set<OperatingSystem> getSupportedSystems() {
        var set = new HashSet<OperatingSystem>();
        if(windowsSupportedProperty.get()) {
            set.add(OperatingSystem.WINDOWS);
        }
        if(macSupportedProperty.get()) {
            set.add(OperatingSystem.MAC);
        }
        if(linuxSupportedProperty.get()) {
            set.add(OperatingSystem.LINUX);
        }
        return set;
    }
}
