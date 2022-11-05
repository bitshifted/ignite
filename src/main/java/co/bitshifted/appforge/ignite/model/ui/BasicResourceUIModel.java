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

import co.bitshifted.appforge.common.model.BasicResource;
import javafx.beans.property.SimpleStringProperty;

public class BasicResourceUIModel {

    private final BasicResource resource;

    private final SimpleStringProperty sourceProperty;
    private final SimpleStringProperty targetProperty;

    public BasicResourceUIModel(BasicResource resource) {
        if (resource != null) {
            this.resource = resource;
        } else {
            this.resource = new BasicResource();
        }
        this.sourceProperty = new SimpleStringProperty(this.resource.getSource());
        this.targetProperty = new SimpleStringProperty(this.resource.getTarget());

    }

    public SimpleStringProperty getSourceProperty() {
        return sourceProperty;
    }

    public SimpleStringProperty getTargetProperty() {
        return targetProperty;
    }

    public BasicResource getResource() {
        // if no source, assume that property is empty
        var src = sourceProperty.get();
        if(src == null || src.isEmpty() || src.isBlank()) {
            return null;
        }
        resource.setSource(sourceProperty.get());
        resource.setTarget(targetProperty.get());
        return  resource;
    }
}
