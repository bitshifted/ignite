/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MavenPomCustomizerTest {

    private final String pomContent = """
<project>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>some.group</groupId>
                    <artifactId>some-artifact</artifactId>
                </plugin>
            </plugins>
        </pluginManagement>
        <plugins>
        </plugins>
    </build>
</project>
""";

    @Test
    void testProcessConfigurationPluginNotConfigured() throws Exception {
        var sis = new ByteArrayInputStream(pomContent.getBytes(StandardCharsets.UTF_8));
        var customizer = new MavenPomCustomizer(sis);
        customizer.configureIgnite();
        sis.close();
    }
}
