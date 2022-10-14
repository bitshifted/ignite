/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.persist;

import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.UserData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserDataPersister {

    private static final String STORAGE_FILE_NAME = "userdata.json";
    private static final UserDataPersister INSTANCE;

    static {
        INSTANCE = new UserDataPersister();
    }

    private final ObjectMapper mapper;

    private UserDataPersister() {
        this.mapper = new ObjectMapper();
    }

    public Path getStoragePath() {
        return Path.of(System.getProperty("user.home"), IgniteAppConstants.STORAGE_DIR_NAME, STORAGE_FILE_NAME);
    }

    public void save(UserData userData) throws IOException {
        var path = getStoragePath();
        Files.createDirectories(path.getParent());
        mapper.writeValue(path.toFile(), userData);
    }

    public UserData load() throws IOException {
        var path = getStoragePath();
        if(Files.exists(path)) {
            var userData = mapper.readValue(path.toFile(), UserData.class);
            userData.getDeployments().forEach(d -> d.initConfiguration());
            return userData;
        }
        return new UserData();
    }

    public static UserDataPersister instance() {
        return INSTANCE;
    }
}
