/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite

import javafx.scene.control.Alert
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <T : Any> T.logger(clazz : Class<T>) : Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(clazz) }
}

fun showAlert(type : Alert.AlertType, headerText : String, contentText : String) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.headerText = headerText
    alert.contentText = contentText
    alert.showAndWait()
}