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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ResourceBundle;


public class DialogBuilder<T> {

    private Dialog<T> dialog;

    private DialogBuilder() {
        dialog = new Dialog<>();
    }

    public static <R> DialogBuilder<R> newBuilder(Class<R> clazz) {
        return new DialogBuilder<R>();
    }

    public DialogBuilder<T> withTitle(String title) {
        dialog.setTitle(title);
        return  this;
    }

    public DialogBuilder<T> withButtonTypes(ButtonType... buttonTypes) {
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);
        return this;
    }

    public DialogBuilder<T> withContent(Node content) {
        dialog.getDialogPane().setContent(content);
        return this;
    }

    public DialogBuilder<T> withResultConverter(Callback<ButtonType, T> converter) {
        dialog.setResultConverter(converter);
        return this;
    }

    public DialogBuilder<T> withFxmlContent(String fxmlFile, ResourceBundle bundle, Object controller) throws IOException {
        var loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setResources(bundle);
        loader.setController(controller);
        dialog.getDialogPane().setContent(loader.load());
        return this;
    }

    public Dialog<T> build() {
        return dialog;
    }

}
