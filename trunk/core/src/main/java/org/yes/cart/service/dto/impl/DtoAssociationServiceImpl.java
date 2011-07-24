package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.AssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AssociationDTOImpl;
import org.yes.cart.domain.entity.Association;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAssociationService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAssociationServiceImpl
        extends AbstractDtoServiceImpl<AssociationDTO, AssociationDTOImpl, Association>
        implements DtoAssociationService {

    /**
     * Construct association remote service.
     *
     * @param dtoFactory                {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param associationGenericService {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository  {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoAssociationServiceImpl(final DtoFactory dtoFactory,
                                     final GenericService<Association> associationGenericService,
                                     final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, associationGenericService, valueConverterRepository);
    }


    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<AssociationDTO> getDtoIFace() {
        return AssociationDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<AssociationDTOImpl> getDtoImpl() {
        return AssociationDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Association> getEntityIFace() {
        return Association.class;
    }
}
