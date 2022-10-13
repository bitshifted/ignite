/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.nio.file.Path;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Deployment {
    private final String location;

    @JsonIgnore
    private final SimpleObjectProperty<String> locationProperty;
    @JsonIgnore
    private final SimpleObjectProperty<String> configFileNameProperty;
    @JsonIgnore
    private final SimpleObjectProperty<DependencyManagementType> dependencyManagementTypeProperty;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Deployment(@JsonProperty("location") String location) {
        this.location = location;

        this.locationProperty = new SimpleObjectProperty<>(location);
        this.configFileNameProperty = new SimpleObjectProperty<>();
        this.dependencyManagementTypeProperty = new SimpleObjectProperty<>();
    }

    @JsonIgnore
    public String getName() {
        return Path.of(location).getFileName().toString();
    }

    public String getLocation() {
        return location;
    }

    public DependencyManagementType getDependencyManagementType() {
        return dependencyManagementTypeProperty.get();
    }

    @JsonProperty
    public void setDependencyManagementType(DependencyManagementType dependencyManagementType) {
        dependencyManagementTypeProperty.set(dependencyManagementType);
    }

    @JsonProperty
    public void setConfigFileName(String configFileName) {
        configFileNameProperty.set(configFileName);
    }

    public String getConfigFileName() {
        return configFileNameProperty.get();
    }


    @JsonIgnore 
    public IgniteConfig getConfiguration() {
        return new IgniteConfig();
    }

    // property getters
    public SimpleObjectProperty<String> getLocationProperty() {
        return locationProperty;
    }

    public SimpleObjectProperty<String> getConfigFileNameProperty() {
        return configFileNameProperty;
    }

    public SimpleObjectProperty<DependencyManagementType> getDependencyManagementTypeProperty() {
        return dependencyManagementTypeProperty;
    }
}
