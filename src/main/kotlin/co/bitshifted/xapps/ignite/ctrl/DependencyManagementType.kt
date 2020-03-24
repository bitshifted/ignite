/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model

enum class DependencyManagementType(val display : String) {

    MANUAL ("Manual"), MAVEN ("Maven"), GRADLE ("Gradle");

    override fun toString(): String {
        return display
    }
}