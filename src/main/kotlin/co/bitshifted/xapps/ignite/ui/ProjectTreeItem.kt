/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ui

import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.ProjectItemType

class ProjectTreeItem (val type : ProjectItemType) {

    var project : Project? = null

    constructor(type : ProjectItemType, project : Project) : this(type) {
        this.project = project
    }


}