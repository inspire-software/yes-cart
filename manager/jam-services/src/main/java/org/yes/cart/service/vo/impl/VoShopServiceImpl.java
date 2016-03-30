/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
  public VoShop update(VoShop voShop) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    final ShopDTO shopDTO = dtoShopService.getById(voShop.getShopId());
    if (federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
      simpleVoShopAssembler.assembleEntity(voShop, shopDTO, null, null);
      dtoShopService.update(shopDTO);
    }
    return getById(voShop.getShopId());
  }

  /** {@inheritDoc} */
  public VoShop create(VoShop voShop) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    ShopDTO shopDTO = dtoShopService.getNew();
    simpleVoShopAssembler.assembleEntity(voShop, shopDTO, null, null);
    shopDTO = dtoShopService.create(shopDTO);
    return getById(shopDTO.getShopId());
  }

  /** {@inheritDoc} */
  public VoShopLocale getShopLocale(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    final ShopDTO shopDTO = dtoShopService.getById(shopId);
    final VoShopLocale voShopLocale = new VoShopLocale();
    simpleVoShopAssembler.assembleDto(voShopLocale, shopDTO, null, null);
    return voShopLocale;
  }

  /** {@inheritDoc} */
  public void remove(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
    dtoShopService.remove(id);
  }

}
