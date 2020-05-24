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
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: denispavlov
 * Date: 22/09/2017
 * Time: 23:28
 */
public class RemoveObsoleteProductProcessorImpl implements RemoveObsoleteProductProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(RemoveObsoleteProductProcessorImpl.class);

    private static final String UNAVAILABLE_COUNTER = "Obsolete products";
    private static final String REMOVED_COUNTER = "Removed products";

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao;
    private final ProductSkuService productSkuService;
    private final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao;
    private final GenericDAO<ProductAssociation, Long> productAssociationDao;
    private final SkuWarehouseService skuWarehouseService;
    private final SystemService systemService;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG, "Remove obsolete product", true);

    public RemoveObsoleteProductProcessorImpl(final ProductService productService,
                                              final ProductCategoryService productCategoryService,
                                              final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao,
                                              final ProductSkuService productSkuService,
                                              final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao,
                                              final GenericDAO<ProductAssociation, Long> productAssociationDao,
                                              final SkuWarehouseService skuWarehouseService,
                                              final SystemService systemService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.attrValueEntityProductDao = attrValueEntityProductDao;
        this.productSkuService = productSkuService;
        this.attrValueEntityProductSkuDao = attrValueEntityProductSkuDao;
        this.productAssociationDao = productAssociationDao;
        this.skuWarehouseService = skuWarehouseService;
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

        listener.notifyMessage("Remove obsolete products unavailable before {} (min days {}), batch: {}",
                time, minDays, batchSize);

        final List<String> potentialObsoleteSku = skuWarehouseService.findProductSkuByUnavailableBefore(time);

        LOG.info("Potentially obsolete SKU count: {}", potentialObsoleteSku.size());

        listener.count(UNAVAILABLE_COUNTER, potentialObsoleteSku.size());

        for (final String skuCode : potentialObsoleteSku) {
            if (self().removeProductSkuIfInventoryDisabledSince(skuCode, time)) {
                int count = listener.count(REMOVED_COUNTER);
                if (count >= batchSize) {
                    break; // Do not remove more than necessary
                }
            }
        }

        listener.notifyCompleted();
        listener.reset();

    }

    @Override
    public boolean removeProductSkuIfInventoryDisabledSince(final String skuCode, final LocalDateTime time) {

        final List<SkuWarehouse> allInventory = skuWarehouseService.findByCriteria(" where e.skuCode = ?1", skuCode);

        final Iterator<SkuWarehouse> allIterator = allInventory.iterator();
        while (allIterator.hasNext()) {

            final SkuWarehouse inventory = allIterator.next();

            if (/* if we have no reservations AND */
                !MoneyUtils.isPositive(inventory.getReserved()) &&
                    (/* if not available for a long time */
                    (inventory.getAvailableto() != null && !inventory.isAvailable(time))
                    ||
                    /* or if disabled and was not updated for a long time */
                    (inventory.getAvailableto() == null && inventory.isDisabled() &&
                        inventory.getUpdatedTimestamp() == null || inventory.getUpdatedTimestamp().isBefore(DateUtils.iFrom(time)))
                    )
                ) {

                LOG.warn("Removing obsolete inventory record for {} in fulfilment centre {}", skuCode, inventory.getWarehouse().getWarehouseId());

                skuWarehouseService.delete(inventory);
                allIterator.remove();

            }

        }

        if (!allInventory.isEmpty()) {
            LOG.warn("Keeping SKU {} as inventory records exist for other fulfilment centres {}", skuCode, allInventory.size());
            return false; // we still have valid inventory for this SKU
        }

        Product product = productService.getProductBySkuCode(skuCode);
        if (product == null) {
            LOG.warn("Product for SKU {} not found", skuCode);
            return false;
        }

        final long productId = product.getProductId();
        final String productCode = product.getCode();

        // Find fresh record
        product = productService.findById(productId);

        ProductSku sku = product.getSku(skuCode);
        if (sku == null) {
            LOG.warn("SKU {} not found", skuCode);
            return false;
        }

        final boolean multiSku = product.isMultiSkuProduct();

        LOG.info("Remove obsolete product SKU {}/{}/{}", product.getProductId(), product.getCode(), skuCode);

        productSkuService.removeAllInventory(sku);
        productSkuService.removeAllPrices(sku);
        productSkuService.removeAllWishLists(sku);
        productSkuService.removeAllOptions(sku);

        final List<Long> sAvIds = new ArrayList<>();
        for (final AttrValueProductSku av : sku.getAttributes()) {
            sAvIds.add(av.getAttrvalueId());
        }

        long skuId = sku.getSkuId();
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

        LOG.warn("SKU {} removed as it is obsolete", skuCode);

        if (multiSku) {
            return true;
        }

        productCategoryService.removeByProductIds(productId);
        productSkuService.removeAllOptions(productId);

        product = productService.findById(productId);

        LOG.info("Remove obsolete product {}/{}", product.getProductId(), product.getCode());

        final List<Long> pAvIds = new ArrayList<>();
        for (final AttrValueProduct av : product.getAttributes()) {
            pAvIds.add(av.getAttrvalueId());
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

        productService.delete(productService.findById(productId));

        LOG.warn("Product {} removed as it is obsolete", productCode);

        return true;
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
