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
import co.bitshifted.appforge.ignite.error.ErrorLoader
import co.bitshifted.appforge.ignite.error.IgniteException
import co.bitshifted.appforge.ignite.model.IgniteConfig
import co.bitshifted.appforge.ignite.model.Project
import co.bitshifted.appforge.ignite.yamlObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Path

object ProjectPersister {

    private val yamlObjectMapper = yamlObjectMapper()

    fun writeProject(project : Project) {
        val filePath = Path.of(project.location, DEFAULT_CONFIG_FILE_NAME)
        yamlObjectMapper.writeValue(filePath.toFile(), project.config)
    }

    fun loadProject(location : String, configFileName : String = DEFAULT_CONFIG_FILE_NAME) : Project {
        val projectPath = Path.of(location)
        val configFile = projectPath.resolve(configFileName)
        if (Files.notExists(configFile)) {
            throw IgniteException(ErrorLoader.errorInfo(ErrorCode.CONFIG_FILE_NOT_FOUND))
        }
        val config = yamlObjectMapper.readValue(configFile.toFile(), IgniteConfig::class.java)
        return Project(config, location, configFileName)
    }
}