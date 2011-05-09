package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.StateDTOImpl;
import org.yes.cart.domain.entity.State;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.service.dto.DtoStateService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoStateServiceImpl extends AbstractDtoServiceImpl<StateDTO, StateDTOImpl, State>
        implements DtoStateService {


    /**
     * Construct state dto service.
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param stateGenericService generic serivce
     * @param valueConverterRepository     value converter
     */
    public DtoStateServiceImpl(final DtoFactory dtoFactory,
                               final GenericService<State> stateGenericService,
                               final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, stateGenericService, valueConverterRepository);
    }

    /** {@inheritDoc} */
    public StateDTO create(final StateDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        State state = getEntityFactory().getByIface(State.class);
        assembler.assembleEntity(instance, state, null, dtoFactory);
        state = service.create(state);
        return getById(state.getStateId());
    }


    /** {@inheritDoc} */
    public StateDTO update(final StateDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        State state = service.getById(instance.getStateId());
        assembler.assembleEntity(instance, state, null, dtoFactory);
        state = service.update(state);
        return getById(state.getStateId());

    }

    /** {@inheritDoc} */
    public Class<StateDTO> getDtoIFace() {
        return StateDTO.class;
    }

    /** {@inheritDoc} */
    public Class<StateDTOImpl> getDtoImpl() {
        return StateDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<State> getEntityIFace() {
        return State.class;
    }

    /** {@inheritDoc} */
    public List<StateDTO> findByCountry(final String countryCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<State> states = ((StateService)service).findByCountry(countryCode);
        return getDTOs(states);
    }
}
