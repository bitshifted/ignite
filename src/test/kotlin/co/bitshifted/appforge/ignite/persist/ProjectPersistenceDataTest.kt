/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.persist


import co.bitshifted.appforge.ignite.model.IgniteConfig
import co.bitshifted.appforge.ignite.model.Project
import co.bitshifted.appforge.ignite.model.Server
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.prefs.Preferences
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

const val TEST_PROJECT_ROOT_NODE = "dummy.projects.root"
const val TEST_SERVER_ROOT_NODE = "dummy.projects.root"

class ProjectPersistenceDataTest {

    private lateinit  var project1 : Project
    private lateinit  var project2 : Project

    private lateinit var server1 : Server

    @BeforeEach
    fun setup() {
        project1 = Project(IgniteConfig(), "/some/location/project1")
        project2 = Project(IgniteConfig(), "/some/other/location/project2")

        server1 = Server(baseUrl = "http://localhost:8080", name = "My Server")

        ProjectPersistenceData.init(TEST_PROJECT_ROOT_NODE, TEST_SERVER_ROOT_NODE)
    }

    @AfterEach
    fun tearDown() {
        val rootNode = Preferences.userRoot().node(TEST_PROJECT_ROOT_NODE)
        rootNode.removeNode()
        Preferences.userRoot().flush()
    }

    @Test
    fun testSaveProject() {
        ProjectPersistenceData.saveProject(project1)
        ProjectPersistenceData.saveProject(project2)

        val rootNode = Preferences.userRoot().node(TEST_PROJECT_ROOT_NODE)
        val nodeNames = rootNode.childrenNames()

        assertEquals(2, nodeNames.size)
        for(nodeName in nodeNames) {
            val node = rootNode.node(nodeName)
            assertNotNull(node)
            if(nodeName.endsWith("project1")){
                verifyProjectData(node, project1)
            }
            if(nodeName.endsWith("project2")) {
                verifyProjectData(node, project2)
            }

        }
    }

    @Test
    fun loadProjectsTest() {
        ProjectPersistenceData.saveProject(project1)
        ProjectPersistenceData.saveProject(project2)

        val list = ProjectPersistenceData.loadProjectLocations()
        assertEquals(2, list.size)
    }

    @Test
    fun testSaveServer() {
        ProjectPersistenceData.saveServer(server1)

        val rootNode = Preferences.userRoot().node(TEST_SERVER_ROOT_NODE)
        val nodeNames = rootNode.childrenNames()

        assertEquals(1, nodeNames.size)
        for(nodeName in nodeNames) {
            val node = rootNode.node(nodeName)
            assertNotNull(node)
            verifyServerData(node, server1)

        }
    }

    @Test
    fun testLoadServer() {
        ProjectPersistenceData.saveServer(server1)

        val list = ProjectPersistenceData.loadServers()
        assertEquals(1, list.size)
        for(server in list) {
            assertEquals(server.name, server1.name)
            assertEquals(server.baseUrl, server1.baseUrl)
        }
    }

    private fun verifyProjectData(node : Preferences, project: Project) {
        assertEquals(project.name, node.get(ProjectPersistenceData.NAME_KEY, ""))
        assertEquals(project.location, node.get(ProjectPersistenceData.LOCATION_KEY, ""))
    }

    private fun verifyServerData(node : Preferences, server : Server) {
        assertEquals(server.name, node.get(ProjectPersistenceData.NAME_KEY, ""))
        assertEquals(server.baseUrl, node.get(ProjectPersistenceData.URL_KEY, ""))
    }
}