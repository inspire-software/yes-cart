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
   * Update given shop.
   * @param voShop shop to update
   * @return updated instance
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  VoShop update(VoShop voShop) throws UnmappedInterfaceException, UnableToCreateInstanceException;

  /**
   * Create new shop
   * @param voShop given instance to persist
   * @return persisted instance
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  VoShop create(VoShop voShop) throws UnmappedInterfaceException, UnableToCreateInstanceException;

  /**
   * Get shop by id.
   * @param id
   * @return shop vo
   * @throws UnmappedInterfaceException
   * @throws UnableToCreateInstanceException
   */
  void remove(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException;

  /**
     * Fet localization information for given shop.
     * @param shopId given shop
     * @return localization information
     * @throws UnmappedInterfaceException
     * @throws UnableToCreateInstanceException
     */
  VoShopLocale getShopLocale(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
