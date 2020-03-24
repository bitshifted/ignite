/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.persist


import co.bitshifted.xapps.ignite.model.Application
import co.bitshifted.xapps.ignite.model.DependencyManagementType
import co.bitshifted.xapps.ignite.model.Project
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class XMLPersisterTest {

    val project = Project()
    val curDirectory = Path.of(System.getProperty("user.dir"), "target").toAbsolutePath().toString()

    @Before
    fun setup() {
        project.name = "Test project"
        project.location = curDirectory
        print(curDirectory)
        project.application = Application()

    }

    @After
    fun tearDown() {
        Files.delete(Path.of(curDirectory, "ignite-config.xml"))
    }

    @Test
    fun testSerialization() {
        XMLPersister.writeProject(project)

        // load project back
        val out = XMLPersister.loadProject(project.location ?: return)

        assertEquals(out.name, project.name)
        assertNotNull(out.application)
    }
}