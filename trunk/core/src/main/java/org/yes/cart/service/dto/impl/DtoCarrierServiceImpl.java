package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CarrierDTOImpl;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCarrierService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCarrierServiceImpl
    extends AbstractDtoServiceImpl<CarrierDTO, CarrierDTOImpl, Carrier>
    implements DtoCarrierService {

    /**
     * Construct service.
     * @param dtoFactory dto factory
     * @param carrierGenericService generic service to use
     * @param valueConverterRepository convertor factory.
     */
    public DtoCarrierServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<Carrier> carrierGenericService,
                                 final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, carrierGenericService, valueConverterRepository);
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO create(final CarrierDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Carrier carrier = getEntityFactory().getByIface(Carrier.class);
        assembler.assembleEntity(instance, carrier,  null, dtoFactory);
        carrier = service.create(carrier);
        return getById(carrier.getCarrierId());
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO update(final CarrierDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Carrier carrier = service.getById(instance.getCarrierId());
        assembler.assembleEntity(instance, carrier,  null, dtoFactory);
        carrier = service.update(carrier);
        return getById(carrier.getCarrierId());

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<CarrierDTO> getDtoIFace() {
        return CarrierDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<CarrierDTOImpl> getDtoImpl() {
        return CarrierDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Carrier> getEntityIFace() {
        return Carrier.class;
    }
}
