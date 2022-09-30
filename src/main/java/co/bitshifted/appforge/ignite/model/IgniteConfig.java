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

import co.bitshifted.appforge.common.dto.JvmConfigurationDTO;
import co.bitshifted.appforge.common.model.ApplicationInfo;
import co.bitshifted.appforge.common.model.BasicResource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IgniteConfig {

    @JsonProperty("server-url")
    private String serverUrl;
    @JsonProperty("application-id")
    private String applicationId;
    private ApplicationInfo applicationInfo;
    private List<BasicResource> resources;
    private JvmConfigurationDTO jvmConfigurationDTO;
}
