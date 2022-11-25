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

import javafx.concurrent.Task;

public abstract class  BaseHttpTask<T> extends Task<T> {

    protected final SimpleHttpClient client;
    protected final String serverUrl;

    public BaseHttpTask(String serverUrl) {
        this.client = new SimpleHttpClient();
        this.serverUrl = serverUrl;
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
