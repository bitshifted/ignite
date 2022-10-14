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

import co.bitshifted.appforge.ignite.model.IgniteConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public class IgniteConfigPersister {

    private final ObjectMapper objectMapper;

    public IgniteConfigPersister() {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public IgniteConfig load(String location) throws IOException {
        var path = Path.of(location);
        return objectMapper.readValue(path.toFile(), IgniteConfig.class);
    }

    public void save(IgniteConfig config, Path target) throws IOException {
        objectMapper.writeValue(target.toFile(), config);
    }
}
