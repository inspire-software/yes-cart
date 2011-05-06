package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CustomerOrderDTO;
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
     * Find customer's order by given criteria.
     * @param customerId customer id. Rest of parameters will be ignored, if customerId more that 0.
     * @param firstName optional to perform search using like by first name
     * @param lastName optional to perform search using like by last name
     * @param email optional to perform search using like by email
     * @param orderStatus optional order status
     * @param fromDate optional order created from
     * @param tillDate optional orer created till
     * @param orderNum optional to perform search using like by order number
     * @return list of customer's order dtos
     */
    List<CustomerOrderDTO> findCustomerOrdersByCriterias(
            long customerId,
            String firstName,
            String lastName,
            String email,
            String orderStatus,
            Date fromDate,
            Date tillDate,
            String orderNum
            ) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    

}
