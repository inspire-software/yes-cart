/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.vo.VoProductSupplierCatalog;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoProductSupplierService;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 14/12/2019
 * Time: 08:12
 */
public class VoProductSupplierServiceImpl implements VoProductSupplierService {

    private final DtoProductService  dtoProductService;

    private final FederationFacade federationFacade;

    public VoProductSupplierServiceImpl(final DtoProductService dtoProductService,
                                        final FederationFacade federationFacade) {
        this.dtoProductService = dtoProductService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    @Override
    public List<VoProductSupplierCatalog> getAllProductSuppliersCatalogs() throws Exception {
        final Collection<String> catalogCodes;
        if (federationFacade.isCurrentUserSystemAdmin()) {
            catalogCodes = new TreeSet<>(dtoProductService.findProductSupplierCatalogCodes());
        } else {
            catalogCodes = new TreeSet<>(federationFacade.getAccessibleSupplierCatalogCodesByCurrentManager());
        }
        return catalogCodes.stream().map(code -> {
            final VoProductSupplierCatalog catalog = new VoProductSupplierCatalog();
            catalog.setCode(code);
            return catalog;
        }).collect(Collectors.toList());
    }
}
