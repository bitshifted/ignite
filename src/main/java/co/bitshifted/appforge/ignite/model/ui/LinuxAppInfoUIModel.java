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

import co.bitshifted.appforge.common.model.CpuArch;
import co.bitshifted.appforge.common.model.LinuxApplicationInfo;
import co.bitshifted.appforge.common.model.LinuxPackageType;
import co.bitshifted.appforge.common.model.OperatingSystem;
import co.bitshifted.appforge.ignite.model.RuntimeData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class LinuxAppInfoUIModel {

    private final LinuxApplicationInfo source;
    private final ObservableList<BasicResourceUIModel> iconsUiModel;
    private final ObservableList<LinuxDesktopCategory> categoriesUiModel;
    private final SimpleBooleanProperty archX86SupportedProperty;
    private final SimpleBooleanProperty archArmSupportedProperty;
    private final SimpleBooleanProperty debPackageProperty;
    private final SimpleBooleanProperty rpmPackageProperty;
    private final SimpleBooleanProperty tarGzPackagePropety;

    public LinuxAppInfoUIModel(LinuxApplicationInfo source) {
        if(source == null) {
            this.source = new LinuxApplicationInfo();
            this.iconsUiModel = FXCollections.observableArrayList();
            this.categoriesUiModel = FXCollections.observableArrayList();
        } else {
            this.source = source;
            var icons = source.getIcons();
            if(icons == null) {
                icons = List.of();
            }
            this.iconsUiModel = FXCollections.observableList(
                    icons.stream().map(i -> new BasicResourceUIModel(i)).collect(Collectors.toList()));

            this.categoriesUiModel = FXCollections.observableList(createCategoryList(source.getCategories()));
        }
        this.archX86SupportedProperty = new SimpleBooleanProperty( this.source.getSupportedCpuArchitectures().contains(CpuArch.X64));
        this.archArmSupportedProperty = new SimpleBooleanProperty(this.source.getSupportedCpuArchitectures().contains(CpuArch.AARCH64));
        this.debPackageProperty = new SimpleBooleanProperty(this.source.getPackageTypes().contains(LinuxPackageType.DEB));
        this.rpmPackageProperty = new SimpleBooleanProperty(this.source.getPackageTypes().contains(LinuxPackageType.RPM));
        this.tarGzPackagePropety = new SimpleBooleanProperty(this.source.getPackageTypes().contains(LinuxPackageType.TAR_GZ));
        this.iconsUiModel.addListener(new DirtyChangeListener<>());
        categoriesUiModel.addListener(new DirtyChangeListener<>());
    }

    public ObservableList<BasicResourceUIModel> getIconsUiModel() {
        return iconsUiModel;
    }

    public ObservableList<LinuxDesktopCategory> getCategoriesUiModel() {
        return categoriesUiModel;
    }

    public SimpleBooleanProperty getArchX86SupportedProperty() {
        return archX86SupportedProperty;
    }

    public SimpleBooleanProperty getArchArmSupportedProperty() {return archArmSupportedProperty; }

    public SimpleBooleanProperty getDebPackageProperty() {return  debPackageProperty; }

    public SimpleBooleanProperty getRpmPackageProperty() { return rpmPackageProperty; }

    public SimpleBooleanProperty getTarGzPackagePropety() { return tarGzPackagePropety; }

    public LinuxApplicationInfo getSource() {
        source.setIcons(iconsUiModel.stream().map(ui -> ui.getResource()).collect(Collectors.toList()));
        source.setCategories(categoriesUiModel.stream().map(cat -> cat.name()).collect(Collectors.toList()));
        source.setSupportedCpuArchitectures(getSupportedCpuArchitectures());
        source.setPackageTypes(getPackageTypes());
        return source;
    }


    private List<LinuxDesktopCategory> createCategoryList(List<String> names) {
        var out = new ArrayList<LinuxDesktopCategory>();
        var defined = RuntimeData.getInstance().getLinuxDesktopCategories().stream().map(LinuxDesktopCategory::flatData).flatMap(Collection::stream).collect(Collectors.toList());
        names.stream().forEach(catName -> {
            defined.stream().filter(dc -> dc.name().equals(catName)).findFirst().ifPresent(c -> {out.add(c);});
        });
        return  out;
    }

    private Set<CpuArch> getSupportedCpuArchitectures() {
        var set = new HashSet<CpuArch>();
        if(archX86SupportedProperty.get()) {
            set.add(CpuArch.X64);
        }
        if(archArmSupportedProperty.get()) {
            set.add(CpuArch.AARCH64);
        }
        return set;
    }

    private Set<LinuxPackageType> getPackageTypes() {
        var set = new HashSet<LinuxPackageType>();
        if(debPackageProperty.get()) {
            set.add(LinuxPackageType.DEB);
        }
        if(rpmPackageProperty.get()) {
            set.add(LinuxPackageType.RPM);
        }
        if(tarGzPackagePropety.get()) {
            set.add(LinuxPackageType.TAR_GZ);
        }
        return  set;
    }
}
