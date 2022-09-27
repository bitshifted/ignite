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

import co.bitshifted.appforge.ignite.IgniteConstants;
import co.bitshifted.appforge.ignite.model.Deployment;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DeploymentDataPersister {

    private static final String STORAGE_FILE_NAME = "deployment.json";
    private static final DeploymentDataPersister INSTANCE;

    static {
        INSTANCE = new DeploymentDataPersister();
    }

    private final ObjectMapper mapper;

    private DeploymentDataPersister() {
        this.mapper = new ObjectMapper();
    }

    public Path getStoragePath() {
        return Path.of(System.getProperty("user.home"), IgniteConstants.STORAGE_DIR_NAME, STORAGE_FILE_NAME);
    }

    public void save(List<Deployment> deploymentList) throws IOException {
        var path = getStoragePath();
        Files.createDirectories(path.getParent());
        mapper.writeValue(path.toFile(), deploymentList);
//        try(var os = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
//            os.writeObject(deploymentList);
//        }
    }

    public Deployment[] load() throws IOException, ClassNotFoundException {
        var path = getStoragePath();
        return mapper.readValue(path.toFile(), Deployment[].class);
//        try(var is = new ObjectInputStream(new FileInputStream(path.toFile()))) {
//            return (List<Deployment>) is.readObject();
//        }
    }

    public static DeploymentDataPersister instance() {
        return INSTANCE;
    }
}
