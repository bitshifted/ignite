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
import co.bitshifted.appforge.common.util.JdkVersionComparator;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import co.bitshifted.appforge.ignite.ui.JdkTreeItem;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.stream.Collectors;

import static co.bitshifted.appforge.ignite.util.Helpers.distinctByKey;

public class ListInstalledJdksTask extends Task<List<JdkTreeItem>> {

    private final SimpleHttpClient client;
    private final String serverUrl;

    public ListInstalledJdksTask(String serverUrl) {
        this.client = new SimpleHttpClient();
        this.serverUrl = serverUrl;
    }

    @Override
    protected List<JdkTreeItem> call() throws Exception {
        var installedJdks =  client.getInstalledJdks(serverUrl);
        var vendorTreeItems = installedJdks.stream().filter(distinctByKey(jdk -> jdk.getVendor())).map(jdk -> JdkTreeItem.builder().vendor(jdk.getVendor()).type(JdkTreeItemType.VENDOR).build()).collect(Collectors.toList());
        vendorTreeItems.stream().forEach(vendorTreeItem -> {
            var majorVersions = installedJdks.stream().filter(jdk -> jdk.getVendor() == vendorTreeItem.getVendor())
                .map(jdk -> JdkTreeItem.builder().type(JdkTreeItemType.MAJOR_VERSION).majorVersion(jdk.getMajorVersion()).jdkId(jdk.getId()).autoUpdate(jdk.getAutoUpdate()).build())
                .collect(Collectors.toList());
            majorVersions.stream().forEach(majorVersionItem -> {
                var jdkReleases = installedJdks.stream().filter(jdk -> jdk.getVendor() == vendorTreeItem.getVendor() && jdk.getMajorVersion() == majorVersionItem.getMajorVersion()).findFirst().get().getReleases();
                var releaseItems = jdkReleases.stream().map(rl -> JdkTreeItem.builder().type(JdkTreeItemType.RELEASE).release(rl.getRelease()).latest(rl.getLatest()).releaseId(rl.getId()).build()).sorted(new JdkVersionComparator().reversed()).collect(Collectors.toList());
                majorVersionItem.getChildren().addAll(releaseItems);
            });
            vendorTreeItem.getChildren().addAll(majorVersions);
        });
        return vendorTreeItems;
    }
}
