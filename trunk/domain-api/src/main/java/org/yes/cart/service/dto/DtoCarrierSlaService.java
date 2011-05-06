package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.exception.UnableToCreateInstanceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCarrierSlaService extends GenericDTOService<CarrierSlaDTO>{

    /**
     * Get shipping SLA by carrier Id.
     * @param carrierId given carrier id
     * @return list of SLA, that belongs to given carrier id
     */
    List<CarrierSlaDTO> findByCarrier(long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
