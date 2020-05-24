/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;

import java.util.List;

/**
 * User: Denis Lozenko
 * Date: 8/23/2016.
 */
@Dto
public class VoManager extends VoManagerInfo {

    private List<VoManagerShop> managerShops;
    private List<VoManagerRole> managerRoles;
    private List<VoManagerSupplierCatalog> managerSupplierCatalogs;
    private List<VoManagerCategoryCatalog> managerCategoryCatalogs;

    public List<VoManagerShop> getManagerShops() {
        return managerShops;
    }

    public void setManagerShops(List<VoManagerShop> managerShops) {
        this.managerShops = managerShops;
    }

    public List<VoManagerRole> getManagerRoles() {
        return managerRoles;
    }

    public void setManagerRoles(List<VoManagerRole> managerRoles) {
        this.managerRoles = managerRoles;
    }

    public List<VoManagerSupplierCatalog> getManagerSupplierCatalogs() {
        return managerSupplierCatalogs;
    }

    public void setManagerSupplierCatalogs(final List<VoManagerSupplierCatalog> managerSupplierCatalogs) {
        this.managerSupplierCatalogs = managerSupplierCatalogs;
    }

    public List<VoManagerCategoryCatalog> getManagerCategoryCatalogs() {
        return managerCategoryCatalogs;
    }

    public void setManagerCategoryCatalogs(final List<VoManagerCategoryCatalog> managerCategoryCatalogs) {
        this.managerCategoryCatalogs = managerCategoryCatalogs;
    }
}
