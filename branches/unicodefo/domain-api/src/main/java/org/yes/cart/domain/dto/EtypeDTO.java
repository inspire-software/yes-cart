package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Type DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface EtypeDTO extends Identifiable {

    /**
     * Get primary key.
     *
     * @return pk
     */
    long getEtypeId();

    /**
     * Set pk value
     *
     * @param etypeId pk value.
     */
    void setEtypeId(long etypeId);

    /**
     * Get the full java class name.
     *
     * @return class name.
     */
    String getJavatype();

    /**
     * Set java type.
     *
     * @param javatype java type
     */
    void setJavatype(String javatype);

    /**
     * High level business type
     *
     * @return business type
     */
    String getBusinesstype();

    /**
     * Set business type.
     *
     * @param businesstype business type.
     */
    void setBusinesstype(String businesstype);


}
