package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CountryDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCountryService;

/**
 * Country dto service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCountryServiceImpl
        extends AbstractDtoServiceImpl<CountryDTO, CountryDTOImpl, Country>
        implements DtoCountryService {

    /**
     * Construct country dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param countryGenericService    generic counry service
     * @param valueConverterRepository value converter
     */
    public DtoCountryServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<Country> countryGenericService,
                                 final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, countryGenericService, valueConverterRepository);
    }


    /**
     * {@inheritDoc}
     */
    public Class<CountryDTO> getDtoIFace() {
        return CountryDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<CountryDTOImpl> getDtoImpl() {
        return CountryDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Country> getEntityIFace() {
        return Country.class;
    }
}
