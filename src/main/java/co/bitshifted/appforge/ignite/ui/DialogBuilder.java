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

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;


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

    public Dialog<T> build() {
        return dialog;
    }

}
