package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AddressDTOImpl;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAddressService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAddressServiceImpl
    extends AbstractDtoServiceImpl<AddressDTO, AddressDTOImpl, Address>
        implements DtoAddressService {
    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param addressGenericService    {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoAddressServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Address> addressGenericService,
            final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, addressGenericService, valueConverterRepository);
    }


    /** {@inheritDoc} */
    public AddressDTO update(final AddressDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Address address = service.getById(instance.getAddressId());
        assembler.assembleEntity(instance, address, null, dtoFactory);
        address = service.update(address);
        return getById(address.getAddressId());
    }

    /** {@inheritDoc} */
    public List<AddressDTO> getAddressesByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {        
        return getDTOs(((AddressService)service).getAddressesByCustomerId(customerId));

    }

    /** {@inheritDoc} */
    public Class<AddressDTO> getDtoIFace() {
        return AddressDTO.class;
    }

    /** {@inheritDoc} */
    public Class<AddressDTOImpl> getDtoImpl() {
        return AddressDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Address> getEntityIFace() {
        return Address.class;
    }
}
