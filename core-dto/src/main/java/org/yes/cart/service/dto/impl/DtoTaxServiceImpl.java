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
import org.springframework.util.StringUtils;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxDTOImpl;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.service.dto.DtoTaxService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 10:55
 */
public class DtoTaxServiceImpl
    extends AbstractDtoServiceImpl<TaxDTO, TaxDTOImpl, Tax>
        implements DtoTaxService {

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoTaxServiceImpl(final DtoFactory dtoFactory,
                             final GenericService<Tax> taxGenericService,
                             final AdaptersRepository adaptersRepository) {
        super(dtoFactory, taxGenericService, adaptersRepository);
    }

    /** {@inheritDoc} */
    public List<TaxDTO> findByParameters(final String code,
                                         final String shopCode,
                                         final String currency)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Tax> taxes = ((TaxService) service).findByParameters(code, shopCode, currency);
        final List<TaxDTO> dtos = new ArrayList<TaxDTO>();
        fillDTOs(taxes, dtos);
        return dtos;
    }

    private final static char[] RATE = new char[] { '%' };
    private final static char[] EXCLUSIVE_INCLUSIVE = new char[] { '%', '-', '+' };
    static {
        Arrays.sort(RATE);
        Arrays.sort(EXCLUSIVE_INCLUSIVE);
    }

    private final static Order[] TAX_ORDER = new Order[] { Order.asc("code") };

    /** {@inheritDoc} */
    public List<TaxDTO> findBy(final String shopCode, final String currency, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<TaxDTO> dtos = new ArrayList<>();


        if (StringUtils.hasLength(shopCode) && StringUtils.hasLength(currency)) {
            // only allow lists for shop+currency selection

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("shopCode", shopCode));
            criteria.add(Restrictions.eq("currency", currency));
            if (StringUtils.hasLength(filter)) {

                final Pair<String, String> exclOrIncSearch = ComplexSearchUtils.checkSpecialSearch(filter, EXCLUSIVE_INCLUSIVE);
                final Pair<String, BigDecimal> rateSearch = ComplexSearchUtils.checkNumericSearch(exclOrIncSearch != null ? filter.substring(1) : filter, RATE, 2);

                if (exclOrIncSearch != null) {

                    final boolean all = exclOrIncSearch.getFirst().equals(exclOrIncSearch.getSecond().substring(0, 1));

                    if ("+".equals(exclOrIncSearch.getFirst())) {

                        criteria.add(Restrictions.eq("exclusiveOfPrice", Boolean.TRUE));

                    } else if ("-".equals(exclOrIncSearch.getFirst())) {

                        criteria.add(Restrictions.eq("exclusiveOfPrice", Boolean.FALSE));

                    }

                    if (rateSearch != null) {

                        criteria.add(Restrictions.eq("taxRate", rateSearch.getSecond()));

                    }

                    if (!all) {

                        final String search = exclOrIncSearch.getSecond();

                        criteria.add(Restrictions.or(
                                Restrictions.ilike("code", search, MatchMode.ANYWHERE),
                                Restrictions.ilike("description", search, MatchMode.ANYWHERE)
                        ));

                    }

                } else {

                    criteria.add(Restrictions.or(
                            Restrictions.ilike("code", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("description", filter, MatchMode.ANYWHERE)
                    ));

                }

            }

            final List<Tax> entities = getService().getGenericDao().findByCriteria(
                    page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), TAX_ORDER);

            fillDTOs(entities, dtos);
        }

        return dtos;
    }

    /** {@inheritDoc} */
    public Class<TaxDTO> getDtoIFace() {
        return TaxDTO.class;
    }

    /** {@inheritDoc} */
    public Class<TaxDTOImpl> getDtoImpl() {
        return TaxDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Tax> getEntityIFace() {
        return Tax.class;
    }


}
