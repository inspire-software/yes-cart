package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.CustomerWishListDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerWishListDTOImpl;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerWishListService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerWishListServiceImpl
    extends AbstractDtoServiceImpl<CustomerWishListDTO, CustomerWishListDTOImpl, CustomerWishList>
        implements DtoCustomerWishListService {

    /**
     * construct service.
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerWishListGenericService    {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoCustomerWishListServiceImpl(final DtoFactory dtoFactory,
                                          final GenericService<CustomerWishList> customerWishListGenericService,
                                          final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, customerWishListGenericService, valueConverterRepository);
    }



    /** {@inheritDoc} */
    public CustomerWishListDTO update(final CustomerWishListDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CustomerWishList entity = service.getById(instance.getCustomerwishlistId());
        assembler.assembleEntity(instance, entity,  null, dtoFactory);
        entity = service.update(entity);
        return getById(entity.getCustomerwishlistId());

    }

    /** {@inheritDoc} */
    public Class<CustomerWishListDTO> getDtoIFace() {
        return CustomerWishListDTO.class;
    }

    /** {@inheritDoc} */
    public Class<CustomerWishListDTOImpl> getDtoImpl() {
        return CustomerWishListDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<CustomerWishList> getEntityIFace() {
        return CustomerWishList.class;
    }

    /** {@inheritDoc} */
    public List<CustomerWishListDTO> getByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getDTOs(((CustomerWishListService)service).getByCustomerId(customerId));
    }
}
