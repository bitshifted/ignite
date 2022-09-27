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

import co.bitshifted.appforge.ignite.IgniteConstants;
import co.bitshifted.appforge.ignite.ctrl.ControllerRegistry;
import co.bitshifted.appforge.ignite.ctrl.DeploymentInfoController;
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
    public static final String DEPLOYMENT_INFO = "deployment-info";

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
        var bundle = ResourceBundle.getBundle(IgniteConstants.MESSAGE_BUNDLE_NAME);

        loadWithController("/fxml/deployment-info.fxml", DeploymentInfoController.class, DEPLOYMENT_INFO, bundle);
        var mainMenu = (MenuBar)FXMLLoader.load(getClass().getResource("/fxml/main-menu.fxml"), bundle);
        componentMap.put(MAIN_MENU, mainMenu);
        // load main window last, to make sure all children are loaded
        var mainPage = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/main.fxml"), bundle);
        componentMap.put(MAIN_PAGE, mainPage);
        // layout
        mainPage.setTop(mainMenu);
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
