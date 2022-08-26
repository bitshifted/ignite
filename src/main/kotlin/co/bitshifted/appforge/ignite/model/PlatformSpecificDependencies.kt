/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.model

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
class PlatformSpecificDependencies {

    @XmlTransient
    val macDependenciesProperty = SimpleListProperty<JvmDependency>(FXCollections.observableArrayList())
    @XmlElementWrapper(name = "mac")
    @XmlElement(name = "dependency")
    fun getMacDependencies() : List<JvmDependency> {
        return macDependenciesProperty.get()
    }

    fun setMacDependencies(value : List<JvmDependency>) {
        macDependenciesProperty.clear()
        macDependenciesProperty.addAll(value)
    }


    @XmlTransient
    val winDependenciesProperty = SimpleListProperty<JvmDependency>(FXCollections.observableArrayList())
    @XmlElementWrapper(name = "windows")
    @XmlElement(name = "dependency")
    fun getWindowsDependencies() = winDependenciesProperty.get()

    fun setWindowsDependencies(value : List<JvmDependency>) {
        winDependenciesProperty.clear()
        winDependenciesProperty.addAll(value)
    }

    @XmlTransient
    val linuxDependenciesProperty = SimpleListProperty<JvmDependency>(FXCollections.observableArrayList())
    @XmlElementWrapper(name = "linux")
    @XmlElement(name = "dependency")
    fun getLinuxDependencies() = linuxDependenciesProperty.get()

    fun setLinuxDependencies(value : List<JvmDependency>) {
        linuxDependenciesProperty.clear()
        linuxDependenciesProperty.addAll(value)
    }


    fun addPlatformSpecificDependency(dependencies: List<JvmDependency>, os: OperatingSystem) {
        when (os) {
            OperatingSystem.MAC_OS_X -> macDependenciesProperty.addAll(dependencies)
            OperatingSystem.WINDOWS -> winDependenciesProperty.addAll(dependencies)
            OperatingSystem.LINUX -> linuxDependenciesProperty.addAll(dependencies)
        }
    }

    fun isPlatformSpecificDependency(dependency: JvmDependency) : Boolean {
        return (macDependenciesProperty.contains(dependency)) || winDependenciesProperty.contains(dependency) || linuxDependenciesProperty.contains(dependency)
    }
}