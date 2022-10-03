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

import java.nio.file.Path;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Deployment {
    private final String location;
    @JsonIgnore
    private final String configFileName;
    private final DependencyManagementType dependencyManagementType;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Deployment(@JsonProperty("location") String location, @JsonProperty("dependencyManagementType") DependencyManagementType dependencyManagementType) {
        this.location = location;
        this.dependencyManagementType = dependencyManagementType;
        this.configFileName = Path.of(location).getFileName().toString();
    }


    public String getName() {
        return Path.of(location).getFileName().toString();
    }

    public String getLocation() {
        return location;
    }


    public DependencyManagementType getDependencyManagementType() {
        return dependencyManagementType;
    }


}
