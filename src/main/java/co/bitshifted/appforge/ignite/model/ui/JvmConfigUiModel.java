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

import co.bitshifted.appforge.common.dto.JvmConfigurationDTO;
import co.bitshifted.appforge.common.model.JavaVersion;
import co.bitshifted.appforge.common.model.JvmVendor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JvmConfigUiModel {

    private final JvmConfigurationDTO source;
    private final SimpleObjectProperty<JvmVendor> jvmVendorProperty;
    private final SimpleObjectProperty<JavaVersion> javaVersionProperty;
    private final SimpleStringProperty releaseProperty;
    private final SimpleStringProperty mainClassProperty;
    private final SimpleStringProperty moduleNameProperty;
    private final SimpleStringProperty argumentsProperty;
    private final SimpleStringProperty jvmOptionsProperty;
    private final SimpleStringProperty systemPropertiesProperty;
    private final SimpleStringProperty addModulesProperty;
    private final SimpleStringProperty jlinkIgnoreModulesProperty;

    public JvmConfigUiModel(JvmConfigurationDTO source) {
        this.source = source;
        this.jvmVendorProperty = new SimpleObjectProperty<>(source.getVendor());
        this.javaVersionProperty = new SimpleObjectProperty<>(source.getMajorVersion());
        this.releaseProperty = new SimpleStringProperty(source.getRelease());
        this.mainClassProperty = new SimpleStringProperty(source.getMainClass());
        this.moduleNameProperty = new SimpleStringProperty(source.getModuleName());
        this.argumentsProperty = new SimpleStringProperty(source.getArguments());
        this.jvmOptionsProperty = new SimpleStringProperty(source.getJvmOptions());
        this.systemPropertiesProperty = new SimpleStringProperty(source.getSystemProperties());
        this.addModulesProperty = new SimpleStringProperty(source.getAddModules());
        this.jlinkIgnoreModulesProperty = new SimpleStringProperty(modulesToString(source.getJlinkIgnoreModules()));
    }

    public JvmConfigurationDTO getSource() {
        source.setVendor(jvmVendorProperty.get());
        source.setMajorVersion(javaVersionProperty.get());
        source.setRelease(releaseProperty.get());
        source.setMainClass(mainClassProperty.get());
        source.setModuleName(moduleNameProperty.get());
        source.setArguments(argumentsProperty.get());
        source.setJvmOptions(jvmOptionsProperty.get());
        source.setSystemProperties(systemPropertiesProperty.get());
        source.setAddModules(addModulesProperty.get());
        source.setJlinkIgnoreModules(stringToModules(jlinkIgnoreModulesProperty.get()));
        return source;
    }

    public SimpleObjectProperty<JvmVendor> jvmVendorProperty() {
        return jvmVendorProperty;
    }

    public SimpleObjectProperty<JavaVersion> javaVersionProperty() {
        return javaVersionProperty;
    }

    public SimpleStringProperty releaseProperty() {return releaseProperty; }

    public SimpleStringProperty mainClassProperty() {
        return mainClassProperty;
    }

    public SimpleStringProperty moduleNameProperty() {
        return moduleNameProperty;
    }

    public SimpleStringProperty argumentsProperty() {
        return argumentsProperty;
    }

    public SimpleStringProperty jvmOptionsProperty() {
        return jvmOptionsProperty;
    }

    public SimpleStringProperty systemPropertiesProperty() {
        return systemPropertiesProperty;
    }

    public SimpleStringProperty addModulesProperty() {
        return addModulesProperty;
    }

    public SimpleStringProperty jlinkIgnoreModulesProperty() {
        return jlinkIgnoreModulesProperty;
    }

    private String modulesToString(Set<String> moduleNames) {
        if(moduleNames == null) {
            return "";
        }
        var sb = new StringBuilder();
        moduleNames.forEach( m -> sb.append(m).append("\n"));
        return sb.toString().trim();
    }

    private Set<String> stringToModules(String input) {
        var result = new HashSet<String>();
        var newlineSplit = input.split("\n");
        Arrays.stream(newlineSplit).forEach(s -> {
            var commaSplit = s.split(",");
            Arrays.stream(commaSplit).filter(i -> i.length() > 0).forEach(t -> result.add(t.trim()));
        });
        return result;
    }
}
