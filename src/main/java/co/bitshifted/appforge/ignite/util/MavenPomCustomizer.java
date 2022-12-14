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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MavenPomCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenPomCustomizer.class);
    private static final String IGNITE_PLUGIN_GROUP = "co.bitshifted.appforge";
    private static final String IGNITE_PLUGIN_ARTIFACT = "ignite-maven-plugin";
    private static final String IGNITE_CURRENT_VERSION = "1.0.0-SNAPSHOT";

    private final Document pomDocument;
    private final XPath xpath;

    public MavenPomCustomizer(Path baseDir) throws ParserConfigurationException, SAXException, IOException {
        this(Files.newInputStream(baseDir.resolve("pom.xml")));
    }

    MavenPomCustomizer(InputStream input) throws ParserConfigurationException, SAXException, IOException {
        var dbf = DocumentBuilderFactory.newInstance();
        var docBuilder = dbf.newDocumentBuilder();
        pomDocument = docBuilder.parse(input);
        xpath = XPathFactory.newInstance().newXPath();
    }

    public void configureIgnite() throws XPathExpressionException {
        var expression = "/project/build/pluginManagement/plugins/plugin[(groupId[contains(., '%s')]) and (artifactId[contains(., '%s')])]";
        var pluginExists = pluginExists(expression);
        LOGGER.debug("Plugin is in pluginManagement: {}", pluginExists);
        expression = "/project/build/plugins/plugin[(groupId[contains(., '%s')]) and (artifactId[contains(., '%s')])]";
        pluginExists = pluginExists(expression);
        LOGGER.debug("Plugin is in plugins: {}", pluginExists);
        if(!pluginExists) {
            LOGGER.info("Ignite plugin not found, configuring...");

        }
    }

    // check if ignite plugin is in pluginManagement section
    private boolean pluginExists(String expression) throws XPathExpressionException {
        var input = String.format(expression, IGNITE_PLUGIN_GROUP, IGNITE_PLUGIN_ARTIFACT);
        LOGGER.debug("XPATH input: {}", input);
        var nodeList = (NodeList) xpath.compile(expression).evaluate(pomDocument, XPathConstants.NODESET);
        return nodeList.getLength() > 0;
    }

    private Node getPluginsManagementSection() {
        var pmSection = pomDocument.getElementsByTagName("pluginManagement");
        return pmSection.item(0);
    }

}
