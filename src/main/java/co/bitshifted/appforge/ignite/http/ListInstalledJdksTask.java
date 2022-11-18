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

import co.bitshifted.appforge.common.dto.jdk.InstalledJdkDTO;
import javafx.concurrent.Task;

import java.util.List;

public class ListInstalledJdksTask extends Task<List<InstalledJdkDTO>> {

    private final SimpleHttpClient client;
    private final String serverUrl;

    public ListInstalledJdksTask(String serverUrl) {
        this.client = new SimpleHttpClient();
        this.serverUrl = serverUrl;
    }

    @Override
    protected List<InstalledJdkDTO> call() throws Exception {
        return client.getInstalledJdks(serverUrl);
    }
}
