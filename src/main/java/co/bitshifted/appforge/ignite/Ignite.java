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

import co.bitshifted.appforge.ignite.ctrl.ControllerRegistry;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import co.bitshifted.appforge.ignite.persist.UserDataPersister;
import co.bitshifted.appforge.ignite.ui.UIRegistry;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Ignite extends Application {

    private static final double DEFAULT_WIDTH = 900.0;
    private static final double DEFAULT_HEIGHT = 700.0;

    @Override
    public void init() throws Exception {
        super.init();
        // initialize application
        var userData = UserDataPersister.instance().load();
        RuntimeData.getInstance().setUserData(userData);
        ControllerRegistry.instance().registerControllers();
        UIRegistry.instance().registerComponents();
    }

    @Override
    public void start(Stage stage) throws Exception {
        UIRegistry.instance().setMainWindow(stage);
        var scene = new Scene(UIRegistry.instance().getComponent(UIRegistry.MAIN_PAGE), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        stage.setTitle("Ignite");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(Ignite.class, args);
    }
}
