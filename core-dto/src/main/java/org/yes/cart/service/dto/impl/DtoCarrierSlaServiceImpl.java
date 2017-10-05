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
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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


    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CarrierSlaDTO> dtos = new ArrayList<CarrierSlaDTO>();

        List<CarrierSla> entities = Collections.emptyList();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> code = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

            if (code != null) {

                if ("!".equals(code.getFirst())) {

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where lower(e.guid) = ?1 order by e.name",
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIeq(code.getSecond())
                    );

                }

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where lower(e.guid) like ?1 or lower(e.name) like ?1 order by e.name",
                        page * pageSize, pageSize,
                        HQLUtils.criteriaIlikeAnywhere(filter)
                );

            }

        }

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
