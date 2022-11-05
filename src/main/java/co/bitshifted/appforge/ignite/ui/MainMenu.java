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

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MainMenu {

    private final MenuBar mainMenu;
    private final Menu fileMenu;
    private final MenuItem newProjectMenuItem;

    public MainMenu() {
        mainMenu = new MenuBar();

        fileMenu = new Menu("File");
        fileMenu.setMnemonicParsing(true);
        newProjectMenuItem = new MenuItem("New Project...");

    }

    public MenuBar getMainMenu() {
        return mainMenu;
    }
}
