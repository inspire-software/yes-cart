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
import org.yes.cart.domain.dto.EtypeDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class EtypeDTOImpl implements EtypeDTO {

    private static final long serialVersionUID = 20100425L;

    @DtoField(value = "etypeId", readOnly = true)
    private long etypeId;

    @DtoField(value = "javatype")
    private String javatype;

    @DtoField(value = "businesstype")
    private String businesstype;

    /** {@inheritDoc} */
    public long getEtypeId() {
        return etypeId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return etypeId;
    }

    /** {@inheritDoc} */
    public void setEtypeId(final long etypeId) {
        this.etypeId = etypeId;
    }

    /** {@inheritDoc}*/
    public String getJavatype() {
        return javatype;
    }

    /** {@inheritDoc} */
    public void setJavatype(final String javatype) {
        this.javatype = javatype;
    }

    /** {@inheritDoc}*/
    public String getBusinesstype() {
        return businesstype;
    }

    /** {@inheritDoc} */
    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (otherObj == null || getClass() != otherObj.getClass()) {
            return false;
        }

        final EtypeDTOImpl etypeDTO = (EtypeDTOImpl) otherObj;

        if (etypeId != etypeDTO.etypeId) {
            return false;
        }
        if (!businesstype.equals(etypeDTO.businesstype)) {
            return false;
        }
        if (!javatype.equals(etypeDTO.javatype)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (etypeId ^ (etypeId >>> 32));
        result = 31 * result + javatype.hashCode();
        result = 31 * result + businesstype.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EtypeDTOImpl{" +
                "etypeId=" + etypeId +
                ", javatype='" + javatype + '\'' +
                ", businesstype='" + businesstype + '\'' +
                '}';
    }
}
