package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by iazarnyi on 1/20/16.
 */
public class TestJamFederationFacadeImpl implements FederationFacade {

  /**
   * {@inheritDoc}
   */
  public boolean isCurrentUserSystemAdmin() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isShopAccessibleByCurrentManager(final String shopCode) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public Set<Long> getAccessibleShopIdsByCurrentManager() {
    return new HashSet<Long>(Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L));
  }

  /**
   * {@inheritDoc}
   */
  public Set<String> getAccessibleShopCodesByCurrentManager() {
    return new HashSet<String>(Arrays.asList("SHOIP1", "SHOIP2", "SHOIP3", "SHOIP4", "SHOIP5", "JEWEL_SHOP"));
  }

  /**
   * {@inheritDoc}
   */
  public List<ShopDTO> getAccessibleShopsByCurrentManager() {
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public void applyFederationFilter(final Collection list, final Class objectType) {
    if (objectType == ShopDTO.class) {
      Iterator iter = list.iterator();
      while(iter.hasNext()) {
        ShopDTO shopDTO = (ShopDTO) iter.next();
        if (shopDTO.getShopId() % 2 == 0) {
          iter.remove();
        }
      }
      return;
    }
    throw new UnsupportedOperationException("Import can only work with individual objects");
  }

  /**
   * {@inheritDoc}
   */
  public boolean isManageable(final Object object, final Class objectType) {
    return true;
  }

}
