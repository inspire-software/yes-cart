package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.EtypeDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class EtypeDTOImpl implements EtypeDTO {

    private static final long serialVersionUID = 20100425L;

    @DtoField(value = "etypeId")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtypeDTOImpl etypeDTO = (EtypeDTOImpl) o;

        if (etypeId != etypeDTO.etypeId) return false;
        if (!businesstype.equals(etypeDTO.businesstype)) return false;
        if (!javatype.equals(etypeDTO.javatype)) return false;

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
