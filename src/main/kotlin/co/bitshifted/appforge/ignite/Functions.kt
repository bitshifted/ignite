/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite

import co.bitshifted.appforge.ignite.model.JvmDependencyScope
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

fun <T : Any> T.logger(clazz : Class<T>) : Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(clazz) }
}

fun showAlert(type : Alert.AlertType, headerText : String, contentText : String) {
    val alert = Alert(type)
    alert.headerText = headerText
    alert.contentText = contentText
    alert.showAndWait()
}

fun iconExtensionFilters() : List<FileChooser.ExtensionFilter> {
    return listOf(
        FileChooser.ExtensionFilter("All Icons", "*.png", "*.ico", "*.icns"),
        FileChooser.ExtensionFilter("PNG", ".png"),
        FileChooser.ExtensionFilter("ICO", "*.ico"),
        FileChooser.ExtensionFilter("ICNS", "*.icns")
    )
}

fun filePathRelative(filePath : String, parent : String) : String {
    if(filePath.startsWith(parent)) {
        return filePath.substring(parent.length + 1)
    }
    return filePath
}

fun getLocalMavenRepoDir() : File {
    val homeDir = System.getProperty("user.home")
    return Path.of(homeDir, ".m2", "repository").toFile()
}

fun dependencyDeploymentPath(fileName : String, scope: JvmDependencyScope) : String {
    val dir = if(scope == JvmDependencyScope.CLASSPATH){
        PACKAGE_CLASSPATH_DIR
    } else {
        PACKAGE_MODULES_DIR
    }
    return "$dir/$fileName"
}