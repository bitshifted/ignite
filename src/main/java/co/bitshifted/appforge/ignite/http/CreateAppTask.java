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
import javafx.concurrent.Task;

public class CreateAppTask extends Task<ApplicationDTO> {

    private final SimpleHttpClient client;
    private final ApplicationDTO input;
    private final String serverUrl;

    public CreateAppTask(ApplicationDTO input, String serverUrl) {
        this.input = input;
        this.serverUrl = serverUrl;
        this.client = new SimpleHttpClient();
    }

    @Override
    protected ApplicationDTO call() throws Exception {
        return client.createApplication(input, serverUrl);
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
