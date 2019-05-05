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

package org.yes.cart.service.dto.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.ContentDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoContentService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/04/2019
 * Time: 11:59
 */
public class DtoContentServiceFactoryImpl implements DtoContentService, ConfigurationRegistry<String, DtoContentService> {

    private static final Logger LOG = LoggerFactory.getLogger(DtoContentServiceFactoryImpl.class);

    private DtoContentService active;
    private final DtoContentService fallback;

    public DtoContentServiceFactoryImpl(final DtoContentService base) {
        this.active = base;
        this.fallback = base;
    }

    @Override
    public void createContentRoot(final long shopId) {
        active.createContentRoot(shopId);
    }

    @Override
    public List<ContentDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getAllByShopId(shopId);
    }

    @Override
    public List<ContentDTO> findBy(final long shopId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.findBy(shopId, filter, page, pageSize);
    }

    @Override
    public List<ContentDTO> getAllWithAvailabilityFilter(final long shopId, final boolean withAvailabilityFiltering) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getAllWithAvailabilityFilter(shopId, withAvailabilityFiltering);
    }

    @Override
    public List<ContentDTO> getBranchById(final long shopId, final long categoryId, final List<Long> expand) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getBranchById(shopId, categoryId, expand);
    }

    @Override
    public List<ContentDTO> getBranchByIdWithAvailabilityFilter(final long shopId, final long contentId, final boolean withAvailabilityFiltering, final List<Long> expand) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getBranchByIdWithAvailabilityFilter(shopId, contentId, withAvailabilityFiltering, expand);
    }

    @Override
    public List<? extends AttrValueDTO> getEntityContentAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getEntityContentAttributes(entityPk);
    }

    @Override
    public boolean isUriAvailableForContent(final String seoUri, final Long contentId) {
        return active.isUriAvailableForContent(seoUri, contentId);
    }

    @Override
    public boolean isGuidAvailableForContent(final String guid, final Long contentId) {
        return active.isGuidAvailableForContent(guid, contentId);
    }

    @Override
    public List<ContentDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getAll();
    }

    @Override
    public ContentDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getById(id);
    }

    @Override
    public ContentDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getById(id, converters);
    }

    @Override
    public ContentDTO getNew() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        return active.getNew();
    }

    @Override
    public ContentDTO create(final ContentDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.create(instance);
    }

    @Override
    public ContentDTO update(final ContentDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.update(instance);
    }

    @Override
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        active.remove(id);
    }

    @Override
    public GenericService getService() {
        return active.getService();
    }

    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.getEntityAttributes(entityPk);
    }

    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.updateEntityAttributeValue(attrValueDTO);
    }

    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.createEntityAttributeValue(attrValueDTO);
    }

    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.createAndBindAttrVal(entityPk, attrName, attrValue);
    }

    @Override
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return active.deleteAttributeValue(attributeValuePk);
    }

    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        return active.getNewAttribute(entityPk);
    }

    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof DtoContentService ||
                (configuration instanceof Class && DtoContentService.class.isAssignableFrom((Class<?>) configuration));

    }

    @Override
    public void register(final String key, final DtoContentService dtoContentService) {

        if (dtoContentService != null) {
            LOG.debug("Custom CMS settings registering {}", dtoContentService.getClass());
            active = dtoContentService;
        } else {
            LOG.debug("Custom CMS settings registering DEFAULT {}", fallback.getClass());
            active = fallback;
        }


    }

}
