/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.model

import co.bitshifted.xapps.ignite.model.JvmDependency
import co.bitshifted.xapps.ignite.model.JvmDependencyScope
import java.util.*
import javax.xml.bind.annotation.XmlAttribute

//@XmlAccessorType(XmlAccessType.FIELD)
class MavenDependency(@XmlAttribute(name = "group-id") var groupId : String = "",
                      @XmlAttribute(name = "artifact-id") var artifactId : String = "",
                      @XmlAttribute var version : String = "",
                      @XmlAttribute var packaging : String = "jar",
                      @XmlAttribute var classifier : String? = null, name : String = "", fileSize : Long = 0, scope: JvmDependencyScope = JvmDependencyScope.CLASSPATH)
    : JvmDependency(name, "",  fileSize, scope) {

    override fun equals(other: Any?): Boolean {
        if(other is MavenDependency) {
            return (other.groupId == groupId && other.artifactId == artifactId && other.version == version && other.packaging == packaging && other.classifier == classifier)
        }
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(groupId, artifactId, version, packaging, classifier)
    }

}