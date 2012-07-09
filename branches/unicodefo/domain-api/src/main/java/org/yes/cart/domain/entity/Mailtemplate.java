package org.yes.cart.domain.entity;

/**
 * Mail template.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Mailtemplate extends Auditable {

    /**
     */
    long getMailtemplateId();

    void setMailtemplateId(long mailtemplateId);

    /**
     */
    String getCode();

    void setCode(String code);

    /**
     */
    String getFspointer();

    void setFspointer(String fspointer);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

    /**
     */
    Mailtemplategroup getMailTemplateGroup();

    void setMailTemplateGroup(Mailtemplategroup mailTemplateGroup);

}


