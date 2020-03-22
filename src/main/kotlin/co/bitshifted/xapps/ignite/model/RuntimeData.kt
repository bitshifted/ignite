/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model


import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object RuntimeData {

    val projectList : ObservableList<Project> = FXCollections.observableArrayList()
    var selectedProjectItem  = SimpleObjectProperty<ProjectTreeItem>()
}