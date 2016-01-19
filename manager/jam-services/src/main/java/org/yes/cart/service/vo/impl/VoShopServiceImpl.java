package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl {

  private final DtoShopService dtoShopService;
  private final FederationFacade federationFacade;

  /**
   * Construct service.
   * @param dtoShopService underlaying service to use.
   */
  public VoShopServiceImpl(final DtoShopService dtoShopService, final FederationFacade federationFacade) {
    this.dtoShopService = dtoShopService;
    this.federationFacade = federationFacade;
  }

  public List<VoShop> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {

    List<ShopDTO> all = dtoShopService.getAll();
    federationFacade.applyFederationFilter(all, ShopDTO.class);
    return null;

  }



}
