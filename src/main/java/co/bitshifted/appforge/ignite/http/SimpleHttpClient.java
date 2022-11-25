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
import co.bitshifted.appforge.common.dto.jdk.InstalledJdkDTO;
import co.bitshifted.appforge.common.dto.jdk.JavaPlatformInfoDTO;
import co.bitshifted.appforge.common.dto.jdk.JdkInstallRequestDTO;
import co.bitshifted.appforge.common.dto.jdk.JdkInstallStatusDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimpleHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);
    private static final String LIST_APPLICATIONS_ENDPOINT = "v1/applications";
    private static final String JDK_MANAGEMENT_ENDPOINT = "/v1/jdks";
    private static final String AVAILABLE_JDKS_ENDPOINT = "/v1/jdks/available";
    private static final String DELETE_JDK_ENDPOINT = "/v1/jdks/%s/releases/%s";
    private static final String INSTALL_JDK_ENDPOINT = "/v1/jdks";
    private static final String JDK_INSTALL_STATUS_ENDPOINT = "/v1/jdks/installations/%s";

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

    public List<InstalledJdkDTO> getInstalledJdks(String serverUrl) throws IOException {
        LOGGER.debug("Getting installed JDKs list");
        var target = getTargetUrl(serverUrl, JDK_MANAGEMENT_ENDPOINT);
        var request = new Request.Builder().url(target).get().build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        return mapper.readValue(response.body().byteStream(), new TypeReference<>() {});
    }

    public List<JavaPlatformInfoDTO> getAvailableJdks(String serverUrl) throws IOException {
        LOGGER.debug("Getting available JDKs list");
        var target = getTargetUrl(serverUrl, AVAILABLE_JDKS_ENDPOINT);
        var request = new Request.Builder().url(target).get().build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        return mapper.readValue(response.body().byteStream(), new TypeReference<>() {});
    }

    public void deleteInstalledJdk(String serverUrl, String jdkId, String releaseId) throws Exception {
        var target = getTargetUrl(serverUrl, String.format(DELETE_JDK_ENDPOINT, jdkId, releaseId));
        LOGGER.debug("Remove JDK url: {}", target);
        var request = new Request.Builder().url(target).delete().build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        if(response.code() != 204) {
            throw new IOException("Removal failed. Status=" + response.code());
        }
    }

    public JdkInstallStatusDTO installNewJdk(String serverUrl, List<JdkInstallRequestDTO> installRequest) throws Exception {
        var target = getTargetUrl(serverUrl, INSTALL_JDK_ENDPOINT);
        var body = RequestBody.create(mapper.writeValueAsString(installRequest), MediaType.parse("application/json"));
        LOGGER.debug("Install request: {}", mapper.writeValueAsString(installRequest));
        var request = new Request.Builder().url(target).post(body).build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        if(response.code() != 202) {
            throw new Exception("Failed to submit install request: Response code " + response.code());
        }
        return  mapper.readValue(response.body().byteStream(), JdkInstallStatusDTO.class);
    }

    public JdkInstallStatusDTO getJdkInstallationStatus(String serverUrl, String taskId) throws IOException {
        var target = getTargetUrl(serverUrl, String.format(JDK_INSTALL_STATUS_ENDPOINT, taskId));
        LOGGER.debug("Remove JDK url: {}", target);
        var request = new Request.Builder().url(target).get().build();
        var call = okHttpClient.newCall(request);
        var response = call.execute();
        return mapper.readValue(response.body().byteStream(), JdkInstallStatusDTO.class);
    }

    private String getTargetUrl(String serverUrl, String path) {
        var target = new StringBuilder(serverUrl);
        if(serverUrl.endsWith("/")) {
            target.append(path);
        } else {
            target.append("/").append(path);
        }
        return target.toString().replace("//", "/");
    }
}
