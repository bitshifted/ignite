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

public class RemoveInstalledJdkTask extends BaseHttpTask<Void> {

    private final String jdkId;
    private final String releaseId;

    public RemoveInstalledJdkTask(String serverUrl, String jdkId, String releaseId) {
        super(serverUrl);
        this.jdkId = jdkId;
        this.releaseId = releaseId;
    }

    @Override
    protected Void call() throws Exception {
        client.deleteInstalledJdk(serverUrl, jdkId, releaseId);
        return null;
    }
}
