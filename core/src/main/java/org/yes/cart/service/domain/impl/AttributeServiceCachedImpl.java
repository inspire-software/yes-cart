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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
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
    @Override
    public List<Attribute> findByAttributeGroupCode(final String attributeGroupCode) {
        return attributeService.findByAttributeGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute findByAttributeCode(final String attributeCode) {
        return attributeService.findByAttributeCode(attributeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-byAttributeCode")
    public Attribute getByAttributeCode(final String attributeCode) {
        return attributeService.getByAttributeCode(attributeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAttributesWithMultipleValues(final String attributeGroupCode) {
        return attributeService.findAttributesWithMultipleValues(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAttributes(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return attributeService.findAttributes(start, offset, sort, sortDescending, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findAttributeCount(final Map<String, List> filter) {
        return attributeService.findAttributeCount(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAvailableAttributes(final String attributeGroupCode, final List<String> exclude) {
        return attributeService.findAvailableAttributes(attributeGroupCode, exclude);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAttributesByCodes(final String attributeGroupCode, final List<String> codes) {
        return attributeService.findAttributesByCodes(attributeGroupCode, codes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-availableAttributesByProductTypeId")
    public List<Attribute> getAvailableAttributesByProductTypeId(final long productTypeId) {
        return attributeService.getAvailableAttributesByProductTypeId(productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-availableImageAttributesByGroupCode")
    public List<Attribute> getAvailableImageAttributesByGroupCode(final String attributeGroupCode) {
        return attributeService.getAvailableImageAttributesByGroupCode(attributeGroupCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-availableAttributesByGroupCodeStartsWith")
    public List<Attribute> getAvailableAttributesByGroupCodeStartsWith(final String attributeGroupCode, final String codePrefix) {
        return attributeService.getAvailableAttributesByGroupCodeStartsWith(attributeGroupCode, codePrefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allAttributeCodes")
    public Set<String> getAllAttributeCodes() {
        return attributeService.getAllAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allNavigatableAttributeCodes")
    public Set<String> getAllNavigatableAttributeCodes() {
        return attributeService.getAllNavigatableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allSearchableAttributeCodes")
    public Set<String> getAllSearchableAttributeCodes() {
        return attributeService.getAllSearchableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allSearchablePrimaryAttributeCodes")
    public Set<String> getAllSearchablePrimaryAttributeCodes() {
        return attributeService.getAllSearchablePrimaryAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allStorableAttributeCodes")
    public Set<String> getAllStorableAttributeCodes() {
        return attributeService.getAllStorableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-singleNavigatableAttributeCodesByProductType")
    public Map<String, Integer> getSingleNavigatableAttributeCodesByProductType(final long productTypeId) {
        return attributeService.getSingleNavigatableAttributeCodesByProductType(productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-navigatableAttributeDisplayValue")
    public I18NModel getNavigatableAttributeDisplayValue(final String attrCode, final String value) {
        return attributeService.getNavigatableAttributeDisplayValue(attrCode, value);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-allAttributeNames")
    public Map<String, I18NModel> getAllAttributeNames() {
        return attributeService.getAllAttributeNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "attributeService-attributeNamesByCodes")
    public Map<String, I18NModel> getAttributeNamesByCodes(final Set<String> codes) {
        return attributeService.getAttributeNamesByCodes(codes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Attribute> findAll() {
        return attributeService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Attribute> callback) {
        attributeService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Attribute> callback) {
        attributeService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute findById(final long pk) {
        return attributeService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-byAttributeCode",
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
    @Override
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-byAttributeCode",
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
    @Override
    @CacheEvict(value = {"attributeService-availableAttributesByProductTypeId",
            "attributeService-availableImageAttributesByGroupCode",
            "attributeService-availableAttributesByGroupCodeStartsWith",
            "attributeService-byAttributeCode",
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
    @Override
    public List<Attribute> findByCriteria(final String eCriteria, final Object... parameters) {
        return attributeService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return attributeService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Attribute findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return attributeService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Attribute, Long> getGenericDao() {
        return attributeService.getGenericDao();
    }
}
