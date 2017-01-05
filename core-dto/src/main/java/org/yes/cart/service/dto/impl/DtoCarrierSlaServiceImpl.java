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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CarrierSlaDTOImpl;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCarrierSlaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCarrierSlaServiceImpl
    extends AbstractDtoServiceImpl<CarrierSlaDTO, CarrierSlaDTOImpl, CarrierSla>
    implements DtoCarrierSlaService {

    /**
     * Construct service.
     * @param dtoFactory dto factory
     * @param carrierSlaGenericService generic service to use
     * @param AdaptersRepository convertor factory.
     */
    public DtoCarrierSlaServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<CarrierSla> carrierSlaGenericService,
                                 final AdaptersRepository AdaptersRepository) {
        super(dtoFactory, carrierSlaGenericService, AdaptersRepository);
    }




    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> findByCarrier(final long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getDTOs(
                ((CarrierSlaService)service).findByCarrier(carrierId)
        );
    }

    private final static char[] CODE = new char[] { '!' };
    static {
        Arrays.sort(CODE);
    }

    private final static Order[] SLA_ORDER = new Order[] { Order.asc("name") };


    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CarrierSlaDTO> dtos = new ArrayList<CarrierSlaDTO>();

        final List<Criterion> criteria = new ArrayList<Criterion>();

        if (org.springframework.util.StringUtils.hasLength(filter)) {

            final Pair<String, String> code = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

            if (code != null) {

                if ("!".equals(code.getFirst())) {

                    criteria.add(Restrictions.ilike("guid", code.getSecond(), MatchMode.EXACT));

                }

            } else {

                criteria.add(Restrictions.or(
                        Restrictions.ilike("guid", filter, MatchMode.ANYWHERE),
                        Restrictions.ilike("name", filter, MatchMode.ANYWHERE)
                ));

            }

        }

        final List<CarrierSla> entities = getService().getGenericDao().findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), SLA_ORDER);

        fillDTOs(entities, dtos);

        return dtos;
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<CarrierSlaDTO> getDtoIFace() {
        return CarrierSlaDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<CarrierSlaDTOImpl> getDtoImpl() {
        return CarrierSlaDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<CarrierSla> getEntityIFace() {
        return CarrierSla.class;
    }
}
