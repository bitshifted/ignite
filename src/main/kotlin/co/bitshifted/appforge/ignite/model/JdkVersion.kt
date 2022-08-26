/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.model;

const val JDK_11_VALUE = "11"
const val JDK_12_VALUE = "12"
const val JDK_13_VALUE = "13"
const val JDK_14_VALUE = "14"

enum class JdkVersion(val display : String, val lts: Boolean) {
    JDK_11 (JDK_11_VALUE, true),
    JDK_12 (JDK_12_VALUE, false),
    JDK_13 (JDK_13_VALUE, false),
    JDK_14 (JDK_14_VALUE, false)
}