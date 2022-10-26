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

import co.bitshifted.appforge.common.dto.ApplicationDTO;
import co.bitshifted.appforge.common.model.ApplicationInfo;
import co.bitshifted.appforge.ignite.model.ui.ApplicationInfoUIModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IgniteConfig {

    @JsonIgnore
    private final SimpleObjectProperty<Server> serverProperty;
    @JsonIgnore
    private final SimpleObjectProperty<ApplicationDTO> applicationIdProperty;
    @JsonIgnore
    private final SimpleObjectProperty<ApplicationInfoUIModel> applicationInfoProperty;
    @JsonIgnore
    private final SimpleBooleanProperty dirtyProperty;

    public IgniteConfig() {
        this.serverProperty = new SimpleObjectProperty<>();
        this.applicationIdProperty = new SimpleObjectProperty<>();
        this.dirtyProperty = new SimpleBooleanProperty(false);
        this.applicationInfoProperty = new SimpleObjectProperty<>();

        this.serverProperty.addListener((observableValue, server, t1) -> dirtyProperty.set(true));
        this.applicationIdProperty.addListener((observable, oldValue, newValue) -> dirtyProperty.set(true));
    }

    @JsonProperty("server-url")
    public void setServerUrl(String url) {
        var server = new Server("Untitled", url);
        serverProperty.set(server);
    }

    public String getServerUrl() {
        return serverProperty.get().getBaseUrl();
    }

    @JsonProperty("application-id")
    public void setApplicationId(String id) {
        var app = new ApplicationDTO();
        app.setId(id);
        applicationIdProperty.set(app);
    }

    public String getApplicationId() {
        return applicationIdProperty.get().getId();
    }

    @JsonProperty("application-info")
    public void setApplicationInfo(ApplicationInfo appInfo) {
        if(appInfo != null) {
            this.applicationInfoProperty.set(new ApplicationInfoUIModel(appInfo));
        } else {
            this.applicationInfoProperty.set(new ApplicationInfoUIModel(new ApplicationInfo()));
        }
    }

    public ApplicationInfo getApplicationInfo() {
        return this.applicationInfoProperty.get().getSource();
    }

    public SimpleObjectProperty<Server> serverProperty() {
        return serverProperty;
    }

    public SimpleObjectProperty<ApplicationDTO> applicationIdProperty() {
        return applicationIdProperty;
    }

    public SimpleObjectProperty<ApplicationInfoUIModel> applicationInfoProperty() {
        return applicationInfoProperty;
    }

    public SimpleBooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

}
