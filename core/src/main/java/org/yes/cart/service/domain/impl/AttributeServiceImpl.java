package org.yes.cart.service.domain.impl;

import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.misc.Pair;
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
    public List<String> getAllAttributeCodes() {
        List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.UNIQUE");
        allowedAttributeNames.add(ProductSearchQueryBuilder.BRAND_FIELD);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_PRICE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.QUERY);
        return allowedAttributeNames;
    }

    @Cacheable(value = "attributeServiceImplMethodCache")
    public Map<String, String> getAttributeNamesByCodes(final List<String> codes) {
        Map<String, String> result = new HashMap<String, String>();
        List<Object[]> codeNameList = attributeDao.findQueryObjectsByNamedQueryWithList(
                "ATTRIBUTE.CODE.NAMES",
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

    /** {@inheritDoc} */
    public List<Pair<String, List<AttrValue>>> merge(
            final List<Pair<String, List<AttrValue>>> to,
            final List<Pair<String, List<AttrValue>>> from) {

        for (Pair<String, List<AttrValue>> pair : to) { //overwrite sections
            final String sectionName = pair.getFirst();
            final List<AttrValue> valuesToMegreFrom = removeAttrValues(from, sectionName);
            if (valuesToMegreFrom != null) {
                for (AttrValue attrValue : pair.getSecond()) { //overwrite valuesToMegreFrom in current section
                    final AttrValue value = removeAttrValue(valuesToMegreFrom, attrValue.getAttribute().getName());
                    if (value != null) {
                        attrValue.setVal(value.getVal());
                    }
                }
                pair.getSecond().addAll(valuesToMegreFrom); //merge the rest, because they are not present in "to" list
            }
        }
        //merge sections
        to.addAll(from);

        return to;

    }


    /**
     * Remove attr value from given list by given name
     * @param values list to remove from
     * @param attrName name to remove
     * @return removed {@link AttrValue} if found, otherwise null
     */
    public AttrValue removeAttrValue(List<AttrValue> values, final String attrName) {
        for (AttrValue attrValue : values) {
            if (attrValue.getAttribute().getName().equals(attrName)) {
                values.remove(attrValue);
                return attrValue;
            }
        }
        return null;
    }

    /**
     * Remove section from given lisy by name.
     * @param fromList list to remove section
     * @param sectionName section name to remove.
     * @return removed section if found, otherwise null
     */
    public List<AttrValue> removeAttrValues(final List<Pair<String, List<AttrValue>>> fromList, final String sectionName) {
        for (Pair<String, List<AttrValue>> pair : fromList) {
            if (sectionName.equals(pair.getFirst())) {
                fromList.remove(pair);
                return pair.getSecond();
            }
        }
        return null;
    }

}
