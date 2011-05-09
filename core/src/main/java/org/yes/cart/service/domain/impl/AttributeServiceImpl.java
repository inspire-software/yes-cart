package org.yes.cart.service.domain.impl;

import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttributeServiceImpl extends BaseGenericServiceImpl<Attribute> implements AttributeService {



    private final GenericDAO<Attribute, Long> attributeDao;

    /**
     * Construct attribute service
     * @param attributeDao dao to use.
     */
    public AttributeServiceImpl(final GenericDAO<Attribute, Long> attributeDao) {
        super(attributeDao);
        this.attributeDao = attributeDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findByAttributeGroupCode(final String attributeGroupCode) {
        return attributeDao.findByNamedQuery("ATTRIBUTES.BY.GROUPCODE", attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    public Attribute findByAttributeCode(final String attributeCode) {
        return attributeDao.findSingleByNamedQuery("ATTRIBUTE.BY.CODE", attributeCode);
    }


    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAttributesWithMultipleValues(final String attributeGroupCode) {
        List<Attribute> attr = attributeDao.findByNamedQuery(
                "ATTRIBUTES.WITH.MULTIPLE.VALUES.BY.GROUPCODE",
                attributeGroupCode);
        if (attr.isEmpty()) {
            return null;
        }
        return attr;
    }


    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAvailableAttributes(
            final String attributeGroupCode,
            final List<String> assignedAttributeCodes) {
        if (assignedAttributeCodes == null || assignedAttributeCodes.isEmpty()) {
            return findByAttributeGroupCode(attributeGroupCode);
        } else {
            return attributeDao.findQueryObjectsByNamedQueryWithList(
                    "ATTRIBUTES.BY.GROUPCODE.NOT.IN.LIST",
                    (List) assignedAttributeCodes,
                    attributeGroupCode);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeServiceImplMethodCache")
    public List<Object> getAllAttributeCodes() {
        List<Object> allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.UNIQUE");
        allowedAttributeNames.add(ProductSearchQueryBuilder.BRAND_FIELD);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_PRICE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.QUERY);
        return allowedAttributeNames;
    }

    @Cacheable(value = "attributeServiceImplMethodCache")
    public Map<String, String> getAttributeNamesByCodes(List<Object> codes) {
        Map<String, String> result = new HashMap<String, String>();
        List<Object[]> codeNameList = attributeDao.findQueryObjectsByNamedQueryWithList("ATTRIBUTE.CODE.NAMES",
                codes);
        if (codeNameList != null) {
            for (Object[] tuple : codeNameList) {
                result.put(
                        (String) tuple[0],
                        (String) tuple[1]
                );
            }
        }
        return result;
    }

}
