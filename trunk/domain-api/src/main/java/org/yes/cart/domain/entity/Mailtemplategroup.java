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
    public long getMailtemplategroupId();

    public void setMailtemplategroupId(long mailtemplategroupId);

    /**
     */
    public String getName();

    public void setName(String name);

    /**
     */
    public String getDescription();

    public void setDescription(String description);

}


