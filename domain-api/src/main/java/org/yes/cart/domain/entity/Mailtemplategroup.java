package org.yes.cart.domain.entity;


/**
 * Mail template group.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Mailtemplategroup extends Auditable {

    /**
     */
    long getMailtemplategroupId();

    void setMailtemplategroupId(long mailtemplategroupId);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

}


