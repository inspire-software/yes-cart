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

import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.service.misc.LanguageService;


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

    private final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao;
    private final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao;


    /**
     * Construct image name strategy.
     *
     * @param relativeInternalRootDirectory  internal image relative path root directory without {@see File#separator}. E.g. "product"
     * @param attrValueProductSkuDao         product sku attributes  dao
     * @param attrValueProductDao            product attributes dao
     * @param languageService                language service
     */
    public ProductImageNameStrategyImpl(final String relativeInternalRootDirectory,
                                        final GenericDAO<AttrValueProductSku, Long> attrValueProductSkuDao,
                                        final GenericDAO<AttrValueProduct, Long> attrValueProductDao,
                                        final LanguageService languageService) {
        super(Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, relativeInternalRootDirectory, languageService);
        this.attrValueEntityProductSkuDao = attrValueProductSkuDao;
        this.attrValueEntityProductDao = attrValueProductDao;
    }

    /**
     * {@inheritDoc}
     */
    protected String resolveObjectCodeInternal(final String url) {

        final String val = resolveFileName(url);

        final String productCode =
                attrValueEntityProductDao.findSingleByNamedQuery("PRODUCT.CODE.BY.IMAGE.NAME", val);
        if (productCode != null) {
            return productCode;
        }

        return attrValueEntityProductSkuDao.findSingleByNamedQuery("SKU.CODE.BY.IMAGE.NAME", val);

    }

}
