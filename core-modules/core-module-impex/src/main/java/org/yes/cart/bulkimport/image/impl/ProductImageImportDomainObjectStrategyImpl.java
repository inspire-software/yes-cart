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

package org.yes.cart.bulkimport.image.impl;

import org.apache.commons.lang.StringUtils;
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

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:26
 */
public class ProductImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private static final String MERGE_COUNTER = "Images upserted";
    private static final String SKIP_COUNTER = "Images skipped";

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

        Product product = null;

        final Long id = productService.findProductIdByCode(code);

        if (id != null) {
            product = productService.getProductById(id, true);
        }

        if (product == null) {

            final Product productBySku = productService.getProductBySkuCode(code);
            if (productBySku != null) {
                return false; // SKU specific
            }

            statusListener.notifyWarning("product with code {} not found.", code);
            statusListener.count(SKIP_COUNTER);
            return false;
        }

        validateAccessBeforeUpdate(product, Product.class);

        final Product productWithAttrs = product;
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
                statusListener.notifyWarning("attribute with code {} not found.", attributeCode);
                statusListener.count(SKIP_COUNTER);
                return false;
            }
            imageAttributeValue = productService.getGenericDao().getEntityFactory().getByIface(AttrValueProduct.class);
            imageAttributeValue.setProduct(productWithAttrs);
            imageAttributeValue.setAttributeCode(attribute.getCode());
            productWithAttrs.getAttributes().add(imageAttributeValue);
        } else if (isInsertOnly()) {
            statusListener.count(SKIP_COUNTER);
            return false;
        }
        imageAttributeValue.setVal(fileName);
        imageAttributeValue.setIndexedVal(fileName);
        statusListener.notifyMessage("file {} attached as {} to product {}", fileName, attributeCode, productWithAttrs.getCode());

        try {
            productService.update(productWithAttrs);
            statusListener.count(MERGE_COUNTER);
            return true;

        } catch (DataIntegrityViolationException e) {
            statusListener.notifyError("image {} for product with code {} could not be added (db error).", e, fileName, product.getCode());
            statusListener.count(SKIP_COUNTER);
            return false;

        }

    }
}
