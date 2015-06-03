/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.RoleDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class RoleDTOImpl implements RoleDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "roleId", readOnly = true)
    private long roleId;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "description", readOnly = true)
    private String description;

    /**
     * {@inheritDoc}
     */
    public long getRoleId() {
        return roleId;
    }


    /**
     * {@inheritDoc}
     */
    public long getId() {
        return roleId;
    }

    /**
     * {@inheritDoc}
     */
    public void setRoleId(final long roleId) {
        this.roleId = roleId;
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(final String code) {
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
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (otherObj == null || getClass() != otherObj.getClass()) {
            return false;
        }

        final RoleDTOImpl roleDTO = (RoleDTOImpl) otherObj;

        if (!code.equals(roleDTO.code)) {
            return false;
        }
        if (!description.equals(roleDTO.description)) {
            return false;
        }

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
