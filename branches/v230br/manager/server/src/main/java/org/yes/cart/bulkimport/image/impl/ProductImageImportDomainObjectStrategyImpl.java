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

package org.yes.cart.bulkimport.image.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.federation.FederationFacade;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:26
 */
public class ProductImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesBulkImportServiceImpl.class);

    private final ProductService productService;
    private final AttributeService attributeService;

    public ProductImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                      final ProductService productService,
                                                      final AttributeService attributeService) {
        super(federationFacade);
        this.productService = productService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN.equals(uriPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean doImageImport(final JobStatusListener statusListener, final String fileName, final String code, final String suffix, final String locale) {

        final Product product = productService.getProductBySkuCode(code);
        if (product == null) {
            final String warn = MessageFormat.format("product with code {0} not found.", code);
            statusListener.notifyWarning(warn);
            LOG.warn(warn);
            return false;
        }

        validateAccessBeforeUpdate(product, Product.class);

        final Product productWithAttrs = productService.getProductById(product.getProductId(), true);
        final String attributeCode = AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");
        AttrValueProduct imageAttributeValue = (AttrValueProduct) productWithAttrs.getAttributeByCode(attributeCode);
        if (imageAttributeValue == null) {
            final List<Attribute> imageAttributes = attributeService.getAvailableImageAttributesByGroupCode(AttributeGroupNames.PRODUCT);
            Attribute attribute = null;
            for (final Attribute imageAttribute : imageAttributes) {
                if (attributeCode.equals(imageAttribute.getCode())) {
                    attribute = imageAttribute;
                    break;
                }
            }
            if (attribute == null) {
                final String warn = MessageFormat.format("attribute with code {0} not found.", attributeCode);
                statusListener.notifyWarning(warn);
                LOG.warn(warn);
                return false;
            }
            imageAttributeValue = productService.getGenericDao().getEntityFactory().getByIface(AttrValueProduct.class);
            imageAttributeValue.setProduct(productWithAttrs);
            imageAttributeValue.setAttribute(attribute);
            productWithAttrs.getAttributes().add(imageAttributeValue);
        }
        imageAttributeValue.setVal(fileName);
        final String info = MessageFormat.format("file {0} attached as {1} to product {2}", fileName, attributeCode, productWithAttrs.getCode());
        statusListener.notifyMessage(info);

        LOG.info(info);
        try {
            productService.update(productWithAttrs);
            return true;

        } catch (DataIntegrityViolationException e) {
            final String err = MessageFormat.format("image {0} for product with code {1} could not be added (db error).", fileName, product.getCode());
            LOG.error(err, e);
            statusListener.notifyError(err);
            return false;

        }

    }
}
