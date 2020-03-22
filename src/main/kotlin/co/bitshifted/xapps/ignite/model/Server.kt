/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model

import java.util.*
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

@XmlAccessorType(XmlAccessType.FIELD)
class Server {

    @XmlAttribute (name = "base-url")
    lateinit var baseUrl : String
    @XmlAttribute
    lateinit var name : String

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (other is Server) {
            return other.baseUrl == baseUrl && other.name == name
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(baseUrl, name)
    }
}