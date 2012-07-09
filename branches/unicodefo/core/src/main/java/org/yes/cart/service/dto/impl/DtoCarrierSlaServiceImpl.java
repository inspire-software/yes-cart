package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CarrierSlaDTOImpl;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCarrierSlaService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCarrierSlaServiceImpl
    extends AbstractDtoServiceImpl<CarrierSlaDTO, CarrierSlaDTOImpl, CarrierSla>
    implements DtoCarrierSlaService {

    /**
     * Construct service.
     * @param dtoFactory dto factory
     * @param carrierSlaGenericService generic service to use
     * @param AdaptersRepository convertor factory.
     */
    public DtoCarrierSlaServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<CarrierSla> carrierSlaGenericService,
                                 final AdaptersRepository AdaptersRepository) {
        super(dtoFactory, carrierSlaGenericService, AdaptersRepository);
    }




    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> findByCarrier(final long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getDTOs(
                ((CarrierSlaService)service).findByCarrier(carrierId)
        );
    }


    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<CarrierSlaDTO> getDtoIFace() {
        return CarrierSlaDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<CarrierSlaDTOImpl> getDtoImpl() {
        return CarrierSlaDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<CarrierSla> getEntityIFace() {
        return CarrierSla.class;
    }
}
