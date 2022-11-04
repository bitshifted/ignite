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
import co.bitshifted.appforge.ignite.model.IgniteConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourcesUiModel {

    private final List<BasicResource> source;
    private final ObservableList<BasicResourceUIModel> resourceList;

    public ResourcesUiModel(List<BasicResource> source) {
        if(source != null) {
            this.source = source;
            this.resourceList = FXCollections.observableList(
                source.stream().map(i -> new BasicResourceUIModel(i)).collect(Collectors.toList()));
        } else {
            this.source = new ArrayList<>();
            this.resourceList = FXCollections.observableArrayList();
        }

    }

    public List<BasicResource> getSource() {
        source.clear();
        source.addAll(resourceList.stream().map(ui -> ui.getResource()).collect(Collectors.toList()));
        return source;
    }

    public ObservableList<BasicResourceUIModel> getResourceList() {
        return resourceList;
    }
}
