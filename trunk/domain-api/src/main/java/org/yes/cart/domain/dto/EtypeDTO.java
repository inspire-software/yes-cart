package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * Type DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface EtypeDTO extends Serializable {

    /**
     * Get primary key.
     *
     * @return pk
     */
    public long getEtypeId();

    /**
     * Set pk value
     *
     * @param etypeId pk value.
     */
    public void setEtypeId(long etypeId);

    /**
     * Get the full java class name.
     *
     * @return class name.
     */
    public String getJavatype();

    /**
     * Set java type.
     *
     * @param javatype java type
     */
    public void setJavatype(String javatype);

    /**
     * High level business type
     *
     * @return business type
     */
    public String getBusinesstype();

    /**
     * Set business type.
     *
     * @param businesstype business type.
     */
    public void setBusinesstype(String businesstype);


}
