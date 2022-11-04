/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ui;

import co.bitshifted.appforge.ignite.IgniteAppConstants;
import co.bitshifted.appforge.ignite.ctrl.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class UIRegistry {

    private static final UIRegistry INSTANCE;

    // Component names
    public static final String MAIN_PAGE = "main-page";
    public static final String MAIN_MENU = "main-menu";
    public static final String DEPLOYMENT_INFO_DLG = "deployment-info-dlg";
    public static final String DEPLOYMENT_INFO = "deployment-info";
    public static final String  APPLICATION_INFO_UI = "app-info";
    public static final String  APPLICATION_INFO_LINUX_UI = "app-info-linux";
    public static final String  APPLICATION_INFO_WINDOWS_UI = "app-info-windows";
    public static final String  APPLICATION_INFO_MAC_UI = "app-info-mac";
    public static final String SERVER_MANAGEMENT = "server-management";
    public static final String ADD_SERVER_PANE = "add-server-pane";
    public static final String PROJECT_BUTTONS_BAR = "project-button-bar";
    public static final String JVM_INFO = "jvm-info";
    public static final String RESOURCES_DATA = "resources-data";

    static {
        INSTANCE = new UIRegistry();
    }

    private final Map<String, Parent> componentMap;
    private Stage mainWindow;

    private UIRegistry() {
        componentMap = new HashMap<>();
    }

    public static UIRegistry instance() {
        return INSTANCE;
    }

    public void registerComponents() throws IOException {
        var bundle = ResourceBundle.getBundle(IgniteAppConstants.MESSAGE_BUNDLE_NAME);

        componentMap.put(PROJECT_BUTTONS_BAR, FXMLLoader.load(getClass().getResource("/fxml/project-button-bar.fxml"), bundle));
        loadWithController("/fxml/server-management.fxml", ServerManagementController.class, SERVER_MANAGEMENT, bundle);
        loadWithController("/fxml/new-server.fxml", AddServerController.class, ADD_SERVER_PANE, bundle);
        loadWithController("/fxml/app-info.fxml", AppInfoController.class, APPLICATION_INFO_UI, bundle);
        loadWithController("/fxml/app-info-linux.fxml", AppInfoLinuxController.class, APPLICATION_INFO_LINUX_UI, bundle);
        loadWithController("/fxml/app-info-windows.fxml", AppInfoWindowsController.class, APPLICATION_INFO_WINDOWS_UI, bundle);
        loadWithController("/fxml/app-info-mac.fxml", AppInfoMacController.class, APPLICATION_INFO_MAC_UI, bundle);
        loadWithController("/fxml/deployment-info-dlg.fxml", DeploymentInfoDlgController.class, DEPLOYMENT_INFO_DLG, bundle);
        loadWithController("/fxml/deployment-info.fxml", DeploymentInfoController.class, DEPLOYMENT_INFO, bundle);
        loadWithController("/fxml/jvm.fxml", JvmInfoController.class, JVM_INFO, bundle);
        loadWithController("/fxml/resources.fxml", ResourcesController.class, RESOURCES_DATA, bundle);
        var mainMenu = (MenuBar)FXMLLoader.load(getClass().getResource("/fxml/main-menu.fxml"), bundle);
        componentMap.put(MAIN_MENU, mainMenu);
        // load main window last, to make sure all children are loaded
        var mainPage = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/main.fxml"), bundle);
        componentMap.put(MAIN_PAGE, mainPage);
        // layout
        mainPage.setTop(mainMenu);
    }

    public Parent createView(String fxmlFile, ResourceBundle bundle, Object controller) throws IOException {
        var loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setResources(bundle);
        loader.setController(controller);
        return loader.load();
    }

    public Parent getComponent(String name) {
        return componentMap.get(name);
    }

    private void loadWithController(String fxmlFile, Class clazz  , String key, ResourceBundle bundle) throws IOException {
        var loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setResources(bundle);
        loader.setController(ControllerRegistry.instance().getController(clazz));

        componentMap.put(key, loader.load());
    }

    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
    }

    public Stage getMainWindow() {
        return mainWindow;
    }
}
