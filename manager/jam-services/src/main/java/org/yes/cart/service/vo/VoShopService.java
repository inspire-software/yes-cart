package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public interface VoShopService {

  /**
   * Get all manageble shops.
   * @return list of all manageble shops.
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  List<VoShop> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
