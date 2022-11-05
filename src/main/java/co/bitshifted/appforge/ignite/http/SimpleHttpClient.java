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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimpleHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);
    private static final String LIST_APPLICATIONS_ENDPOINT = "v1/applications";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper mapper;

    public SimpleHttpClient() {
        this.okHttpClient = new OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).build();
        this.mapper = new ObjectMapper();
    }

    public List<ApplicationDTO> listApplication(String serverUrl) throws IOException {
        LOGGER.debug("Getting applications list...");
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
        var appList = mapper.readValue(response.body().byteStream(), AppListContentDTO.class);
        return appList.getContent();
    }

    public ApplicationDTO createApplication(ApplicationDTO applicationDTO, String serverUrl) throws Exception {
        LOGGER.debug("Creating application {}", applicationDTO);
        var target = new StringBuilder(serverUrl);
        if(serverUrl.endsWith("/")) {
            target.append(LIST_APPLICATIONS_ENDPOINT);
        } else {
            target.append("/").append(LIST_APPLICATIONS_ENDPOINT);
        }
        var body = RequestBody.create(mapper.writeValueAsString(applicationDTO), MediaType.parse("application/json"));
        var request = new Request.Builder().url(target.toString()).post(body).build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        return mapper.readValue(response.body().byteStream(), ApplicationDTO.class);
    }
}
