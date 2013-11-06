/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
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
    @Cacheable(value = "attributeService-availableAttributesByProductTypeId")
    public List<Attribute> getAvailableAttributesByProductTypeId(final long productTypeId) {
        return getGenericDao().findByNamedQuery("PRODUCTS.ATTRIBUTE.BY.PROD.TYPE.ID", productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-availableImageAttributesByGroupCode")
    public List<Attribute> getAvailableImageAttributesByGroupCode(final String attributeGroupCode) {
        return getGenericDao().findByNamedQuery("PRODUCTS.IMAGE.ATTRIBUTE.BY.GROUP.CODE", attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allAttributeCodes")
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
    @Cacheable(value = "attributeService-allNavigatableAttributeCodes")
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
    @Cacheable(value = "attributeService-singleNavigatableAttributeCodesByProductType")
    public Map<String, Integer> getSingleNavigatableAttributeCodesByProductType(final long productTypeId) {
        final List<Object[]> allowedAttributeNames = attributeDao
                .findQueryObjectsByNamedQuery("ATTRIBUTE.CODES.AND.RANK.SINGLE.NAVIGATION.UNIQUE.BY.PRODUCTTYPE.ID",
                        productTypeId, Boolean.TRUE);
        if (allowedAttributeNames.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Integer> map = new HashMap<String, Integer>();
        for (final Object[] codeAndRank : allowedAttributeNames) {
            map.put((String) codeAndRank[0], (Integer) codeAndRank[1]);
        }
        return map;
    }

    @Cacheable(value = "attributeService-allAttributeNames")
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

    @Cacheable(value = "attributeService-attributeNamesByCodes")
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

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
             allEntries = true)
    public Attribute create(Attribute instance) {
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
            allEntries = true)
    public Attribute update(Attribute instance) {
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
            allEntries = true)
    public void delete(Attribute instance) {
        super.delete(instance);
    }
}
