package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.domain.vo.VoShopLocale;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public interface VoShopService {

  /**
   * Get all manageable shops.
   * @return list of all manageble shops.
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  List<VoShop> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException;

  /**
   * Get shop by id.
   * @param id
   * @return shop vo
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
     */
  VoShop getById(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException;

  /**
   * Fet localization information for given shop.
   * @param shopId given shop
   * @return localization information
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  VoShopLocale getShopLocale(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
