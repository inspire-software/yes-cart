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

import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCustomerOrderService;
import org.yes.cart.report.ReportService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCustomerOrderServiceImpl
        extends AbstractRemoteService<CustomerOrderDTO>
        implements RemoteCustomerOrderService {

    private static final Result AUTH_ERROR = new Result("OR-1000", "Auth failed");

    private final ReportService reportService;
    private final FederationFacade federationFacade;

    /**
     * Construct remote service
     *
     * @param customerOrderDTOGenericDTOService
     *         dto serivese to use.
     * @param federationFacade
     */
    public RemoteCustomerOrderServiceImpl(final GenericDTOService<CustomerOrderDTO> customerOrderDTOGenericDTOService,
                                          final ReportService reportService,
                                          final FederationFacade federationFacade) {
        super(customerOrderDTOGenericDTOService);
        this.reportService = reportService;
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrderDTO> all = super.getAll();
        federationFacade.applyFederationFilter(all, CustomerOrderDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrderDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CustomerOrderDTO.class)) {
            return super.getById(id);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDTO> findCustomerOrdersByCriteria(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date toDate,
            final String orderNum
    ) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrderDTO> orders = ((DtoCustomerOrderService) getGenericDTOService()).findCustomerOrdersByCriteria(
                customerId,
                firstName,
                lastName,
                email,
                orderStatus,
                fromDate,
                toDate,
                orderNum
        );
        federationFacade.applyFederationFilter(orders, CustomerOrderDTO.class);
        return orders;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).findDeliveryDetailsByOrderNumber(orderNum);
        }
        return Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetConfirmed(String orderNum) {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).updateOrderSetConfirmed(orderNum);
        }
        return AUTH_ERROR;
    }

    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetCancelled(final String orderNum) {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).updateOrderSetCancelled(orderNum);
        }
        return AUTH_ERROR;
    }

    /** {@inheritDoc} */
    public Result updateExternalDeliveryRefNo(String orderNum, String deliveryNum, String newRefNo) {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).updateExternalDeliveryRefNo(orderNum, deliveryNum, newRefNo) ;
        }
        return AUTH_ERROR;
    }

    /** {@inheritDoc} */
    public Result updateDeliveryStatus(final String orderNum, final String deliveryNum,
                                       final String currentStatus, final String destinationStatus) {

        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService())
                .updateDeliveryStatus(orderNum, deliveryNum, currentStatus, destinationStatus);
        }
        return AUTH_ERROR;

    }

    /** {@inheritDoc} */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).findDeliveryByOrderNumber(orderNum);
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum, final String deliveryNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {
            return ((DtoCustomerOrderService) getGenericDTOService()).findDeliveryByOrderNumber(orderNum, deliveryNum);
        }
        return Collections.emptyList();
    }



    /** {@inheritDoc} */
    public byte[] produceDeliveryReport(final String reportLang, final String orderNum, final String deliveryNum)
            throws Exception {
        if (federationFacade.isManageable(orderNum, CustomerOrderDTO.class)) {

            List rez =  findDeliveryByOrderNumber(orderNum, deliveryNum);

            return reportService.produceReport(reportLang, "reportDelivery", rez);
        }
        return new byte[0];

    }

}
