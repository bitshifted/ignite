/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ui;

import co.bitshifted.appforge.common.model.JavaVersion;
import co.bitshifted.appforge.common.model.JvmVendor;
import co.bitshifted.appforge.common.util.JdkVersionAware;
import co.bitshifted.appforge.ignite.model.JdkTreeItemType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public final class JdkTreeItem implements JdkVersionAware {
    private final JvmVendor vendor;
    private final JavaVersion majorVersion;
    private final String jdkId;
    private final boolean autoUpdate;
    private final String release;
    private final boolean latest;
    private final String releaseId;
    private final JdkTreeItemType type;
    private final List<JdkTreeItem> children = new ArrayList<>();

    @Override
    public String getVersion() {
        return release;
    }

}
