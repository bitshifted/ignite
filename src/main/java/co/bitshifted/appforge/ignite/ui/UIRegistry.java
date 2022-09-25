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

import co.bitshifted.appforge.ignite.ctrl.ControllerRegistry;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class UIRegistry {

    private static final UIRegistry INSTANCE;
    private static final String MSG_BUNDLE_NAME = "i18n/strings";

    // Component names
    public static final String MAIN_PAGE = "main_page";

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

    public void loadComponents() throws IOException {
        var bundle = ResourceBundle.getBundle(MSG_BUNDLE_NAME);

        // load main window last, to make sure all children are loaded
        componentMap.put(MAIN_PAGE, FXMLLoader.load(getClass().getResource("/fxml/main.fxml")));
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
