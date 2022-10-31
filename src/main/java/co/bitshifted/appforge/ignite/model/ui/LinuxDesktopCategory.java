/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model.ui;

import java.util.List;

public record LinuxDesktopCategory(String name, String description,
                                   List<LinuxDesktopCategory> additionalCategories) {

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}
