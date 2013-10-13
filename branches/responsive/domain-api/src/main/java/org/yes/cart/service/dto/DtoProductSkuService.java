/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoProductSkuService extends GenericDTOService<ProductSkuDTO>, GenericAttrValueService {

    /**
     * Get all product SKUs.
     *
     * @param productId product id
     * @return list of product skus.
     */
    List<ProductSkuDTO> getAllProductSkus(long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create sku price.
     *
     * @param skuPriceDTO to create in database
     * @return created price sku pk value
     */
    long createSkuPrice(SkuPriceDTO skuPriceDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update sku price.
     *
     * @param skuPriceDTO to create in database
     * @return update price sku pk value
     */
    long updateSkuPrice(SkuPriceDTO skuPriceDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Delete sku price by given pk
     *
     * @param skuPriceId pk value.
     */
    SkuPriceDTO getSkuPrice(long skuPriceId);

    /**
     * Delete sku price by given pk
     *
     * @param skuPriceId pk value.
     */
    void removeSkuPrice(long skuPriceId);

     /**
     * Remove all sku prices from all shops.
     * @param productId product pk value
     */
    void removeAllPrices(long productId);

    /**
     * Remove from all warehouses.
     * @param productId product pk value
     */
    void removeAllItems(long productId);

}
