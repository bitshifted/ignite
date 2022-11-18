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

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Comparator;

public final class UiUtils {

    public static final int TREE_ICON_SIZE = 17;

    private UiUtils(){

    }

    public static FontIcon getIcon(Ikon font) {
        var icon = new FontIcon(font);
        icon.setIconSize(TREE_ICON_SIZE);
        return icon;
    }

    public static Comparator<JdkTreeItem> jdkVersionComparator() {
        System.out.println("create comparator");
        return (first, second) -> {
            var parts1 = first.release().split(".");
            var minSize = parts1.length;
            var parts2 = second.release().split(".");
            if(parts2.length < minSize) {
                minSize = parts2.length;
            }
            for(int i = 0;i < minSize;i++) {
                if(parts1[i] == parts2[i]) {
                    continue;
                }
                if(Integer.parseInt(parts1[i]) > Integer.parseInt(parts2[i])) {
                    return 1;
                } else if(Integer.parseInt(parts1[i]) < Integer.parseInt(parts2[i])) {
                    return -1;
                }
            }
            if(parts1.length > parts2.length) {
                return 1;
            } else if(parts1.length < (parts2).length) {
                return -1;
            }
            return 0;
        };
    }
}
