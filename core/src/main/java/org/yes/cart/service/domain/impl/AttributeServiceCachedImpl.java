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

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.service.domain.AttributeService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 15:27
 */
public class AttributeServiceCachedImpl implements AttributeService {

    private final AttributeService attributeService;

    public AttributeServiceCachedImpl(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findByAttributeGroupCode(final String attributeGroupCode) {
        return attributeService.findByAttributeGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    public Attribute findByAttributeCode(final String attributeCode) {
        return attributeService.findByAttributeCode(attributeCode);
    }


    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAttributesWithMultipleValues(final String attributeGroupCode) {
        return attributeService.findAttributesWithMultipleValues(attributeGroupCode);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAttributesBy(final String attributeGroupCode, final String code, final String name, final String description, final int page, final int pageSize) {
        return attributeService.findAttributesBy(attributeGroupCode, code, name, description, page, pageSize);
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAvailableAttributes(final String attributeGroupCode, final List<String> exclude) {
        return attributeService.findAvailableAttributes(attributeGroupCode, exclude);
    }

    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAttributesByCodes(final String attributeGroupCode, final List<String> codes) {
        return attributeService.findAttributesByCodes(attributeGroupCode, codes);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-availableAttributesByProductTypeId")
    public List<Attribute> getAvailableAttributesByProductTypeId(final long productTypeId) {
        return attributeService.getAvailableAttributesByProductTypeId(productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-availableImageAttributesByGroupCode")
    public List<Attribute> getAvailableImageAttributesByGroupCode(final String attributeGroupCode) {
        return attributeService.getAvailableImageAttributesByGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-availableAttributesByGroupCodeStartsWith")
    public List<Attribute> getAvailableAttributesByGroupCodeStartsWith(final String attributeGroupCode, final String codePrefix) {
        return attributeService.getAvailableAttributesByGroupCodeStartsWith(attributeGroupCode, codePrefix);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allAttributeCodes")
    public Set<String> getAllAttributeCodes() {
        return attributeService.getAllAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allNavigatableAttributeCodes")
    public Set<String> getAllNavigatableAttributeCodes() {
        return attributeService.getAllNavigatableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allSearchableAttributeCodes")
    public Set<String> getAllSearchableAttributeCodes() {
        return attributeService.getAllSearchableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allSearchablePrimaryAttributeCodes")
    public Set<String> getAllSearchablePrimaryAttributeCodes() {
        return attributeService.getAllSearchablePrimaryAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allStorableAttributeCodes")
    public Set<String> getAllStorableAttributeCodes() {
        return attributeService.getAllStorableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-singleNavigatableAttributeCodesByProductType")
    public Map<String, Integer> getSingleNavigatableAttributeCodesByProductType(final long productTypeId) {
        return attributeService.getSingleNavigatableAttributeCodesByProductType(productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-navigatableAttributeDisplayValue")
    public I18NModel getNavigatableAttributeDisplayValue(final String attrCode, final String value) {
        return attributeService.getNavigatableAttributeDisplayValue(attrCode, value);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allAttributeNames")
    public Map<String, I18NModel> getAllAttributeNames() {
        return attributeService.getAllAttributeNames();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-attributeNamesByCodes")
    public Map<String, I18NModel> getAttributeNamesByCodes(final Set<String> codes) {
        return attributeService.getAttributeNamesByCodes(codes);
    }


    /**
     * {@inheritDoc}
     */
    public List<Attribute> findAll() {
        return attributeService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Attribute findById(final long pk) {
        return attributeService.findById(pk);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-allSearchableAttributeCodes",
            "attributeService-allSearchablePrimaryAttributeCodes",
            "attributeService-allStorableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-navigatableAttributeDisplayValue",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
            allEntries = true)
    public Attribute create(final Attribute instance) {
        return attributeService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-allSearchableAttributeCodes",
            "attributeService-allSearchablePrimaryAttributeCodes",
            "attributeService-allStorableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-navigatableAttributeDisplayValue",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
            allEntries = true)
    public Attribute update(final Attribute instance) {
        return attributeService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-allAttributeCodes",
            "attributeService-allNavigatableAttributeCodes",
            "attributeService-allSearchableAttributeCodes",
            "attributeService-allSearchablePrimaryAttributeCodes",
            "attributeService-allStorableAttributeCodes",
            "attributeService-singleNavigatableAttributeCodesByProductType",
            "attributeService-navigatableAttributeDisplayValue",
            "attributeService-attributeNamesByCodes",
            "attributeService-allAttributeNames"},
            allEntries = true)
    public void delete(final Attribute instance) {
        attributeService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<Attribute> findByCriteria(final Criterion... criterion) {
        return attributeService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return attributeService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return attributeService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public Attribute findSingleByCriteria(final Criterion... criterion) {
        return attributeService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<Attribute, Long> getGenericDao() {
        return attributeService.getGenericDao();
    }
}
