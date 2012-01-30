package org.yes.cart.domain.entity;

import java.util.Set;


/**
 * Etype.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Etype extends Auditable {

    // The OOTB business types
    String IMAGE_BUSINESS_TYPE = "Image";

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

    /**
     * Get attributes, that has this type
     *
     * @return attributes.
     */
   // Set<Attribute> getAttributes();

    /**
     * Set attributes.
     *
     * @param attributes attribute collection
     */
    //void setAttributes(Set<Attribute> attributes);

}


