/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite;

import co.bitshifted.appforge.common.model.CpuArch;
import co.bitshifted.appforge.ignite.persist.IgniteConfigPersister;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IgniteConfigPersisterTest {

    @Test
    public void configurationLaodTest() throws Exception {
        var persister = new IgniteConfigPersister();
        var location = getClass().getResource("/ignite-config-javafx.yml").getFile();
        var result = persister.load(location);
        assertNotNull(result);
        assertEquals("http://localhost:8080", result.getServerUrl());
        var appInfo = result.getApplicationInfo().getLinux();
        assertEquals(1, appInfo.getSupportedCpuArchitectures().size());
        assertTrue(appInfo.getSupportedCpuArchitectures().contains(CpuArch.X64));

    }
}
