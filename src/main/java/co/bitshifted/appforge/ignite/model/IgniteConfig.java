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
    private final SimpleBooleanProperty dirtyProperty;

    public IgniteConfig() {
        this.serverProperty = new SimpleObjectProperty<>();
        this.dirtyProperty = new SimpleBooleanProperty(false);

        this.serverProperty.addListener((observableValue, server, t1) -> {
            System.out.println("server changed");
            dirtyProperty.set(true);
        });
    }

    @JsonProperty("server-url")
    public void setServerUrl(String url) {
        var server = new Server("Untitled", url);
        serverProperty.set(server);
    }

    public String getServerUrl() {
        return serverProperty.get().getBaseUrl();
    }

    public SimpleObjectProperty<Server> serverProperty() {
        return serverProperty;
    }

    public SimpleBooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

//    @JsonProperty("server-url")
//    private String serverUrl;
//    @JsonProperty("application-id")
//    private String applicationId;
//    private ApplicationInfo applicationInfo;
//    private List<BasicResource> resources;
//    private JvmConfigurationDTO jvmConfigurationDTO;
}
