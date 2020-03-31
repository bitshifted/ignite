/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javax.xml.bind.annotation.*

const val PROJECT_XML_ELEMENT_NAME = "ignite-project"

@XmlRootElement(name = PROJECT_XML_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.FIELD)
class Project  {

    @XmlTransient
    var synced : Boolean = false

    @XmlTransient
    val nameProperty = SimpleStringProperty("")
    var name : String
        @XmlAttribute
        get() = nameProperty.get()
        set(value) = nameProperty.set(value)

    @XmlTransient
    val locationProperty = SimpleStringProperty("")
    var location : String
        @XmlTransient
        get() = locationProperty.get()
        set(value) = locationProperty.set(value)

    @XmlTransient
    val dependencyManagementTypeProperty = SimpleObjectProperty(DependencyManagementType.MAVEN)
    var dependencyManagementType : DependencyManagementType
        @XmlAttribute(name = "dependency-management-type")
        get() = dependencyManagementTypeProperty.get()
        set(value) = dependencyManagementTypeProperty.set(value)

    @XmlTransient
    val serverProperty = SimpleObjectProperty<Server>()
    var server : Server?
        @XmlElement
        get() = serverProperty.get()
        set(value) = serverProperty.set(value)

    @XmlElement
    val application = Application()
    @XmlElement
    val jvm = Jvm()


}