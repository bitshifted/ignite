/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ui

import co.bitshifted.xapps.ignite.filePathRelative
import co.bitshifted.xapps.ignite.model.RuntimeData
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.io.File
import java.nio.file.Paths

class IconBar(iconFilePath : String, projectLocation : String, parent : VBox) : HBox(15.0) {

    private val ICON_WIDTH = 32.0
    private val ICON_HEIGHT = 32.0
    private val removeButton : Button

    init {
        alignment = Pos.CENTER_LEFT
        children.add(ImageView(Image(getIconFile(iconFilePath, projectLocation).inputStream(), ICON_WIDTH, ICON_HEIGHT, false, false)))
        val pathField = TextField(filePathRelative(iconFilePath, projectLocation))
        pathField.editableProperty().set(false)
        pathField.prefWidth(80.0)
        children.add(pathField)

        removeButton = Button("Remove")
        removeButton.onAction = EventHandler{
            parent.children.remove(this)
            RuntimeData.selectedProjectItem.get().project?.application?.info?.removeIcon(iconFilePath)
        }
        children.add(removeButton)
    }



    private fun getIconFile(path : String, projectLocation: String) : File {
        val p = Paths.get(path)
        if(p.isAbsolute) {
            return p.toFile()
        } else {
            return File(projectLocation, path)
        }
    }

}