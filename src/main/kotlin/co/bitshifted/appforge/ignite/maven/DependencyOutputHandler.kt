/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.maven

import org.apache.maven.shared.invoker.PrintStreamHandler
import java.util.regex.Pattern

class DependencyOutputHandler(val dependencyList : MutableList<String>) : PrintStreamHandler() {

    val DEPENDENCY__WITH_CLASSIFIER_PATTERN = Pattern.compile("[a-zA-Z0-9-\\.]+:[a-zA-Z0-9-\\.]+:(jar|zip):([a-zA-Z]+)?:[a-zA-Z0-9-\\.]+:(compile|runtime|provided|system)")
    val DEPENDENCY_PATTERN = Pattern.compile("([a-zA-Z0-9-\\.]+):([a-zA-Z0-9-\\.]+):(jar|zip):([a-zA-Z0-9-\\.]+):(compile|runtime|provided|system)$")

    override fun consumeLine(line: String?) {
        val matcher = DEPENDENCY_PATTERN.matcher(line)

        if(matcher.find()) {
            dependencyList.add(matcher.group())
            return
        }
        val classifierMatcher = DEPENDENCY__WITH_CLASSIFIER_PATTERN.matcher(line)
        if(classifierMatcher.find()) {
            dependencyList.add(classifierMatcher.group())
        }
    }
}