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


import co.bitshifted.appforge.ignite.model.Project
import co.bitshifted.appforge.ignite.model.Server
import java.util.*
import java.util.prefs.Preferences

object ProjectPersistenceData {

    private const val IGNITE_PROJECTS_ROOT = "co.bitshifted.appforge.ignite.pojects"
    private const val IGNITE_SERVERS_ROOT = "co.bitshifted.appforge.ignite.servers"
    private const val CONFIG_FILE_NAME_KEY = "configFileName"
    const val LOCATION_KEY = "location"
    const val NAME_KEY = "name"
    const val URL_KEY = "url"

    private lateinit var projectsRootNode : String
    private lateinit var serversRootNode : String

    /**
     * Must call this function to initialize object.
     */
    fun init(projectsRoot : String = IGNITE_PROJECTS_ROOT, serversRoot : String = IGNITE_SERVERS_ROOT) {
        this.projectsRootNode = projectsRoot
        this.serversRootNode = serversRoot
    }

    /**
     * Save project to target preference node. Provided node name is user root node
     * for prefernces storage.
     *
     * @param project
     * @param userRootNodeName preference node name
     */
    fun saveProject(project : Project) {
        val rootNode = Preferences.userRoot().node(projectsRootNode)
        val projectNode = rootNode.node(escapeSlashes(project.location))
        projectNode.put(LOCATION_KEY, project.location)
        projectNode.put(NAME_KEY, project.name)
        projectNode.put(CONFIG_FILE_NAME_KEY, project.configFileName)
    }

    fun loadProjectLocations() : List<String> {
        val locations = mutableListOf<String>()
        val rootNode = Preferences.userRoot().node(projectsRootNode)
        for(location in rootNode.childrenNames()) {
            val projectNode = rootNode.node(location)
            val loc = projectNode.get(LOCATION_KEY, "")
            if(loc.isNotEmpty()) {
                locations.add(loc)
            }

        }
        return locations
    }

    fun saveServer(server : Server) {
        val serversRoot = Preferences.userRoot().node(serversRootNode)
        val uuid = UUID.randomUUID().toString()
        val serverNode = serversRoot.node(uuid)
        serverNode.put(NAME_KEY, server.name)
        serverNode.put(URL_KEY, server.baseUrl)
    }

    fun loadServers() : List<Server> {
        val list = mutableListOf<Server>()
        val serversRoot = Preferences.userRoot().node(serversRootNode)
        for(srvId in serversRoot.childrenNames()) {
            val serverNode = serversRoot.node(srvId)
            val server = Server(name = serverNode.get(NAME_KEY, " "), baseUrl = serverNode.get(URL_KEY, ""))
            list.add(server)
        }
        return list
    }

    private fun escapeSlashes(value : String?) : String {
        return value?.replace("/", "___") ?: ""
    }
}