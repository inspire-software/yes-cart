package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Product asssociation interface. At this moment supported
 * accessories, up sell, cross sell, buy with
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AssociationDTO extends Identifiable {

    /**
     * @return pkimary key.
     */
    public long getAssociationId();

    /**
     * @param associationId primary key to set
     */
    public void setAssociationId(long associationId);

    /**
     * Identifiable human readable association code.
     *
     * @return unique human readable association code
     */
    public String getCode();

    /**
     * @param code unique human readable association code to use.
     */
    public void setCode(String code);

    /**
     * @return Association name.
     */
    public String getName();

    /**
     * @param name association name.
     */
    public void setName(String name);

    /**
     * @return description
     */
    public String getDescription();

    /**
     * @param description description to use.
     */
    public void setDescription(String description);

}
