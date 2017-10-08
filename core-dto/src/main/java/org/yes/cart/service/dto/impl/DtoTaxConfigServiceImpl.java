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
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxConfigDTOImpl;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.dto.DtoTaxConfigService;
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 11:01
 */
public class DtoTaxConfigServiceImpl
    extends AbstractDtoServiceImpl<TaxConfigDTO, TaxConfigDTOImpl, TaxConfig>
        implements DtoTaxConfigService {


    private final GenericService<Tax> taxService;

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxConfigGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param taxGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoTaxConfigServiceImpl(final DtoFactory dtoFactory,
                                   final GenericService<TaxConfig> taxConfigGenericService,
                                   final GenericService<Tax> taxGenericService,
                                   final AdaptersRepository adaptersRepository) {
        super(dtoFactory, taxConfigGenericService, adaptersRepository);
        this.taxService = taxGenericService;
    }

    /** {@inheritDoc} */
    public List<TaxConfigDTO> findByTaxId(final long taxId, final String countryCode, final String stateCode, final String productCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<TaxConfig> configs = ((TaxConfigService) service).findByTaxId(taxId, countryCode, stateCode, productCode);
        final List<TaxConfigDTO> dtos = new ArrayList<TaxConfigDTO>();
        fillDTOs(configs, dtos);
        return dtos;
    }

    private final static char[] LOCATION = new char[] { '@' };
    private final static char[] SKU = new char[] { '#', '!' };
    static {
        Arrays.sort(LOCATION);
        Arrays.sort(SKU);
    }

    /** {@inheritDoc} */
    public List<TaxConfigDTO> findBy(final long taxId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<TaxConfigDTO> dtos = new ArrayList<>();


        if (taxId > 0) {
            // only allow lists for tax selection

            List<TaxConfig> entities = Collections.emptyList();

            if (StringUtils.isNotBlank(filter)) {

                final Pair<String, String> locationSearch = ComplexSearchUtils.checkSpecialSearch(filter, LOCATION);
                final Pair<String, String> skuSearch = locationSearch != null ? null : ComplexSearchUtils.checkSpecialSearch(filter, SKU);

                if (locationSearch != null) {

                    final String location = locationSearch.getSecond();

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where e.tax.taxId = ?1 and (lower(e.countryCode) = ?2 or lower(e.stateCode) like ?3) order by e.countryCode, e.countryCode, e.productCode",
                            page * pageSize, pageSize,
                            taxId,
                            HQLUtils.criteriaIeq(location),
                            HQLUtils.criteriaIlikeAnywhere(location)
                    );

                } else if (skuSearch != null) {

                    final String sku = skuSearch.getSecond();

                    if ("!".equals(sku)) {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where e.tax.taxId = ?1 and (e.productCode = '' or e.productCode is null) order by e.countryCode, e.countryCode",
                                page * pageSize, pageSize,
                                taxId
                        );

                    } else {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where e.tax.taxId = ?1 and lower(e.productCode) like ?2 order by e.countryCode, e.countryCode",
                                page * pageSize, pageSize,
                                taxId,
                                "!".equals(skuSearch.getFirst()) ? HQLUtils.criteriaIeq(sku) : HQLUtils.criteriaIlikeAnywhere(sku)
                        );

                    }

                } else {

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where e.tax.taxId = ?1 and (lower(e.countryCode) = ?2 or lower(e.stateCode) like ?3 or lower(e.productCode) like ?3) order by e.countryCode, e.countryCode, e.productCode",
                            page * pageSize, pageSize,
                            taxId,
                            HQLUtils.criteriaIeq(filter),
                            HQLUtils.criteriaIlikeAnywhere(filter)
                    );

                }

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where e.tax.taxId = ?1 order by e.countryCode, e.countryCode, e.productCode",
                        page * pageSize, pageSize,
                        taxId
                );

            }

            fillDTOs(entities, dtos);
        }

        return dtos;

    }

    protected void createPostProcess(final TaxConfigDTO dto, final TaxConfig entity) {
        entity.setTax(taxService.findById(dto.getTaxId()));
    }

    /** {@inheritDoc} */
    public Class<TaxConfigDTO> getDtoIFace() {
        return TaxConfigDTO.class;
    }

    /** {@inheritDoc} */
    public Class<TaxConfigDTOImpl> getDtoImpl() {
        return TaxConfigDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<TaxConfig> getEntityIFace() {
        return TaxConfig.class;
    }
}
