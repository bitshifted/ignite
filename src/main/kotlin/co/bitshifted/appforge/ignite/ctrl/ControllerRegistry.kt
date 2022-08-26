/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.ctrl

object ControllerRegistry {

    private val controllerMap = mutableMapOf<String, Any>()

    fun registerControllers() {
        controllerMap[ProjectInfoController::class.java.name] = ProjectInfoController()
//        controllerMap[NewMavenDependencyController::class.java.name] = NewMavenDependencyController()
        controllerMap[AddServerController::class.java.name] = AddServerController()
    }

    fun <T>  getController(clazz : Class<T>) : T = controllerMap[clazz.name] as T
}