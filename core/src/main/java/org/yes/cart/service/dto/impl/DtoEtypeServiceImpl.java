package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.EtypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.EtypeDTOImpl;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.dto.DtoEtypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoEtypeServiceImpl
        extends AbstractDtoServiceImpl<EtypeDTO, EtypeDTOImpl, Etype>
        implements DtoEtypeService {


    /**
     * Constrict remote service.
     * @param etypeService {@link EtypeService}
     * @param dtoFactory {@link DtoFactory}
     */
    public DtoEtypeServiceImpl(final EtypeService etypeService,
                                  final DtoFactory dtoFactory) {
        super(dtoFactory, etypeService, null);
    }

    /**
     * {@inheritDoc}
     */
    public EtypeDTO create(final EtypeDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Etype etype = getEntityFactory().getByIface(Etype.class);
        assembler.assembleEntity(instance, etype,  null, dtoFactory);
        etype = service.create(etype);
        return getById(etype.getEtypeId());
    }

    /**
     * {@inheritDoc}
     */
    public EtypeDTO update(final EtypeDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Etype etype = service.getById(instance.getEtypeId());
        assembler.assembleEntity(instance, etype,  null, dtoFactory);
        etype = service.update(etype);
        return getById(etype.getEtypeId());
    }

    /** {@inheritDoc} */
    public Class<EtypeDTO> getDtoIFace() {
        return EtypeDTO.class;
    }

    /** {@inheritDoc} */
    public Class<EtypeDTOImpl> getDtoImpl() {
        return EtypeDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Etype> getEntityIFace() {
        return Etype.class;
    }

}
