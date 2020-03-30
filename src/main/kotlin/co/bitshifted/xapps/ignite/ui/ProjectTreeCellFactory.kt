/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ui


import co.bitshifted.xapps.ignite.model.ProjectItemType
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.scene.control.cell.TextFieldTreeCell
import javafx.util.Callback
import javafx.util.StringConverter

class ProjectTreeCellFactory : Callback<TreeView<ProjectTreeItem>, TreeCell<ProjectTreeItem>> {

    override fun call(treeView: TreeView<ProjectTreeItem>?): TreeCell<ProjectTreeItem> {
        return TextFieldTreeCell(Converter())
    }
}

class Converter : StringConverter<ProjectTreeItem>() {
    override fun toString(item: ProjectTreeItem?): String {
        when(item?.type) {
            ProjectItemType.ROOT -> return "Projects"
            ProjectItemType.APPLICATION -> return "Application"
            ProjectItemType.JVM -> return "JVM"
            ProjectItemType.PROJECT -> return item.project?.name ?: "Unknown"
            else -> return ""

        }

    }

    override fun fromString(p0: String?): ProjectTreeItem {
        return ProjectTreeItem(ProjectItemType.ROOT)
    }
}
