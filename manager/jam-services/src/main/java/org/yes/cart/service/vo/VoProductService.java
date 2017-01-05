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

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoProductService {

    /**
     * Get all association types.
     *
     * @return association types
     */
    List<VoAssociation> getAllAssociations() throws Exception;

    /**
     * Get all products in the system, filtered by criteria and according to rights, up to max
     * @return list of products
     * @throws Exception
     */
    List<VoProduct> getFilteredProducts(String filter, int max) throws Exception;

    /**
     * Get product by id.
     *
     * @param id product id
     * @return product vo
     * @throws Exception
     */
    VoProductWithLinks getProductById(long id) throws Exception;

    /**
     * Update given product.
     *
     * @param vo product to update
     * @return updated instance
     * @throws Exception
     */
    VoProductWithLinks updateProduct(VoProductWithLinks vo) throws Exception;

    /**
     * Create new product
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoProductWithLinks createProduct(VoProduct vo) throws Exception;

    /**
     * Remove product by id.
     *
     * @param id product id
     * @throws Exception
     */
    void removeProduct(long id) throws Exception;


    /**
     * Get supported attributes by given product
     * @param productId given product id
     * @return attributes
     * @throws Exception
     */
    List<VoAttrValueProduct> getProductAttributes(long productId) throws Exception;


    /**
     * Update the product attributes.
     *
     * @param vo product attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return product attributes.
     * @throws Exception
     */
    List<VoAttrValueProduct> updateProduct(List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception;



    /**
     * Get all products in the system, filtered by criteria and according to rights, up to max
     * @return list of products
     * @throws Exception
     */
    List<VoProductSku> getProductSkuAll(long productId) throws Exception;


    /**
     * Get all products in the system, filtered by criteria and according to rights, up to max
     * @return list of products
     * @throws Exception
     */
    List<VoProductSku> getFilteredProductSkus(String filter, int max) throws Exception;

    /**
     * Get product by id.
     *
     * @param id product id
     * @return product vo
     * @throws Exception
     */
    VoProductSku getSkuById(long id) throws Exception;

    /**
     * Update given product.
     *
     * @param vo product to update
     * @return updated instance
     * @throws Exception
     */
    VoProductSku updateSku(VoProductSku vo) throws Exception;

    /**
     * Create new product
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoProductSku createSku(VoProductSku vo) throws Exception;

    /**
     * Remove product by id.
     *
     * @param id product id
     * @throws Exception
     */
    void removeSku(long id) throws Exception;


    /**
     * Get supported attributes by given product
     * @param productId given product id
     * @return attributes
     * @throws Exception
     */
    List<VoAttrValueProductSku> getSkuAttributes(long productId) throws Exception;


    /**
     * Update the product attributes.
     *
     * @param vo product attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return product attributes.
     * @throws Exception
     */
    List<VoAttrValueProductSku> updateSku(List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception;


}
