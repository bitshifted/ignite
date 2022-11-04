/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model;

import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.model.ui.LinuxDesktopCategory;
import co.bitshifted.appforge.ignite.ui.DeploymentTreeItem;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class RuntimeData {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeData.class);
    private static final RuntimeData INSTANCE;

    static {
        INSTANCE = new RuntimeData();
    }

    private SimpleObjectProperty<UserData> userData;
    private SimpleListProperty<Server> serversList;
    private ObservableList<Deployment> deploymentsList;
    private SimpleObjectProperty<DeploymentTreeItem> selectedDeploymentItem;
    private List<LinuxDesktopCategory> linuxDesktopCategories;

    private RuntimeData() {
        this.userData = new SimpleObjectProperty<>();
        this.serversList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.deploymentsList = FXCollections.observableArrayList();
        this.selectedDeploymentItem = new SimpleObjectProperty<>();
        this.linuxDesktopCategories = new ArrayList<>();
    }

    public void init() throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        var docBuilder = dbf.newDocumentBuilder();
        var document  = docBuilder.parse(getClass().getResourceAsStream(IgniteAppConstants.LINUX_CATEGORIES_DATA));
        var xpath = XPathFactory.newInstance().newXPath();
        var nodeList = (NodeList)xpath.compile("/categories/*").evaluate(document, XPathConstants.NODESET);
        for(int i = 0;i < nodeList.getLength();i++) {
            var node = (Element)nodeList.item(i);
            var name = node.getAttribute("name");
            var desc = node.getAttribute("description");
            var childrenList = new ArrayList<LinuxDesktopCategory>();
            var children = node.getChildNodes();
            for(int k=0;k < children.getLength();k++) {
                if(children.item(k) instanceof Element) {
                    var child = (Element)children.item(k);
                    var childName = child.getAttribute("name");
                    var childDesc = child.getAttribute("description");
                    childrenList.add(new LinuxDesktopCategory(childName, childDesc, List.of()));
                }

            }
            var category = new LinuxDesktopCategory(name, desc, childrenList);
            linuxDesktopCategories.add(category);
        }
    }

    public void setUserData(UserData userData) {
        this.userData.set(userData);
        deploymentsList.addAll(userData.getDeployments());
        serversList.addAll(userData.getServers());
        LOGGER.debug("servers list: {}", userData.getServers());
    }

    public UserData getUserData() {
        userData.get().setDeployments(deploymentsList);
        userData.get().setServers(serversList);
        return userData.get();
    }

    public ObservableList<Deployment> getDeploymentsList() {
        return deploymentsList;
    }

    public SimpleListProperty<Server> getServersList() {
        return serversList;
    }

    public List<LinuxDesktopCategory> getLinuxDesktopCategories() {
        return linuxDesktopCategories;
    }

    public void addDeployment(Deployment deployment) {
        this.userData.get().getDeployments().add(deployment);
        this.deploymentsList.add(deployment);
    }

    public SimpleObjectProperty<DeploymentTreeItem> selectedDeploymentTreeITemProperty() {
        return selectedDeploymentItem;
    }

    public static RuntimeData getInstance() {
        return INSTANCE;
    }
}
