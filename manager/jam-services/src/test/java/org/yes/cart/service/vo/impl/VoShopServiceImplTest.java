package org.yes.cart.service.vo.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoShopService;

import java.util.ArrayList;
import java.util.Collection;
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
    final VoShopService voShopService = new VoShopServiceImpl(dss, ff);
    List<VoShop> voShops = voShopService.getAll();
    assertEquals(1, voShops.size());
    context.assertIsSatisfied();
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