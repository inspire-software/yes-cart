/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.bulkjob.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductCategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 22/09/2017
 * Time: 23:28
 */
public class RemoveObsoleteProductProcessorImpl implements RemoveObsoleteProductProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveObsoleteProductProcessorImpl.class);

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao;
    private final ProductSkuService productSkuService;
    private final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao;
    private final GenericDAO<ProductAssociation, Long> productAssociationDao;
    private final SystemService systemService;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG);

    public RemoveObsoleteProductProcessorImpl(final ProductService productService,
                                              final ProductCategoryService productCategoryService,
                                              final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao,
                                              final ProductSkuService productSkuService,
                                              final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao,
                                              final GenericDAO<ProductAssociation, Long> productAssociationDao,
                                              final SystemService systemService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.attrValueEntityProductDao = attrValueEntityProductDao;
        this.productSkuService = productSkuService;
        this.attrValueEntityProductSkuDao = attrValueEntityProductSkuDao;
        this.productAssociationDao = productAssociationDao;
        this.systemService = systemService;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        final int minDays = getObsoleteMinDays();
        final LocalDateTime time = now().plusDays(-minDays);

        final int batchSize = getObsoleteBatchSize();

        LOG.info("Remove obsolete products unavailable before {} (min days {}), batch: {}",
                time, minDays, batchSize);

        final List<Long> obsoleteIds = productService.findProductIdsByUnavailableBefore(time);

        int toIndex = batchSize > obsoleteIds.size() ? obsoleteIds.size() : batchSize;
        final List<Long> batch = new ArrayList<>(obsoleteIds.subList(0, toIndex));

        LOG.info("Remove obsolete products {}", batch);

        for (final Long id : batch) {
            self().removeProduct(id);
        }

        listener.notifyPing("Removed " + toIndex + " obsolete products in last run");

    }

    @Override
    public void removeProduct(final Long productId) {

        productCategoryService.removeByProductIds(productId);

        Product product = productService.findById(productId);

        if (product == null) {
            return;
        }

        LOG.info("Remove obsolete product {}/{}", product.getProductId(), product.getCode());

        final List<Long> pAvIds = new ArrayList<>();
        for (final AttrValueProduct av : product.getAttributes()) {
            pAvIds.add(av.getAttrvalueId());
        }
        final List<Long> skus = new ArrayList<>();
        for (final ProductSku sku : product.getSku()) {
            skus.add(sku.getSkuId());
        }
        final List<Long> assoc = new ArrayList<>();
        for (final ProductAssociation productAssociation : product.getProductAssociations()) {
            assoc.add(productAssociation.getProductassociationId());
        }
        product = null;
        productService.getGenericDao().clear(); // clear session

        for (final Long avId : pAvIds) {
            attrValueEntityProductDao.delete(attrValueEntityProductDao.findById(avId));
        }

        productService.getGenericDao().flushClear(); // ensure we flush delete and clear session

        for (final Long aid : assoc) {
            productAssociationDao.delete(productAssociationDao.findById(aid));
        }

        productService.getGenericDao().flushClear(); // ensure we flush delete and clear session

        for (final Long skuId : skus) {
            ProductSku sku =  productSkuService.findById(skuId);

            if (sku == null) {
                return;
            }

            productSkuService.removeAllInventory(sku);
            productSkuService.removeAllPrices(sku);
            productSkuService.removeAllWishLists(sku);
            productSkuService.removeAllEnsembleOptions(sku);

            final List<Long> sAvIds = new ArrayList<>();
            for (final AttrValueProductSku av : sku.getAttributes()) {
                sAvIds.add(av.getAttrvalueId());
            }
            sku = null;
            productSkuService.getGenericDao().clear(); // clear session

            for (final Long avId : sAvIds) {
                attrValueEntityProductSkuDao.delete(attrValueEntityProductSkuDao.findById(avId));
            }
            productSkuService.getGenericDao().flushClear(); // ensure we flush delete and clear session

            sku = productSkuService.findById(skuId); // get sku again (should be without attributes)
            final Product prod = sku.getProduct();
            prod.getSku().remove(sku);
            sku.setProduct(null);
            productSkuService.delete(sku);
        }

        productSkuService.getGenericDao().flushClear(); // ensure we flush delete and clear session

        productService.delete(productService.findById(productId));

    }

    protected int getObsoleteMinDays() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_PRODUCT_OBSOLETE_MAX_DAYS), 365);
    }

    protected int getObsoleteBatchSize() {
        return NumberUtils.toInt(systemService.getAttributeValue(AttributeNamesKeys.System.JOB_PRODUCT_OBSOLETE_BATCH_SIZE), 500);
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }


    private RemoveObsoleteProductProcessorInternal self;

    private RemoveObsoleteProductProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public RemoveObsoleteProductProcessorInternal getSelf() {
        return null;
    }


}
