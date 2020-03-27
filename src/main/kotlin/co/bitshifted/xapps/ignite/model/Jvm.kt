/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javax.xml.bind.annotation.*


@XmlAccessorType(XmlAccessType.PROPERTY)
//@XmlSeeAlso(MavenDependency::class)
class Jvm {

    @XmlTransient
    val dependenciesProperty = SimpleListProperty<JvmDependency>(FXCollections.observableArrayList())
    var dependencies : List<JvmDependency>
    @XmlElementWrapper(name = "dependencies") @XmlElement(name = "dependency") get() = dependenciesProperty.get()
    set(value)  {
        dependenciesProperty.clear()
        dependenciesProperty.addAll(value)
    }

//    @XmlElement(name = "platform-specific-dependencies")
//    val platformDependencies = PlatformSpecificDependencies()

    @XmlTransient
    val mainClassProperty = SimpleObjectProperty<String>()
    var mainClass : String?
        @XmlElement(name = "main-class") get()  = mainClassProperty.get()
        set(value) = mainClassProperty.set(value)

    @XmlTransient
    val jvmOptionsProperty = SimpleObjectProperty<String>()
    var jvmOptions : String?
       @XmlElement(name = "jvm-options") set(value) = jvmOptionsProperty.set(value)
        get() = jvmOptionsProperty.get()

    @XmlTransient
    val sysPropertiesProperty = SimpleObjectProperty<String>()
    var sysProperties : String?
       @XmlElement(name = "system-properties") set(value) = sysPropertiesProperty.set(value)
        get() = sysPropertiesProperty.get()

    val splashScreenProperty = SimpleObjectProperty<BinaryData>()
    var splashScreen : BinaryData?
        @XmlElement(name = "splash-screen") set(value) = splashScreenProperty.set(value)
        get() = splashScreenProperty.get()

    @XmlTransient
    val argumentsProperty = SimpleObjectProperty<String>()
    var arguments : String?
    @XmlElement set(value) = argumentsProperty.set(value)
    get() = argumentsProperty.get()

    @XmlTransient
    val jdkProviderProperty = SimpleObjectProperty<JdkProvider>(JdkProvider.OPENJDK)
    var provider : JdkProvider
    @XmlAttribute set(value) = jdkProviderProperty.set(value)
    get() = jdkProviderProperty.get()

    @XmlTransient
    val jvmImplementationProperty = SimpleObjectProperty<JvmImplementation>(JvmImplementation.HOTSPOT)
    var implementation : JvmImplementation
    @XmlAttribute set(value) = jvmImplementationProperty.set(value)
    get() = jvmImplementationProperty.get()


    @XmlTransient
    val jdkVersionProperty = SimpleObjectProperty<JdkVersion>(JdkVersion.JDK_11)
    var version : JdkVersion
    @XmlAttribute set(value) = jdkVersionProperty.set(value)
    get() = jdkVersionProperty.get()

    @XmlTransient
    val exactVersionProperty = SimpleObjectProperty<String>("")
    var exactVersion : String
    @XmlAttribute(name = "exact-version") set(value) = exactVersionProperty.set(value)
    get() = exactVersionProperty.get()


    fun addDependency(dependency : JvmDependency) = dependenciesProperty.add(dependency)


}