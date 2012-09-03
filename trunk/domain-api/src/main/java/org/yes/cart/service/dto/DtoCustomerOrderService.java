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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCustomerOrderService extends GenericDTOService<CustomerOrderDTO> {

    /**
     * Fire transition for single delivery. This method may lead to
     * change status for whole order , not only for single delivery.
     *
     *
     * @param orderNum unique order number. not pk value.
     * @param deliveryNum unique delivery number in order scope. not pk value.
     * @param currentStatus from status
     * @param destinationStatus to status
     * @return result object
     *
     */
    Result updateDeliveryStatus(String orderNum, String deliveryNum, String currentStatus, String destinationStatus);

    /**
     * Get list of delivery details for given order number.
     * @param orderNum order number
     * @return list of delivery details.
     * @throws UnmappedInterfaceException in case of dto mapping error
     * @throws UnableToCreateInstanceException  in case of dto mapping error
     */
    List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get list of <code>CustomerOrderDeliveryDTO</code> for given order number. Each delivery  will include
     * slave details objects.
     *
     * @param orderNum order number
     * @return list of deliveries.
     * @throws UnmappedInterfaceException in case of dto mapping error
     * @throws UnableToCreateInstanceException  in case of dto mapping error
     */
    List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;




    /**
     * Find customer's order by given criteria.
     *
     * @param customerId  customer id. Rest of parameters will be ignored, if customerId more that 0.
     * @param firstName   optional to perform search using like by first name
     * @param lastName    optional to perform search using like by last name
     * @param email       optional to perform search using like by email
     * @param orderStatus optional order status
     * @param fromDate    optional order created from
     * @param toDate    optional order created to
     * @param orderNum    optional to perform search using like by order number
     * @return list of customer's order dtos
     */
    List<CustomerOrderDTO> findCustomerOrdersByCriteria(
            long customerId,
            String firstName,
            String lastName,
            String email,
            String orderStatus,
            Date fromDate,
            Date toDate,
            String orderNum
    ) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
