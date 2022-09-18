/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.error

import java.util.ResourceBundle

object ErrorLoader {
    private val unknownMessage = "Unknown error message"

   private val messages : ResourceBundle by lazy {
       ResourceBundle.getBundle("i18n/errors")
   }

    fun errorInfo(code : ErrorCode) : ErrorInfo {
        val msg = try {
            messages.getString(code.name)
        } catch(e : Exception) {
            unknownMessage
        }
        return ErrorInfo(msg, code)
    }
}