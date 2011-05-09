package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.RoleDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class RoleDTOImpl implements RoleDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "description", readOnly = true)
    private String description;

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String descrition) {
        this.description = descrition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleDTOImpl roleDTO = (RoleDTOImpl) o;

        if (!code.equals(roleDTO.code)) return false;
        if (!description.equals(roleDTO.description)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RoleDTOImpl{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
