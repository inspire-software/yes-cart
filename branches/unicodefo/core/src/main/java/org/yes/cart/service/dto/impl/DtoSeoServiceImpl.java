package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.SeoDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.SeoDTOImpl;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoSeoService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoSeoServiceImpl
    extends AbstractDtoServiceImpl<SeoDTO, SeoDTOImpl, Seo>
        implements DtoSeoService {


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param seoGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoSeoServiceImpl(final GenericService<Seo> seoGenericService, final DtoFactory dtoFactory) {
        super(dtoFactory, seoGenericService, null);
    }


    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<SeoDTO> getDtoIFace() {
        return SeoDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<SeoDTOImpl> getDtoImpl() {
        return SeoDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Seo> getEntityIFace() {
        return Seo.class;
    }
}
