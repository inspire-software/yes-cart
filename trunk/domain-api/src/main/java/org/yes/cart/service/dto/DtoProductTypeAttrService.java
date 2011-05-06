package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.exception.UnableToCreateInstanceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoProductTypeAttrService extends GenericDTOService<ProductTypeAttrDTO> {

    /**
     * Get the product type attributes.
     * @param productTypeId proruct type id
     * @return list of {@link ProductTypeAttrDTO}, that belong to product type.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException in case of configuration problem
     */
    List<ProductTypeAttrDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
