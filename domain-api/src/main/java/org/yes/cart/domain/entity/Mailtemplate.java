
package org.yes.cart.domain.entity;

/**
 *
 * Mail template.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Mailtemplate extends Auditable {

    /**
     */
    public long getMailtemplateId();

    public void setMailtemplateId(long mailtemplateId);

    /**
     */
    public String getCode();

    public void setCode(String code);

    /**
     */
    public String getFspointer();

    public void setFspointer(String fspointer);

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
    public Mailtemplategroup getMailTemplateGroup();

    public void setMailTemplateGroup(Mailtemplategroup mailTemplateGroup);

}


