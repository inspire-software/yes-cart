package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: Denis Lozenko
 * Date 8/22/2016.
 */
@Dto
public class VoRole {

    @DtoField(value = "roleId", readOnly = true)
    private long roleId;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "description", readOnly = true)
    private String description;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
