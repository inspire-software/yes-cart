package org.yes.cart.service.vo.impl;

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;

import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.domain.vo.VoShopLocale;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoShopService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

  private final DtoShopService dtoShopService;
  private final FederationFacade federationFacade;

  private final Assembler simpleVoShopAssembler;

  /**
   * Construct service.
   * @param dtoShopService underlaying service to use.
   */
  public VoShopServiceImpl(final DtoShopService dtoShopService, final FederationFacade federationFacade) {
    this.dtoShopService = dtoShopService;
    this.federationFacade = federationFacade;
    this.simpleVoShopAssembler = DTOAssembler.newAssembler(VoShop.class, ShopDTO.class);
  }

  /** {@inheritDoc} */
  public List<VoShop> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
    final List<ShopDTO> all = dtoShopService.getAll();
    federationFacade.applyFederationFilter(all, ShopDTO.class);
    final List<VoShop> rez = new ArrayList<>(all.size());
    simpleVoShopAssembler.assembleDtos(rez, all, null,null);
    return rez;
  }

  /** {@inheritDoc} */
  public VoShop getById(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    final ShopDTO shopDTO = dtoShopService.getById(id);
    if (federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
      final VoShop voShop = new VoShop();
      simpleVoShopAssembler.assembleDto(voShop, shopDTO, null,null);
      return voShop;
    }
    return null;
  }

  /** {@inheritDoc} */
  public VoShopLocale getShopLocale(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    final ShopDTO shopDTO = dtoShopService.getById(shopId);
    final VoShopLocale voShopLocale = new VoShopLocale();
    simpleVoShopAssembler.assembleDto(voShopLocale, shopDTO, null, null);
    return voShopLocale;
  }
}
