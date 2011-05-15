package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.SeoDTO;
import org.yes.cart.service.dto.DtoSeoService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteSeoServiceImpl extends AbstractRemoteService<SeoDTO> implements DtoSeoService {

    /**
     * Construct remote service.
     *
     * @param dtoSeoService dto seo service
     */
    public RemoteSeoServiceImpl(final DtoSeoService dtoSeoService) {
        super(dtoSeoService);
    }

}
