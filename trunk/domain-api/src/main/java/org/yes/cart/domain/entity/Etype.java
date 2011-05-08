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
    public static final String IMAGE_BUSINESS_TYPE = "Image";

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

    /**
     * Get attributes, that has this type
     *
     * @return attributes.
     */
    public Set<Attribute> getAttributes();

    /**
     * Set attributes.
     *
     * @param attributes attribute collection
     */
    public void setAttributes(Set<Attribute> attributes);

}


