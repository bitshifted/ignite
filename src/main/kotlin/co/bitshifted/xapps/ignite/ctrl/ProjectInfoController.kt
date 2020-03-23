/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.logger
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField


class ProjectInfoController : ChangeListener<ProjectTreeItem> {

    private val log by logger(ProjectInfoController::class.java)

    @FXML
    private lateinit var projectNameField : TextField
    @FXML
    private lateinit var projectLocationField : TextField



    @FXML
    fun initialize() {
        RuntimeData.selectedProjectItem.addListener(this)
    }

    override fun changed(observable: ObservableValue<out ProjectTreeItem>?, oldValue: ProjectTreeItem?, newValue: ProjectTreeItem?) {
        log.debug("Current project name: ${newValue?.project?.name}")
        projectNameField.text = newValue?.project?.name
        projectLocationField.text = newValue?.project?.location
    }
}