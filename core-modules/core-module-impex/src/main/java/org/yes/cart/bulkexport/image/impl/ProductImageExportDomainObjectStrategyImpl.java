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

package org.yes.cart.bulkexport.image.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.federation.FederationFacade;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/11/2015
 * Time: 21:52
 */
public class ProductImageExportDomainObjectStrategyImpl extends AbstractImageExportDomainObjectStrategyImpl<Product> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImageExportDomainObjectStrategyImpl.class);

    private final GenericDAO<Product, Long> productDao;


    public ProductImageExportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                      final GenericDAO<Product, Long> productDao,
                                                      final ImageService imageService,
                                                      final SystemService systemService) {
        super(federationFacade, imageService, systemService);
        this.productDao = productDao;
    }

    /** {@inheritDoc} */
    @Override
    protected Set<String> getAllObjectImages(final Product next) {

        final Set<String> images = new HashSet<String>();

        try {
            validateAccessBeforeUpdate(next, Product.class);

            final Set<AttrValueProduct> attrs = next.getAttributes();
            addAttributeValues(images, (Collection) attrs);
            if (CollectionUtils.isNotEmpty(next.getSku())) {
                for (final ProductSku sku : next.getSku()) {
                    final Collection<AttrValueProductSku> attrsSku = sku.getAttributes();
                    addAttributeValues(images, (Collection) attrsSku);
                }
            }
        } catch (AccessDeniedException ade) {

            String message = MessageFormat.format(
                    "Access denied during export image for product : {0}",
                    next.getProductId()
            );
            LOG.debug(message, ade);  // debug only, no error
            return null;

        }

        return images;
    }

    private void addAttributeValues(final Set<String> images,
                                    final Collection<AttrValue> attrs) {
        if (CollectionUtils.isNotEmpty(attrs)) {
            for (final AttrValue attr : attrs) {
                final String code = attr.getAttribute().getCode();
                if (code.startsWith(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX)
                        && StringUtils.isNotBlank(attr.getVal())) {
                    images.add(attr.getVal());
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected ResultsIterator<Product> getAllObjects() {
        return productDao.findAllIterator();
    }

    /** {@inheritDoc} */
    @Override
    protected String getImageRepositoryUrlPattern() {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN;
    }

}
