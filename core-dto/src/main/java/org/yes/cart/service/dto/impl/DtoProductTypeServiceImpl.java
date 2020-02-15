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

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeDTOImpl;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductTypeService;
import org.yes.cart.service.dto.DtoProductTypeService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeServiceImpl
        extends AbstractDtoServiceImpl<ProductTypeDTO, ProductTypeDTOImpl, ProductType>
        implements DtoProductTypeService {


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productTypeGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoProductTypeServiceImpl(final GenericService<ProductType> productTypeGenericService,
                                     final DtoFactory dtoFactory,
                                     final AdaptersRepository adaptersRepository) {
        super(dtoFactory, productTypeGenericService, adaptersRepository);
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<ProductTypeDTO> getDtoIFace() {
        return ProductTypeDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<ProductTypeDTOImpl> getDtoImpl() {
        return ProductTypeDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<ProductType> getEntityIFace() {
        return ProductType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductTypeDTO> findProductTypes(final String name)  throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductType> entities;

        if (StringUtils.isNotBlank(name)) {
            entities = service.getGenericDao().findByCriteria(
                    " where lower(e.guid) like ?1 or lower(e.name) like ?1 or lower(e.description) like ?1 order by e.name",
                    HQLUtils.criteriaIlikeAnywhere(name)
            );
        } else {
            entities = service.findAll();
        }
        final List<ProductTypeDTO> dtos = new ArrayList<>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    private final static char[] EXACT_OR_CODE = new char[] { '#', '!' };
    static {
        Arrays.sort(EXACT_OR_CODE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResult<ProductTypeDTO> findProductTypes(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();

        if (textFilter != null) {

            final Pair<String, String> exactOrCode = ComplexSearchUtils.checkSpecialSearch(textFilter, EXACT_OR_CODE);

            if (exactOrCode != null) {

                if ("!".equals(exactOrCode.getFirst())) {

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("guid", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(exactOrCode.getSecond())));
                    currentFilter.put("name", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(exactOrCode.getSecond())));

                } else {

                    final List<ProductTypeDTO> typesByCode = findByAttributeCode(exactOrCode.getSecond());
                    final List<ProductTypeDTO> typesByCodePage;
                    if (startIndex > typesByCode.size()) {
                        typesByCodePage = Collections.emptyList();
                    } else if (startIndex + pageSize < typesByCode.size()) {
                        typesByCodePage = typesByCode.subList(startIndex, startIndex + pageSize);
                    } else {
                        typesByCodePage = typesByCode.subList(startIndex, typesByCode.size());
                    }

                    return new SearchResult<>(filter, typesByCodePage, typesByCode.size());

                }

            } else {

                final String basic = textFilter;

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("guid", Collections.singletonList(basic));
                currentFilter.put("name", Collections.singletonList(basic));
                currentFilter.put("description", Collections.singletonList(basic));

            }

        }

        final ProductTypeService productTypeService = (ProductTypeService) service;

        final int count = productTypeService.findProductTypeCount(currentFilter);
        if (count > startIndex) {

            final List<ProductTypeDTO> entities = new ArrayList<>();
            final List<ProductType> productTypes = productTypeService.findProductTypes(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(productTypes, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductTypeDTO> findByAttributeCode(final String code) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductType> entities = ((ProductTypeService) service).findByAttributeCode(code);
        final List<ProductTypeDTO> dtos = new ArrayList<>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;

    }
}
