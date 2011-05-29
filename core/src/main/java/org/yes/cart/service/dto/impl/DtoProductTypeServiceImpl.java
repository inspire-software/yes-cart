package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeDTOImpl;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeServiceImpl
        extends AbstractDtoServiceImpl<ProductTypeDTO, ProductTypeDTOImpl, ProductType>
        implements DtoProductTypeService {


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productTypeGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoProductTypeServiceImpl(
            final GenericService<ProductType> productTypeGenericService, final DtoFactory dtoFactory ) {
        super(dtoFactory, productTypeGenericService, null);
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductTypeDTO> getDtoIFace() {
        return ProductTypeDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductTypeDTOImpl> getDtoImpl() {
        return ProductTypeDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductType> getEntityIFace() {
        return ProductType.class;
    }
}
