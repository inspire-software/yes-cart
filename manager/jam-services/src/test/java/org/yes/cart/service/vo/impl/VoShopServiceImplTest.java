/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoShopService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by iazarnyi on 1/20/16.
 */
public class VoShopServiceImplTest {

  private final Mockery context = new JUnit4Mockery();

  @Test
  public void testGetAll() throws Exception {
    final FederationFacade ff = new TestJamFederationFacadeImpl();
    final DtoShopService dss = context.mock(DtoShopService.class, "dss");
    context.checking(new Expectations() {{
      allowing(dss).getAll(); will(returnValue(createShops()));
    }});
    final VoShopService voShopService = new VoShopServiceImpl(null, null, null, dss, ff);
    List<VoShop> voShops = voShopService.getAll();
    assertEquals(1, voShops.size());
    context.assertIsSatisfied();
  }

  @Test
  public void testGetById() throws Exception {
    final FederationFacade ff = new TestJamFederationFacadeImpl();
    final DtoShopService dss = context.mock(DtoShopService.class);
    context.checking(new Expectations() {{
      allowing(dss).getById(100); will(returnValue(createShop(100)));
    }});
    final VoShopService voShopService = new VoShopServiceImpl(null, null, null, dss, ff);
    VoShop voShop = voShopService.getById(100);
    assertEquals("shopname_100", voShop.getName());
    assertEquals("shopdescr_100", voShop.getDescription());
    assertEquals("JEWEL_SHOP", voShop.getCode());
    context.assertIsSatisfied();
  }


  @Test
  public void testUpdate() throws Exception {
    final ShopDTO shopDTOOriginal = createShop(100);
    final FederationFacade ff = new TestJamFederationFacadeImpl();
    final DtoShopService dss = context.mock(DtoShopService.class);
    context.checking(new Expectations() {{
      allowing(dss).getById(100); will(returnValue(shopDTOOriginal));
      allowing(dss).update(shopDTOOriginal); will(returnValue(shopDTOOriginal));
      allowing(dss).getById(100); will(returnValue(shopDTOOriginal));
    }});
    final VoShopService voShopService = new VoShopServiceImpl(null, null, null, dss, ff);
    VoShop voShop = voShopService.getById(100);
    voShop.setName("new name");
    voShop = voShopService.update(voShop);
    assertEquals("new name", voShop.getName());
    context.assertIsSatisfied();
  }

  @Test
  public void testCreate() throws Exception {
    final ShopDTO shopDTOOriginal = createShop(0);
    final FederationFacade ff = new TestJamFederationFacadeImpl();
    final DtoShopService dss = context.mock(DtoShopService.class);
    context.checking(new Expectations() {{
      allowing(dss).getNew(); will(returnValue(shopDTOOriginal));
      allowing(dss).create(shopDTOOriginal); will(returnValue(shopDTOOriginal));
      allowing(dss).getById(with(any(int.class))); will(returnValue(shopDTOOriginal));
    }});
    final VoShopService voShopService = new VoShopServiceImpl(null, null, null, dss, ff);
    voShopService.create(new VoShop());
    context.assertIsSatisfied();
  }



  private ShopDTO createShop(long id) {
    final ShopDTO shop = new ShopDTOImpl();
    shop.setShopId(id);
    shop.setName("shopname_" + id);
    shop.setDescription("shopdescr_" + id);
    shop.setCode("JEWEL_SHOP");
    return shop;
  }

  private List<ShopDTO> createShops() {
    final ShopDTO first = new ShopDTOImpl();
    final ShopDTO second = new ShopDTOImpl();
    fillShopDto(first, 1L);
    fillShopDto(second, 2L);
    return new ArrayList<ShopDTO>() {{
      add(first);
      add(second);
    }};
  }

  private void fillShopDto(ShopDTO first, long id) {
    first.setShopId(id);
    first.setCode("code" + id);
    first.setName("name" + id);
    first.setDescription("descr" + id);
  }

}