/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ctrl;

import java.util.HashMap;
import java.util.Map;

public class ControllerRegistry {

    private static final ControllerRegistry INSTANCE;

    static {
        INSTANCE = new ControllerRegistry();
    }

    private final Map<String, Object> controllerMap;

    private ControllerRegistry() {
        this.controllerMap = new HashMap<>();
    }

    public static ControllerRegistry instance() {
        return INSTANCE;
    }

    public void registerControllers() {
        controllerMap.put(DeploymentInfoDlgController.class.getName(), new DeploymentInfoDlgController());
        controllerMap.put(DeploymentInfoController.class.getName(), new DeploymentInfoController());
        controllerMap.put(MainMenuController.class.getName(), new MainMenuController());
        controllerMap.put(ServerManagementController.class.getName(), new ServerManagementController());
        controllerMap.put(AddServerController.class.getName(), new AddServerController());
        controllerMap.put(AppInfoController.class.getName(), new AppInfoController());
        controllerMap.put(AppInfoLinuxController.class.getName(), new AppInfoLinuxController());
    }

    public  <T> T getController(Class<T> clazz) {
        var ctrl = controllerMap.get(clazz.getName());
        return clazz.cast(ctrl);
    }
}
