/*
 * Copyright 2009 Inspire-Software.com
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

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttributeDTOImpl;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.*;

/**
 *
 * Remote attribute service.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeServiceImpl
        extends AbstractDtoServiceImpl<AttributeDTO, AttributeDTOImpl, Attribute>
        implements DtoAttributeService {


    /**
     * Construct remote service.
     * @param attributeService {@link AttributeService}
     * @param dtoFactory {@link DtoFactory}
     */
    public DtoAttributeServiceImpl(
            final AttributeService attributeService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adapters) {
        super(dtoFactory, attributeService, adapters);
    }

    /** {@inheritDoc}  */
    @Override
    public AttributeDTO getByAttributeCode(final String attributeCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Attribute attribute = ((AttributeService)service).getByAttributeCode(attributeCode);
        if (attribute == null) {
            return null;
        }
        return fillDTO(attribute);
    }

    /** {@inheritDoc}  */
    @Override
    public AttributeDTO findByAttributeCode(final String attributeCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Attribute attribute = ((AttributeService)service).findByAttributeCode(attributeCode);
        if (attribute == null) {
            return null;
        }
        return fillDTO(attribute);
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findByAttributeGroupCode(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes =  ((AttributeService)service).findByAttributeGroupCode(attributeGroupCode);
        final List<AttributeDTO> attributesDTO = new ArrayList<>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findAvailableAttributes(
            final String attributeGroupCode,
            final List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Attribute> attributes =  ((AttributeService)service).findAvailableAttributes(
                attributeGroupCode, assignedAttributeCodes);

        final List<AttributeDTO> attributesDTO = new ArrayList<>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findAvailableAttributesByProductTypeId(
            final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Attribute> attributes = ((AttributeService)service).getAvailableAttributesByProductTypeId(productTypeId);
        final List<AttributeDTO> attributesDTO = new ArrayList<>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findAvailableImageAttributesByGroupCode(
            final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes = ((AttributeService)service).getAvailableImageAttributesByGroupCode(attributeGroupCode);
        final List<AttributeDTO> attributesDTO = new ArrayList<>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findAvailableAttributesByGroupCodeStartsWith(
            final String attributeGroupCode,
            final String codePrefix)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes = ((AttributeService)service).getAvailableAttributesByGroupCodeStartsWith(attributeGroupCode, codePrefix);
        final List<AttributeDTO> attributesDTO = new ArrayList<>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    @Override
    public List<AttributeDTO> findAttributesWithMultipleValues(
            final String attributeGroupCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attrs = ((AttributeService)service).findAttributesWithMultipleValues(attributeGroupCode);
        if (attrs != null) {
            final List<AttributeDTO> attributesDTO = new ArrayList<>(attrs.size());
            fillDTOs(attrs, attributesDTO);
            return attributesDTO;

        }
        return null;
    }

    private final static char[] CODE = new char[] { '#', '!' };
    static {
        Arrays.sort(CODE);
    }

    /** {@inheritDoc}  */
    @Override
    public SearchResult<AttributeDTO> findAttributes(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "groups");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final List groupsParam = params.get("groups");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();
        if (textFilter != null) {

            final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(textFilter, CODE);
            if (byCode != null) {

                if ("!".equals(byCode.getFirst())) {
                    currentFilter.put("code", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(byCode.getSecond())));
                } else {
                    currentFilter.put("code", Collections.singletonList(byCode.getSecond()));
                }

            } else {

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("code", Collections.singletonList(textFilter));
                currentFilter.put("name", Collections.singletonList(textFilter));
                currentFilter.put("displayNameInternal", Collections.singletonList(textFilter));
                currentFilter.put("description", Collections.singletonList(textFilter));

            }

        }

        if (CollectionUtils.isNotEmpty(groupsParam)) {
            currentFilter.put("groups", groupsParam);
        }


        final AttributeService attributeService = (AttributeService) service;

        final int count = attributeService.findAttributeCount(currentFilter);
        if (count > startIndex) {

            final List<AttributeDTO> entities = new ArrayList<>();
            final List<Attribute> attributes = attributeService.findAttributes(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(attributes, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);
    }

    /** {@inheritDoc}  */
    @Override
    public Class<AttributeDTO> getDtoIFace() {
        return AttributeDTO.class;
    }

    /** {@inheritDoc}  */
    @Override
    public Class<AttributeDTOImpl> getDtoImpl() {
        return AttributeDTOImpl.class;
    }

    /** {@inheritDoc}  */
    @Override
    public Class<Attribute> getEntityIFace() {
        return Attribute.class;
    }


}
