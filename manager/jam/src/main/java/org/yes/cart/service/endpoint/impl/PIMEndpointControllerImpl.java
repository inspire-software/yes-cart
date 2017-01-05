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

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.bulkjob.impl.BulkJobAutoContextImpl;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.endpoint.PIMEndpointController;
import org.yes.cart.service.vo.VoProductService;
import org.yes.cart.web.service.ws.client.AsyncContextFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 27/09/2016
 * Time: 09:35
 */
@Component
public class PIMEndpointControllerImpl implements PIMEndpointController {

    private final VoProductService voProductService;
    private final ClusterService clusterService;
    private final AsyncContextFactory asyncContextFactory;
    private final NodeService nodeService;

    @Autowired
    public PIMEndpointControllerImpl(final VoProductService voProductService,
                                     final ClusterService clusterService,
                                     final AsyncContextFactory asyncContextFactory,
                                     final NodeService nodeService) {
        this.voProductService = voProductService;
        this.clusterService = clusterService;
        this.asyncContextFactory = asyncContextFactory;
        this.nodeService = nodeService;
    }

    @Override
    public @ResponseBody
    List<VoAssociation> getAllAssociations() throws Exception {
        return voProductService.getAllAssociations();
    }

    @Override
    public @ResponseBody
    List<VoProduct> getFilteredProducts(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
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
        clusterService.reindexProduct(createJobContext(), product.getProductId());
        return product;
    }

    @Override
    public @ResponseBody
    VoProductWithLinks updateProduct(@RequestBody final VoProductWithLinks vo) throws Exception {
        final VoProductWithLinks product = voProductService.updateProduct(vo);
        clusterService.reindexProduct(createJobContext(), product.getProductId());
        return product;
    }

    @Override
    public @ResponseBody
    void removeProduct(@PathVariable("id") final long id) throws Exception {
        voProductService.removeProduct(id);
        clusterService.reindexProduct(createJobContext(), id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProduct> getProductAttributes(@PathVariable("productId") final long productId) throws Exception {
        return voProductService.getProductAttributes(productId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProduct> updateProduct(@RequestBody final List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception {
        final List<VoAttrValueProduct> list = voProductService.updateProduct(vo);
        if (!list.isEmpty()) {
            clusterService.reindexProduct(createJobContext(), list.get(0).getProductId());
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
    List<VoProductSku> getFilteredProductSkus(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
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
        clusterService.reindexProductSku(createJobContext(), sku.getSkuId());
        return sku;
    }

    @Override
    public @ResponseBody
    VoProductSku updateSku(@RequestBody final VoProductSku vo) throws Exception {
        final VoProductSku sku = voProductService.updateSku(vo);
        clusterService.reindexProductSku(createJobContext(), sku.getSkuId());
        return sku;
    }

    @Override
    public @ResponseBody
    void removeSku(@PathVariable("id") final long id) throws Exception {
        voProductService.removeSku(id);
        clusterService.reindexProductSku(createJobContext(), id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProductSku> getSkuAttributes(@PathVariable("skuId") final long skuId) throws Exception {
        return voProductService.getSkuAttributes(skuId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueProductSku> updateSku(@RequestBody final List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception {
        final List<VoAttrValueProductSku> list = voProductService.updateSku(vo);
        if (!list.isEmpty()) {
            clusterService.reindexProductSku(createJobContext(), list.get(0).getSkuId());
        }
        return list;
    }


    private JobContext createJobContext() {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.TIMEOUT_KEY, AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS);
        param.put(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE, new HashMap<String, Boolean>());
        param.putAll(createAuthCtx().getAttributes());

        // Max char of report to UI since it will get huge and simply will crash the UI, not to mention traffic cost.
        final int logSize = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE), 100);
        // Timeout - just in case runnable crashes and we need to unlock through timeout.
        final int timeout = NumberUtils.toInt(nodeService.getConfiguration().get(AttributeNamesKeys.System.SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS), 100);

        return new JobContextImpl(true, new JobStatusListenerImpl(logSize, timeout), param);
    }

    private AsyncContext createAuthCtx() {

        final Map<String, Object> param = new HashMap<>();

        try {
            // This is manual access via Admin
            return asyncContextFactory.getInstance(param);
        } catch (IllegalStateException exp) {
            // This is auto access with thread local
            return new BulkJobAutoContextImpl(param);
        }
    }


}
