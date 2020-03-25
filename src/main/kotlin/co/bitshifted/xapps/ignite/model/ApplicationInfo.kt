/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.xapps.ignite.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlTransient

/**
 * @author Vladimir Djurovic
 */
class ApplicationInfo {

    @XmlTransient
    val appNameProperty = SimpleStringProperty()
    var appName : String?
        @XmlElement(name = "application-name")
        get() = appNameProperty.get()
        set(value) = appNameProperty.set(value)

    @XmlTransient
    val iconsProperty = SimpleListProperty<BinaryData>(FXCollections.observableArrayList())
    var icons : List<BinaryData>
        @XmlElementWrapper(name = "icons")
        @XmlElement(name = "icon")
        get() = iconsProperty.get()
        set(value)  {
            iconsProperty.clear()
            iconsProperty.addAll(value)
        }


    fun addIcon(icon : BinaryData) {
        iconsProperty.add(icon)
    }

    fun removeIcon(path : String) {
        val toRemove = iconsProperty.find { it.path == path }
        iconsProperty.remove(toRemove)
    }

}