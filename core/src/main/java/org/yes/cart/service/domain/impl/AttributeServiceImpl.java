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

import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeGroupService;
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
    private final AttributeGroupService attributeGroupService;

    private final boolean allowProductToSkuMirror;

    /**
     * Construct attribute service
     *
     * @param attributeDao            dao to use.
     * @param allowProductToSkuMirror in case if ths flag is true all product attributes will be also created as sku attributes. CPOINT
     * @param attributeGroupService   group service to use
     */
    public AttributeServiceImpl(final GenericDAO<Attribute, Long> attributeDao,
                                final AttributeGroupService attributeGroupService,
                                final boolean allowProductToSkuMirror) {
        super(attributeDao);
        this.attributeDao = attributeDao;
        this.allowProductToSkuMirror = allowProductToSkuMirror;
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
    @Cacheable(value = "attributeServiceImplMethodCache")
    public List<Attribute> findAvailableAttributesByProductTypeId(final long productTypeId) {
        return getGenericDao().findByNamedQuery("PRODUCTS.ATTRIBUTE.BY.PROD.TYPE.ID", productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeServiceImplMethodCache")
    public List<Attribute> findAvailableImageAttributesByGroupCode(final String attributeGroupCode) {
        return getGenericDao().findByNamedQuery("PRODUCTS.IMAGE.ATTRIBUTE.BY.GROUP.CODE", attributeGroupCode);
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
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);
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

}
