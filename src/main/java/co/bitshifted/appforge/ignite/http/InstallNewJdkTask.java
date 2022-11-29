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

import co.bitshifted.appforge.common.dto.jdk.JdkInstallRequestDTO;
import co.bitshifted.appforge.common.dto.jdk.JdkInstallStatusDTO;
import co.bitshifted.appforge.common.model.JdkInstallationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InstallNewJdkTask extends BaseHttpTask<JdkInstallStatusDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallNewJdkTask.class);

    private final JdkInstallRequestDTO installRequest;

    public InstallNewJdkTask(String serverUrl, JdkInstallRequestDTO request) {
        super(serverUrl);
        this.installRequest = request;
    }

    @Override
    protected JdkInstallStatusDTO call() throws Exception {
        LOGGER.debug("Start JDK installation task...");
        var taskStatus = client.installNewJdk(serverUrl, List.of(installRequest));
        var taskId = taskStatus.getTaskId();
        LOGGER.debug("Install task ID: {}", taskId);
        updateValue(taskStatus);
        for(int i = 0;i < 200; i++) {
            LOGGER.info("Checking installation status...");
            var curStatus = client.getJdkInstallationStatus(serverUrl, taskId);
            updateValue(curStatus);
            taskStatus.setStatus(curStatus.getStatus());
            if(curStatus.getStatus() == JdkInstallationStatus.COMPLETED) {
                break;
            } else if(curStatus.getStatus() == JdkInstallationStatus.FAILED) {
                throw new Exception("Installation failed!");
            }
            Thread.sleep(5000);
        }
        return taskStatus;
    }
}
