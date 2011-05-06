package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoProductAssociationService extends GenericDTOService<ProductAssociationDTO> {

    /**
     * Get all product associations.
     * @param productId product primary key
     * @return list of product assotiations
     */
    List<ProductAssociationDTO> getProductAssociations(long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get all product associations by association type.
     * @param productId product primary key
     * @param accosiationCode accosiation code [up, cross, etc]
     * @return list of product assotiations
     */
    List<ProductAssociationDTO> getProductAssociationsByProductAssociationType(
            long productId,
            String accosiationCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
