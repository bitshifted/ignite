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
import co.bitshifted.appforge.ignite.model.Deployment;
import co.bitshifted.appforge.ignite.model.IgniteConfig;
import co.bitshifted.appforge.ignite.persist.IgniteConfigPersister;
import javafx.concurrent.Task;

import java.util.List;

public class ListAppsTask extends Task<List<ApplicationDTO>> {

    private final SimpleHttpClient client;
    private final Deployment deployment;
    private final String serverUrl;

    public ListAppsTask(Deployment deployment) {
        this.client = new SimpleHttpClient();
        this.deployment = deployment;
        this.serverUrl = null;
    }

    public ListAppsTask(String serverUrl) {
        this.client = new SimpleHttpClient();
        this.deployment = null;
        this.serverUrl = serverUrl;
    }

    @Override
    protected List<ApplicationDTO> call() throws Exception {
        if(deployment != null) {
            return client.listApplication(deployment.getConfiguration().getServerUrl());
        } else {
            return client.listApplication(serverUrl);
        }
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public String processErrorMessage(String separator) {
        if(getException() == null) {
            return "";
        }
        var sb = new StringBuilder();
        var msg = getException().getMessage();
        if (msg != null) {
            sb.append(msg);
        }
        var cause = getException().getCause();
        if(cause != null) {
            var causeMsg = cause.getMessage();
            sb.append(separator).append(causeMsg);
        }
        return sb.toString();
    }
}
