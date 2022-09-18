/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.persist

import co.bitshifted.appforge.ignite.DEFAULT_CONFIG_FILE_NAME
import co.bitshifted.appforge.ignite.error.ErrorCode
import co.bitshifted.appforge.ignite.error.IgniteException
import co.bitshifted.appforge.ignite.model.IgniteConfig
import co.bitshifted.appforge.ignite.model.Project
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProjectPersisterTest {

    private val sampleConfig = "ignite-config-javafx.yml"

    @Test
    fun writeProjectSuccess() {
        val config = IgniteConfig(appId = "12345", serverUrl = "http://localhost")
        val dir = Files.createTempDirectory("ignite-test")
        val project = Project(config, dir.absolutePathString())
        ProjectPersister.writeProject(project)
        // verify output
        val outPath = dir.resolve(DEFAULT_CONFIG_FILE_NAME)
        val content = Files.readString(outPath)
        println(content)
        assertTrue(content.contains("application-id: \"12345\""))
    }

    @Test
    fun loadProjectErrorNonExistingFile() {
        val result = Assertions.assertThrows(IgniteException::class.java,
            {ProjectPersister.loadProject("/some/location")})
        assertEquals(ErrorCode.CONFIG_FILE_NOT_FOUND, result.errorInfo.code)
    }

    @Test
    fun loadProjectDefaultConfigSuccess() {
        val configFileInput = javaClass.getResourceAsStream("/$sampleConfig")
        val sampleDir = Files.createTempDirectory("project-persist-test")
        val configFile = sampleDir.resolve(DEFAULT_CONFIG_FILE_NAME)
        Files.copy(configFileInput, configFile)
        val result = ProjectPersister.loadProject(sampleDir.absolutePathString())
        assertNotNull(result)
        assertEquals("kq9Dra2hwGN", result.config.appId)
        assertEquals( "http://localhost:8080", result.config.serverUrl)
    }
}