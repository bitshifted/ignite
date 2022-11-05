/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.model.ui;

import co.bitshifted.appforge.ignite.model.RuntimeData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

import java.util.Objects;

public class DirtyChangeListener<T> implements  ChangeListener<T>, ListChangeListener<T> {


    @Override
    public void changed(ObservableValue<? extends T> observableValue, T oldValue, T newValue) {
        if(!Objects.equals(oldValue, newValue)) {
            RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().dirtyProperty().set(true);
        }
    }

    @Override
    public void onChanged(Change<? extends T> change) {
        RuntimeData.getInstance().selectedDeploymentTreeITemProperty().get().deployment().getConfiguration().dirtyProperty().set(true);
    }
}
