package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:29 PM
 */
public interface DtoProdTypeAttributeViewGroupService  extends GenericDTOService<ProdTypeAttributeViewGroupDTO>  {


    /**
     * Get list of view groups, which belong to given product type.
     * @param productTypeId given product type.
     * @return list of attributes view group.
     */
    List<ProdTypeAttributeViewGroupDTO> getByProductTypeId(long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
