/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model

import javafx.beans.property.SimpleStringProperty
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlTransient

class Application {
    @XmlTransient
    val appIdProperty = SimpleStringProperty()
    var appId : String
        @XmlAttribute(name = "id")
        get() = appIdProperty.get() ?: ""
        set(value) = appIdProperty.set(value)

    @XmlTransient
    val versionProperty = SimpleStringProperty()
    var version : String?
        @XmlAttribute
        get() = versionProperty.get()
        set(value) = versionProperty.set(value)


    @XmlElement(name = "application-info")
    val info = ApplicationInfo()
}