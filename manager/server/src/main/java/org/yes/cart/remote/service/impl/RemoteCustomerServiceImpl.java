/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCustomerService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCustomerServiceImpl
        extends AbstractRemoteService<CustomerDTO>
        implements RemoteCustomerService {

    private final DtoCustomerService dtoCustomerService;
    private final FederationFacade federationFacade;

    /**
     * Construct service to manage the users.
     *
     * @param customerDTOGenericService dto service to use
     * @param federationFacade
     */
    public RemoteCustomerServiceImpl(final GenericDTOService<CustomerDTO> customerDTOGenericService,
                                     final FederationFacade federationFacade) {
        super(customerDTOGenericService);
        this.federationFacade = federationFacade;
        dtoCustomerService = (DtoCustomerService) customerDTOGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerDTO> all = super.getAll();
        federationFacade.applyFederationFilter(all, CustomerDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CustomerDTO.class)) {
            return super.getById(id);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> findCustomer(final String email,
                                          final String firstname,
                                          final String lastname,
                                          final String middlename,
                                          final String tag)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerDTO> customers = dtoCustomerService.findCustomer(email, firstname, lastname, middlename, tag);
        federationFacade.applyFederationFilter(customers, CustomerDTO.class);
        return customers;
    }

    /**
     * {@inheritDoc}
     */
    public void remoteResetPassword(final CustomerDTO customer, final long shopId) {
        if (federationFacade.isManageable(customer.getCustomerId(), CustomerDTO.class)) {
            dtoCustomerService.remoteResetPassword(customer, shopId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateCustomerTags(final CustomerDTO customerDTO, final String tags) {
        if (federationFacade.isManageable(customerDTO.getCustomerId(), CustomerDTO.class)) {
            dtoCustomerService.updateCustomerTags(customerDTO, tags);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoCustomerService.deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoCustomerService.createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoCustomerService.updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, CustomerDTO.class)) {
            return dtoCustomerService.getEntityAttributes(entityPk);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(customerId, CustomerDTO.class)) {
            final List<ShopDTO> assigned = dtoCustomerService.getAssignedShop(customerId);

            if (!federationFacade.isCurrentUserSystemAdmin()) { // restrict other managers
                final Set<Long> currentAssignedIds = federationFacade.getAccessibleShopIdsByCurrentManager();
                final Iterator<ShopDTO> availableIt = assigned.iterator();
                while (availableIt.hasNext()) {
                    final ShopDTO shop = availableIt.next();
                    if (!currentAssignedIds.contains(shop.getShopId())) {
                        availableIt.remove();
                    }
                }
            }
            return assigned;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAvailableShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(customerId, CustomerDTO.class)) {

            final List<ShopDTO> available = dtoCustomerService.getAvailableShop(customerId);

            if (!federationFacade.isCurrentUserSystemAdmin()) { // restrict other managers
                final Set<Long> currentAssignedIds = federationFacade.getAccessibleShopIdsByCurrentManager();
                final Iterator<ShopDTO> availableIt = available.iterator();
                while (availableIt.hasNext()) {
                    final ShopDTO shop = availableIt.next();
                    if (!currentAssignedIds.contains(shop.getShopId())) {
                        availableIt.remove();
                    }
                }
            }
            return available;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public void grantShop(final long customerId, final String shopCode) {
        if (federationFacade.isManageable(customerId, CustomerDTO.class)
                && federationFacade.isShopAccessibleByCurrentManager(shopCode)) {
            dtoCustomerService.grantShop(customerId, shopCode);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void revokeShop(final long customerId, final String shopCode) {
        if (federationFacade.isManageable(customerId, CustomerDTO.class)
                && federationFacade.isShopAccessibleByCurrentManager(shopCode)) {
            dtoCustomerService.revokeShop(customerId, shopCode);
        }
    }

}
