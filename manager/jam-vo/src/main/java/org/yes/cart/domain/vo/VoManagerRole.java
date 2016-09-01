package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: Denis Lozenko
 * Date: 8/23/2016.
 */
@Dto
public class VoManagerRole {

    @DtoField(value = "manager.managerId", readOnly = true)
    private long managerId;
    @DtoField(value = "role.roleId", readOnly = true)
    private long roleId;

    public long getManagerId() {
        return managerId;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
