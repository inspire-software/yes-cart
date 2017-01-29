/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttributeServiceImpl extends BaseGenericServiceImpl<Attribute> implements AttributeService {


    private final GenericDAO<Attribute, Long> attributeDao;
    private final AttributeGroupService attributeGroupService;

    /**
     * Construct attribute service
     *
     * @param attributeDao            dao to use.
     * @param attributeGroupService   group service to use
     */
    public AttributeServiceImpl(final GenericDAO<Attribute, Long> attributeDao,
                                final AttributeGroupService attributeGroupService) {
        super(attributeDao);
        this.attributeDao = attributeDao;
        this.attributeGroupService = attributeGroupService;
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
                attributeGroupCode, Boolean.TRUE);
        if (attr.isEmpty()) {
            return null;
        }
        return attr;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAttributesBy(final String attributeGroupCode, final String code, final String name, final String description, final int page, final int pageSize) {

        final String codeP = StringUtils.isNotBlank(code) ? "%" + code.toLowerCase() + "%" : null;
        final String nameP = StringUtils.isNotBlank(name) ? "%" + name.toLowerCase() + "%" : null;
        final String descP = StringUtils.isNotBlank(description) ? "%" + description.toLowerCase() + "%" : null;

        return attributeDao.findRangeByNamedQuery(
                "ATTRIBUTES.BY.GROUPCODE.CODE.NAME.DESCRIPTION",
                page * pageSize, pageSize,
                attributeGroupCode, codeP, nameP, descP);
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAvailableAttributes(final String attributeGroupCode, final List<String> exclude) {
        if (exclude == null || exclude.isEmpty()) {
            return findByAttributeGroupCode(attributeGroupCode);
        } else {
            return attributeDao.findByNamedQuery(
                    "ATTRIBUTES.BY.GROUPCODE.NOT.IN.LIST",
                    attributeGroupCode,
                    exclude);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAttributesByCodes(final String attributeGroupCode, final List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        } else {
            final List<Attribute> attrs = attributeDao.findByNamedQuery(
                    "ATTRIBUTES.BY.GROUPCODE.IN.LIST",
                    attributeGroupCode,
                    codes);
            for (final Attribute attr : attrs) {
                Hibernate.initialize(attr.getEtype());
            }
            return attrs;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> getAvailableAttributesByProductTypeId(final long productTypeId) {
        return initEntities(getGenericDao().findByNamedQuery("PRODUCTS.ATTRIBUTE.BY.PROD.TYPE.ID", productTypeId));
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> getAvailableImageAttributesByGroupCode(final String attributeGroupCode) {
        return initEntities(getGenericDao().findByNamedQuery("PRODUCTS.IMAGE.ATTRIBUTE.BY.GROUP.CODE", attributeGroupCode));
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> getAvailableAttributesByGroupCodeStartsWith(final String attributeGroupCode, final String codePrefix) {
        return initEntities(getGenericDao().findByNamedQuery("PRODUCTS.ATTRIBUTE.BY.GROUP.CODE.LIKE.CODE", attributeGroupCode, codePrefix + "%"));
    }

    /*
     * Need to initialise attributes that we put into cache.
     */
    private List<Attribute> initEntities(final List<Attribute> dbList) {
        if (dbList != null) {
            for (final Attribute attr : dbList) {
                Hibernate.initialize(attr.getEtype());
                Hibernate.initialize(attr.getAttributeGroup());
            }
        }
        return dbList;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.UNIQUE");
        allowedAttributeNames.add(ProductSearchQueryBuilder.BRAND_FIELD);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_PRICE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.QUERY);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllNavigatableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.NAVIGATION.UNIQUE", Boolean.TRUE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.BRAND_FIELD);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_PRICE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.QUERY);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllSearchableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.SEARCH.UNIQUE", Boolean.TRUE, Boolean.FALSE);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllSearchablePrimaryAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.SEARCH.PRIMARY.UNIQUE", Boolean.TRUE, Boolean.TRUE);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllStorableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.STORE.UNIQUE", Boolean.TRUE);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> getSingleNavigatableAttributeCodesByProductType(final long productTypeId) {
        final List<Object[]> allowedAttributeNames = attributeDao
                .findQueryObjectsByNamedQuery("ATTRIBUTE.CODES.AND.RANK.SINGLE.NAVIGATION.UNIQUE.BY.PRODUCTTYPE.ID",
                        productTypeId, Boolean.TRUE, Boolean.TRUE);
        if (allowedAttributeNames.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Integer> map = new HashMap<String, Integer>();
        for (final Object[] codeAndRank : allowedAttributeNames) {
            map.put((String) codeAndRank[0], (Integer) codeAndRank[1]);
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public I18NModel getNavigatableAttributeDisplayValue(final String attrCode, final String value) {

        final List<Object> productSkuDV = attributeDao.findQueryObjectRangeByNamedQuery(
                "PRODUCTSKU.ATTRIBUTE.DISPLAYVALUES.BY.VALUE", 0, 1, attrCode, value);

        if (productSkuDV == null || productSkuDV.isEmpty()) {
            // No SKU value, try product
            final List<Object> productDV = attributeDao.findQueryObjectRangeByNamedQuery(
                    "PRODUCT.ATTRIBUTE.DISPLAYVALUES.BY.VALUE", 0, 1, attrCode, value);

            if (productDV == null || productDV.isEmpty()) {
                // no values at all - fail safe to raw
                return new NonI18NModel(value);
            }

            // product attribute value
            return new FailoverStringI18NModel((String) productDV.get(0), value);

        }
        // SKU attribute value
        return new FailoverStringI18NModel((String) productSkuDV.get(0), value);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, I18NModel> getAllAttributeNames() {
        Map<String, I18NModel> result = new HashMap<String, I18NModel>();
        List<Object[]> codeNameList = attributeDao.findQueryObjectsByNamedQuery(
                "ATTRIBUTE.CODE.NAMES.ALL");
        if (codeNameList != null) {
            for (Object[] tuple : codeNameList) {
                result.put(
                        (String) tuple[0],
                        new FailoverStringI18NModel((String) tuple[2], (String) tuple[1])
                );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, I18NModel> getAttributeNamesByCodes(final Set<String> codes) {
        Map<String, I18NModel> result = new HashMap<String, I18NModel>();
        List<Object[]> codeNameList = attributeDao.findQueryObjectsByNamedQuery(
                "ATTRIBUTE.CODE.NAMES",
                codes);
        if (codeNameList != null) {
            for (Object[] tuple : codeNameList) {
                result.put(
                        (String) tuple[0],
                        new FailoverStringI18NModel((String) tuple[2], (String) tuple[1])
                );
            }
        }
        return result;
    }

}
