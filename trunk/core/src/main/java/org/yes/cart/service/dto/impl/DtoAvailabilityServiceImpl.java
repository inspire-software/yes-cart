package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.AvailabilityDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AvailabilityDTOImpl;
import org.yes.cart.domain.entity.Availability;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToDeleteInstanceError;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAvailabilityService;

/**
 *
 * Availability DTO service to manage product and category availability.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAvailabilityServiceImpl
    extends AbstractDtoServiceImpl<AvailabilityDTO, AvailabilityDTOImpl, Availability>
    implements DtoAvailabilityService {

    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param availabilityGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoAvailabilityServiceImpl(final DtoFactory dtoFactory,
                                      final GenericService<Availability> availabilityGenericService) {
        super(dtoFactory, availabilityGenericService, null);
    }


    /**
     * {@inheritDoc}
     */
    public AvailabilityDTO create(final AvailabilityDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnableToCreateInstanceException("AvailabilityDTO can not be created", null);
    }

    /**
     * {@inheritDoc}
     */
    public AvailabilityDTO update(final AvailabilityDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnableToCreateInstanceException("AvailabilityDTO is read only", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final long id) {
        throw new UnableToDeleteInstanceError("AvailabilityDTO can nto be deleted");
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<AvailabilityDTO> getDtoIFace() {
        return AvailabilityDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<AvailabilityDTOImpl> getDtoImpl() {
        return AvailabilityDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Availability> getEntityIFace() {
        return Availability.class;
    }
}
