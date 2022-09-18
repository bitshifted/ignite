/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ctrl

import co.bitshifted.appforge.ignite.model.Server
import co.bitshifted.appforge.ignite.persist.ProjectPersistenceData
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.util.Callback

class AddServerController {

    @FXML
    private lateinit var serverNameField : TextField
    @FXML
    private lateinit var baseUrlField : TextField

    fun getResultConverter() : Callback<ButtonType, Server?> {
        return object : Callback<ButtonType, Server?> {
            override fun call(btype: ButtonType?): Server? {
                if(btype == ButtonType.OK) {
                    return createServer()
                }
                return null
            }
        }
    }

    fun validateInput() : Boolean {
        return serverNameField.text.isNotEmpty() && baseUrlField.text.isNotEmpty()
    }

    private fun createServer() : Server {
        val server = Server(serverNameField.text, baseUrlField.text)

        ProjectPersistenceData.saveServer(server)
        return server
    }
}
