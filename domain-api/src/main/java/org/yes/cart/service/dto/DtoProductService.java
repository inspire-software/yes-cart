/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * Dto Wrapper for product service.
 * <p/>
 * User: dogma
 * Date: Jan 24, 2011
 * Time: 12:32:02 PM
 */
public interface DtoProductService extends GenericDTOService<ProductDTO>, GenericAttrValueService {



    /**
     * Find products by filter.
     *
     * @param filter                 filter for partial match.
     *
     * @return list of products
     */
    SearchResult<ProductDTO> findProducts(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get product sku by code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    ProductSkuDTO getProductSkuByCode(String skuCode);


    /**
     * Get list of unique supplier catalog codes that exist.
     *
     * @return list of unique codes
     */
    List<String> findProductSupplierCatalogCodes();


    /**
     * Check if URI is available to be set for given product.
     *
     * @param seoUri uri to check
     * @param productId PK or null for transient
     *
     * @return true if this URI is available
     */
    boolean isUriAvailableForProduct(String seoUri, Long productId);

    /**
     * Check if GUID is available to be set for given product.
     *
     * @param guid guid to check
     * @param productId PK or null for transient
     *
     * @return true if this GUID is available
     */
    boolean isGuidAvailableForProduct(String guid, Long productId);

    /**
     * Check if code is available to be set for given product.
     *
     * @param code code to check
     * @param productId PK or null for transient
     *
     * @return true if this code is available
     */
    boolean isCodeAvailableForProduct(String code, Long productId);

    /**
     * Check if URI is available to be set for given product SKU.
     *
     * @param seoUri uri to check
     * @param productSkuId PK or null for transient
     *
     * @return true if this URI is available
     */
    boolean isUriAvailableForProductSku(String seoUri, Long productSkuId);

    /**
     * Check if GUID is available to be set for given product SKU.
     *
     * @param guid guid to check
     * @param productSkuId PK or null for transient
     *
     * @return true if this GUID is available
     */
    boolean isGuidAvailableForProductSku(String guid, Long productSkuId);

    /**
     * Check if code is available to be set for given product SKU.
     *
     * @param code code to check
     * @param productSkuId PK or null for transient
     *
     * @return true if this code is available
     */
    boolean isCodeAvailableForProductSku(String code, Long productSkuId);

}
