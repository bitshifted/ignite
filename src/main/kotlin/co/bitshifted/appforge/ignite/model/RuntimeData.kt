/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model


import co.bitshifted.appforge.ignite.ui.ProjectTreeItem
import co.bitshifted.appforge.ignite.watch.ObservableStack
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.FileSystems

object RuntimeData {

    val projectList : ObservableList<Project> = FXCollections.observableArrayList()
    var selectedProjectItem  = SimpleObjectProperty<ProjectTreeItem>()

    val fileChangeQueue = ObservableStack<Project>()

}