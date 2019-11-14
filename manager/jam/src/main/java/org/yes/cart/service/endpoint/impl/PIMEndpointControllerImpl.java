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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.cluster.ProductAsyncSupport;
import org.yes.cart.service.endpoint.PIMEndpointController;
import org.yes.cart.service.vo.VoProductService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/09/2016
 * Time: 09:35
 */
@Component
public class PIMEndpointControllerImpl implements PIMEndpointController {

    private final VoProductService voProductService;
    private final ProductAsyncSupport productAsyncSupport;


    @Autowired
    public PIMEndpointControllerImpl(final VoProductService voProductService,
                                     final ProductAsyncSupport productAsyncSupport) {
        this.voProductService = voProductService;
        this.productAsyncSupport = productAsyncSupport;
    }

    @Override
    public @ResponseBody
    List<VoAssociation> getAllAssociations() throws Exception {
        return voProductService.getAllAssociations();
    }

    @Override
    public @ResponseBody
    List<VoProduct> getFilteredProducts(@RequestBody(required = false) final String filter, @PathVariable("max") final int max) throws Exception {
        return voProductService.getFilteredProducts(filter, max);
    }

    @Override
    public @ResponseBody
    VoProductWithLinks getProductById(@PathVariable("id") final long id) throws Exception {
        return voProductService.getProductById(id);
    }

    @Override
    public @ResponseBody
    VoProductWithLinks createProduct(@RequestBody final VoProduct vo) throws Exception {
        final VoProductWithLinks product = voProductService.createProduct(vo);
        productAsyncSupport.asyncIndexProduct(product.getProductId());
        return product;
    }

    @Override
    public @ResponseBody
    VoProductWithLinks updateProduct(@RequestBody final VoProductWithLinks vo) throws Exception {
        final VoProductWithLinks product = voProductService.updateProduct(vo);
        productAsyncSupport.asyncIndexProduct(product.getProductId());
        return product;
    }

    @Override
    public @ResponseBody
    void removeProduct(@PathVariable("id") final long id) throws Exception {
        final VoProduct product = voProductService.getProductById(id);
        if (product != null) {
            voProductService.removeProduct(id);
            productAsyncSupport.asyncIndexProduct(product.getProductId());
        }
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProduct> getProductAttributes(@PathVariable("productId") final long productId) throws Exception {
        return voProductService.getProductAttributes(productId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProduct> updateProduct(@RequestBody final List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception {
        final List<VoAttrValueProduct> list = voProductService.updateProductAttributes(vo);
        if (!list.isEmpty()) {
            productAsyncSupport.asyncIndexProduct(list.get(0).getProductId());
        }
        return list;
    }

    @Override
    public @ResponseBody
    List<VoProductSku> getProductSkuAll(@PathVariable("productId") final long productId) throws Exception {
        return voProductService.getProductSkuAll(productId);
    }

    @Override
    public @ResponseBody
    List<VoProductSku> getFilteredProductSkus(@RequestBody(required = false) final String filter, @PathVariable("max") final int max) throws Exception {
        return voProductService.getFilteredProductSkus(filter, max);
    }

    @Override
    public @ResponseBody
    VoProductSku getSkuById(@PathVariable("id") final long id) throws Exception {
        return voProductService.getSkuById(id);
    }

    @Override
    public @ResponseBody
    VoProductSku createSku(@RequestBody final VoProductSku vo) throws Exception {
        final VoProductSku sku = voProductService.createSku(vo);
        productAsyncSupport.asyncIndexSku(sku.getSkuId());
        return sku;
    }

    @Override
    public @ResponseBody
    VoProductSku updateSku(@RequestBody final VoProductSku vo) throws Exception {
        final VoProductSku sku = voProductService.updateSku(vo);
        productAsyncSupport.asyncIndexSku(sku.getSkuId());
        return sku;
    }

    @Override
    public @ResponseBody
    void removeSku(@PathVariable("id") final long id) throws Exception {
        final VoProductSku sku = voProductService.getSkuById(id);
        if (sku != null) {
            voProductService.removeSku(id);
            productAsyncSupport.asyncIndexSku(sku.getSkuId());
        }
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProductSku> getSkuAttributes(@PathVariable("skuId") final long skuId) throws Exception {
        return voProductService.getSkuAttributes(skuId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProductSku> updateSku(@RequestBody final List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception {
        final List<VoAttrValueProductSku> list = voProductService.updateSkuAttributes(vo);
        if (!list.isEmpty()) {
            productAsyncSupport.asyncIndexSku(list.get(0).getSkuId());
        }
        return list;
    }

}
