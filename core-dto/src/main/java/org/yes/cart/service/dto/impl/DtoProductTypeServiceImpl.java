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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeDTOImpl;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductTypeService;
import org.yes.cart.service.dto.DtoProductTypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public Class<ProductTypeDTO> getDtoIFace() {
        return ProductTypeDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductTypeDTOImpl> getDtoImpl() {
        return ProductTypeDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductType> getEntityIFace() {
        return ProductType.class;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductTypeDTO> findProductTypes(final String name)  throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductType> entities;

        if (StringUtils.isNotBlank(name)) {
            entities = service.getGenericDao().findByCriteria(
                    Restrictions.or(
                            Restrictions.ilike("guid", name, MatchMode.ANYWHERE),
                            Restrictions.ilike("name", name, MatchMode.ANYWHERE),
                            Restrictions.ilike("description", name, MatchMode.ANYWHERE)
                    )
            );
        } else {
            entities = service.findAll();
        }
        final List<ProductTypeDTO> dtos = new ArrayList<ProductTypeDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    private final static char[] EXACT_OR_CODE = new char[] { '#', '!' };
    static {
        Arrays.sort(EXACT_OR_CODE);
    }

    private final static Order[] TYPE_ORDER = new Order[] { Order.asc("name") };

    /**
     * {@inheritDoc}
     */
    public List<ProductTypeDTO> findBy(final String name, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductType> entities;

        if (StringUtils.isNotBlank(name)) {

            final Pair<String, String> exactOrCode = ComplexSearchUtils.checkSpecialSearch(name, EXACT_OR_CODE);

            if (exactOrCode != null) {

                if ("!".equals(exactOrCode.getFirst())) {

                    entities = service.getGenericDao().findByCriteria(
                            page * pageSize, pageSize,
                            new Criterion[] {
                                    Restrictions.or(
                                            Restrictions.ilike("guid", exactOrCode.getSecond(), MatchMode.EXACT),
                                            Restrictions.ilike("name", exactOrCode.getSecond(), MatchMode.EXACT)
                                    )
                            },
                            TYPE_ORDER
                    );

                } else {

                    return findByAttributeCode(exactOrCode.getSecond());

                }

            } else {

                entities = service.getGenericDao().findByCriteria(
                        page * pageSize, pageSize,
                        new Criterion[] {
                                Restrictions.or(
                                        Restrictions.ilike("guid", name, MatchMode.ANYWHERE),
                                        Restrictions.ilike("name", name, MatchMode.ANYWHERE),
                                        Restrictions.ilike("description", name, MatchMode.ANYWHERE)
                                )
                        },
                        TYPE_ORDER
                );

            }
        } else {
            entities = service.getGenericDao().findByCriteria(page * pageSize, pageSize, new Criterion[0], TYPE_ORDER);
        }
        final List<ProductTypeDTO> dtos = new ArrayList<ProductTypeDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;

    }

    /**
     * {@inheritDoc}
     */
    public List<ProductTypeDTO> findByAttributeCode(final String code) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductType> entities = ((ProductTypeService) service).findByAttributeCode(code);
        final List<ProductTypeDTO> dtos = new ArrayList<ProductTypeDTO>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;

    }
}
