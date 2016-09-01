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
}
