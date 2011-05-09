package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.service.domain.AttributeGroupService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttributeGroupServiceImpl extends BaseGenericServiceImpl<AttributeGroup> implements AttributeGroupService {

    private final GenericDAO<AttributeGroup, Long> attributeGroupDao;

    /**
     * Construct attribute group service.
     * @param attributeGroupDao
     */
    public AttributeGroupServiceImpl(final GenericDAO<AttributeGroup, Long> attributeGroupDao) {
        super(attributeGroupDao);
        this.attributeGroupDao = attributeGroupDao;
    }


    /**
     * Get single attribute by given code.
     *
     * @param code given code
     * @return {@link AttributeGroup} if found, otherwise null.
     */
    public AttributeGroup getAttributeGroupByCode(final String code) {
        return attributeGroupDao.findSingleByCriteria(Restrictions.eq("code", code));
    }



    /**
     * Delete  {@link AttributeGroup} by given code.
     * @param code code of {@link AttributeGroup} to delete
     */
    public void delete(final String code) {
        attributeGroupDao.delete(
                getAttributeGroupByCode(code)
        );
    }

}
