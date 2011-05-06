
package org.yes.cart.domain.entity;


/**
 *
 * TODO kill 
 * Settings.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Settings extends Auditable {

    /**
     */
    public long getSettingsId();

    public void setSettingsId(long settingsId);

    /**
     */
    public Etype getEtype();

    public void setEtype(Etype etype);

    /**
     */
    public String getCode();

    public void setCode(String code);

    /**
     */
    public String getName();

    public void setName(String name);

    /**
     */
    public String getDescription();

    public void setDescription(String description);

    /**
     */
    public String getVal();

    public void setVal(String val);

    /**
     */
    public long getTimeout();

    public void setTimeout(long timeout);


}


