package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCustomerService extends GenericDTOService<CustomerDTO>, GenericAttrValueService {

    /**
     * Find customer by given serach criteria. Serch will be performed using like operation.
     *
     * @param email      optional email
     * @param firstname  optional first name
     * @param lastname   optional last name
     * @param middlename optional middlename
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of dto mapping errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of config errors
     */
    List<CustomerDTO> findCustomer(String email, String firstname, String lastname, String middlename) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shopId   from what shop customer will have notification
     */
    void remoteResetPassword(CustomerDTO customer, final long shopId);


}
