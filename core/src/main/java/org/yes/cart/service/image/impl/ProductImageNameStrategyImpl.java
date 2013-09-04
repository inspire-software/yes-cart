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

package org.yes.cart.service.image.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.impl.AttrValueEntityProduct;
import org.yes.cart.domain.entity.impl.AttrValueEntityProductSku;


/**
 * Handle both product and product sku image url to code resolving.
 * Product or SKU Code resolving will use two ways:
 * 1. Fast if url has the underscored code, example (.*)_CODE_(.*).imageextension
 * 2. In case if first way failed code will be resolved via attr values.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductImageNameStrategyImpl extends AbstractImageNameStrategyImpl {

    private final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao;
    private final GenericDAO<AttrValueEntityProductSku, Long> attrValueEntityProductSkuDao;


    /**
     * Construct image name strategy.
     *
     * @param attrValueEntityProductSkuDao product sku attributes  dao
     * @param attrValueEntityProductDao    product attributes dao
     */
    public ProductImageNameStrategyImpl(
            final GenericDAO<AttrValueEntityProductSku, Long> attrValueEntityProductSkuDao,
            final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao) {
        this.attrValueEntityProductSkuDao = attrValueEntityProductSkuDao;
        this.attrValueEntityProductDao = attrValueEntityProductDao;
    }

    /**
     * {@inheritDoc}
     */
    protected String getPathPrefix() {
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productImageNameStrategyService-productCode")
    public String getCode(final String url) {

        if (StringUtils.isNotBlank(url)) {

            if (url.indexOf('_') > -1) {
                final String[] nameParts = url.split("_");
                return nameParts[nameParts.length - 2];
            }

            final String val = getFileNameWithoutPrefix(url);

            final String productCode =
                    attrValueEntityProductDao.findSingleByNamedQuery("PRODUCT.CODE.BY.IMAGE.NAME", val);
            if (productCode != null) {
                return productCode;
            }

            final String skuCode =
                    attrValueEntityProductSkuDao.findSingleByNamedQuery("SKU.CODE.BY.IMAGE.NAME", val);
            if (skuCode != null) {
                return skuCode;
            }


        }


        return Constants.NO_IMAGE;
    }

}
