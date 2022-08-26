/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.model

import javax.xml.bind.annotation.XmlAttribute


open class BinaryData(@XmlAttribute val path : String = "",
                      @XmlAttribute(name = "file-name") val fileName : String = "",
                      @XmlAttribute val size : Long = 0) {

}