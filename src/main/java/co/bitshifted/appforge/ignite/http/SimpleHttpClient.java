/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.http;

import co.bitshifted.appforge.common.dto.ApplicationDTO;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SimpleHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);
    private static final String LIST_APPLICATIONS_ENDPOINT = "v1/applications";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper mapper;

    public SimpleHttpClient() {
        this.okHttpClient = new OkHttpClient.Builder().build();
        this.mapper = new ObjectMapper();
    }

    public List<ApplicationDTO> listApplication() throws IOException {
        var serverUrl = RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().getServerUrl();
        var target = new StringBuilder(serverUrl);
        if(serverUrl.endsWith("/")) {
            target.append(LIST_APPLICATIONS_ENDPOINT);
        } else {
            target.append("/").append(LIST_APPLICATIONS_ENDPOINT);
        }
        LOGGER.debug("List application URL: {}", target);
        var request = new Request.Builder().url(target.toString()).get().build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        var type = mapper.getTypeFactory().constructCollectionType(List.class, ApplicationDTO.class);
        return mapper.readValue(response.body().byteStream(), type);
    }
}
