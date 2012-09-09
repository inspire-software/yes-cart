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
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.dto.GenericDTOService;

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

    /**
     * Construct remote service
     *
     * @param customerOrderDTOGenericDTOService
     *         dto serivese to use.
     */
    public RemoteCustomerOrderServiceImpl(final GenericDTOService<CustomerOrderDTO> customerOrderDTOGenericDTOService) {
        super(customerOrderDTOGenericDTOService);
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
        return ((DtoCustomerOrderService) getGenericDTOService()).findCustomerOrdersByCriteria(
                customerId,
                firstName,
                lastName,
                email,
                orderStatus,
                fromDate,
                toDate,
                orderNum
        );
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCustomerOrderService) getGenericDTOService()).findDeliveryDetailsByOrderNumber(orderNum);
    }


    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetConfirmed(String orderNum) {
        return ((DtoCustomerOrderService) getGenericDTOService()).updateOrderSetConfirmed(orderNum);
    }

    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetCancelled(final String orderNum) {
        return ((DtoCustomerOrderService) getGenericDTOService()).updateOrderSetCancelled(orderNum);
    }

    /** {@inheritDoc} */
    public Result updateExternalDelieryRefNo(String orderNum, String deliveryNum, String newRefNo) {
        return ((DtoCustomerOrderService) getGenericDTOService()).updateExternalDelieryRefNo(orderNum,  deliveryNum,  newRefNo) ;
    }

    /** {@inheritDoc} */
    public Result updateDeliveryStatus(final String orderNum, final String deliveryNum,
                                       final String currentStatus, final String destinationStatus) {

        return ((DtoCustomerOrderService) getGenericDTOService())
                .updateDeliveryStatus(orderNum, deliveryNum, currentStatus, destinationStatus);

    }

    /** {@inheritDoc} */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCustomerOrderService) getGenericDTOService()).findDeliveryByOrderNumber(orderNum);
    }
}
