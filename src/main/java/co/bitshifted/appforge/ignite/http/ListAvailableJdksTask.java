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

import co.bitshifted.appforge.common.util.JdkVersionComparator;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import co.bitshifted.appforge.ignite.ui.JdkTreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListAvailableJdksTask extends BaseHttpTask<List<JdkTreeItem>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListAvailableJdksTask.class);


    public ListAvailableJdksTask(String serverUrl) {
       super(serverUrl);
    }

    @Override
    protected List<JdkTreeItem> call() throws Exception {
        var platformInfoList =  client.getAvailableJdks(serverUrl);
        var output = new ArrayList<JdkTreeItem>();
        platformInfoList.stream().forEach(plInfo -> {
            var vendorItem = JdkTreeItem.builder().vendor(plInfo.getVendor()).type(JdkTreeItemType.VENDOR).build();
            plInfo.getSupportedVersions().stream().forEach(releaseDto -> {
                var releases = releaseDto.getReleases().stream().map(rl -> JdkTreeItem.builder().type(JdkTreeItemType.RELEASE).release(rl).build()).sorted(new JdkVersionComparator().reversed()).collect(Collectors.toList());
                // mark first item as latest
                var latestRelease = releases.get(0).getRelease();
                releases.set(0, JdkTreeItem.builder().type(JdkTreeItemType.RELEASE).release(latestRelease).latest(true).build());
                var majorVersionItem = JdkTreeItem.builder().type(JdkTreeItemType.MAJOR_VERSION).majorVersion(releaseDto.getMajorVersion()).build();
                majorVersionItem.getChildren().addAll(releases);
                vendorItem.getChildren().add(majorVersionItem);
            });
            output.add(vendorItem);
        });
        return output;
    }
}
