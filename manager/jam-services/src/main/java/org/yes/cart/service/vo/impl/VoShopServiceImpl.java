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
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.domain.vo.VoShopLocale;
import org.yes.cart.domain.vo.VoShopUrl;
import org.yes.cart.domain.vo.VoShopUrlDetail;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoShopUrlService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoShopService;

import java.util.*;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

  private final DtoShopService dtoShopService;
  private final FederationFacade federationFacade;
  private final DtoShopUrlService dtoShopUrlService;

  private final Assembler simpleVoShopAssembler;
  private final Assembler simpleVoShopLocaleAssembler;
  private final Assembler simpleVoShopUrlDetailAssembler;

  /**
   * Construct service.
   * @param dtoShopService underlaying service to use.
   * @param dtoShopUrlService underlaying service to work with shop urls.
   */
  public VoShopServiceImpl(final DtoShopUrlService dtoShopUrlService,
                           final DtoShopService dtoShopService,
                           final FederationFacade federationFacade) {
    this.dtoShopUrlService = dtoShopUrlService;
    this.dtoShopService = dtoShopService;
    this.federationFacade = federationFacade;
    this.simpleVoShopAssembler = DTOAssembler.newAssembler(VoShop.class, ShopDTO.class);
    this.simpleVoShopLocaleAssembler = DTOAssembler.newAssembler(VoShopLocale.class, ShopDTO.class);
    this.simpleVoShopUrlDetailAssembler = DTOAssembler.newAssembler(VoShopUrlDetail.class, ShopUrlDTO.class);
  }

  /** {@inheritDoc} */
  public List<VoShop> getAll() throws Exception {
    final List<ShopDTO> all = dtoShopService.getAll();
    federationFacade.applyFederationFilter(all, ShopDTO.class);
    final List<VoShop> rez = new ArrayList<>(all.size());
    simpleVoShopAssembler.assembleDtos(rez, all, null,null);
    return rez;
  }

  /** {@inheritDoc} */
  public VoShop getById(long id) throws Exception {
    final ShopDTO shopDTO = dtoShopService.getById(id);
    if (federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
      final VoShop voShop = new VoShop();
      simpleVoShopAssembler.assembleDto(voShop, shopDTO, null,null);
      return voShop;
    }
    return null;
  }

  /** {@inheritDoc} */
  public VoShop update(VoShop voShop) throws Exception {
    final ShopDTO shopDTO = dtoShopService.getById(voShop.getShopId());
    if (federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
      simpleVoShopAssembler.assembleEntity(voShop, shopDTO, null, null);
      dtoShopService.update(shopDTO);
    }
    return getById(voShop.getShopId());
  }

  /** {@inheritDoc} */
  public VoShop create(VoShop voShop) throws Exception {
    ShopDTO shopDTO = dtoShopService.getNew();
    simpleVoShopAssembler.assembleEntity(voShop, shopDTO, null, null);
    shopDTO = dtoShopService.create(shopDTO);
    return getById(shopDTO.getShopId());
  }

  /** {@inheritDoc} */
  public void remove(long id) throws Exception {
    dtoShopService.remove(id);
  }

  /** {@inheritDoc} */
  public VoShopLocale getShopLocale(long shopId) throws Exception {
    final ShopDTO shopDTO = dtoShopService.getById(shopId);
    final VoShopLocale voShopLocale = new VoShopLocale();
    simpleVoShopLocaleAssembler.assembleDto(voShopLocale, shopDTO, null, null);
    voShopLocale.setDisplayTitles(adaptMapToPairs(shopDTO.getDisplayTitles().entrySet()));
    voShopLocale.setDisplayMetadescriptions(adaptMapToPairs(shopDTO.getDisplayMetadescriptions().entrySet()));
    voShopLocale.setDisplayMetakeywords(adaptMapToPairs(shopDTO.getDisplayMetakeywords().entrySet()));
    return voShopLocale;
  }

  /** {@inheritDoc} */
  public VoShopLocale update(final VoShopLocale voShopLocale) throws Exception {
    final ShopDTO shopDTO = dtoShopService.getById(voShopLocale.getShopId());
    if (federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
      simpleVoShopLocaleAssembler.assembleEntity(voShopLocale, shopDTO, null, null);
      shopDTO.setDisplayTitles(adaptPairsToMap(voShopLocale.getDisplayTitles()));
      shopDTO.setDisplayMetakeywords(adaptPairsToMap(voShopLocale.getDisplayMetakeywords()));
      shopDTO.setDisplayMetadescriptions(adaptPairsToMap(voShopLocale.getDisplayMetadescriptions()));
      dtoShopService.update(shopDTO);
    }
    return getShopLocale(voShopLocale.getShopId());
  }

  /** {@inheritDoc} */
  public VoShopUrl getShopUrls(long shopId) throws Exception {
    if (federationFacade.isShopAccessibleByCurrentManager(dtoShopService.getById(shopId).getCode())) {
      final List<ShopUrlDTO> shopUrlDTO  = dtoShopUrlService.getAllByShopId(shopId);
      final List<VoShopUrlDetail> voShopUrlDetails = new ArrayList<>(shopUrlDTO.size());
      final VoShopUrl voShopUrl = new VoShopUrl();
      simpleVoShopUrlDetailAssembler.assembleDtos(voShopUrlDetails, shopUrlDTO, null, null);
      voShopUrl.setUrls(voShopUrlDetails);
      voShopUrl.setShopId(shopId);
      return voShopUrl;
    }
    return null;
  }

  /** {@inheritDoc} */
  public VoShopUrl update(VoShopUrl voShopUrl) throws Exception {
    if (federationFacade.isShopAccessibleByCurrentManager(dtoShopService.getById(voShopUrl.getShopId()).getCode())) {
      final List<ShopUrlDTO> originalShopUrlDTOs  = dtoShopUrlService.getAllByShopId(voShopUrl.getShopId());
      for  (VoShopUrlDetail urlDetail : voShopUrl.getUrls()) {
        ShopUrlDTO shopUrlDTO = new ShopUrlDTOImpl();
        simpleVoShopUrlDetailAssembler.assembleEntity(urlDetail, shopUrlDTO, null, null);
        shopUrlDTO.setShopId(voShopUrl.getShopId());
        if (urlDetail.getUrlId() == 0) {  //new one insert
          dtoShopUrlService.create(shopUrlDTO);
        } else { //update
          dtoShopUrlService.update(shopUrlDTO);
          removeById(originalShopUrlDTOs, shopUrlDTO.getStoreUrlId());
        }
      }
      for(ShopUrlDTO dto : originalShopUrlDTOs ) {
        dtoShopUrlService.remove(dto.getId());
      }
      return getShopUrls(voShopUrl.getShopId());
    }
    return null;
  }

  private void removeById(List<ShopUrlDTO> originalShopUrlDTOs, long storeUrlId) {
    for (ShopUrlDTO dto : originalShopUrlDTOs) {
      if (dto.getStoreUrlId() == storeUrlId) {
        originalShopUrlDTOs.remove(dto);
        break;
      }
    }
  }


  private List<MutablePair<String, String>> adaptMapToPairs(Set<Map.Entry<String, String>> es) {
    List<MutablePair<String, String>> rez = new ArrayList<MutablePair<String, String>>(es.size());
    for(Map.Entry ent : es) {
      rez.add(MutablePair.of(ent.getKey(), ent.getValue()));
    }
    return rez;
  }

  private Map<String, String> adaptPairsToMap(List<MutablePair<String, String>> pairs) {
    Map<String, String> map = new HashMap<>(pairs.size());
    for (MutablePair<String, String> pair : pairs) {
      map.put(pair.getFirst(), pair.getSecond());
    }
    return map;
  }


}
