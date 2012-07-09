package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.AttributeGroup;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttributeGroupService extends GenericService<AttributeGroup> {

    /**
     * Get single attribute by given code.
     *
     * @param code given code
     * @return {@link AttributeGroup} if found, otherwise null.
     */
    AttributeGroup getAttributeGroupByCode(String code);


    /**
     * Delete  {@link AttributeGroup} by given code.
     *
     * @param code code of {@link AttributeGroup} to delete
     */
    void delete(String code);

}
