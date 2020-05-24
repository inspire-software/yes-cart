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

package org.yes.cart.bulkimport.image.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:44
 */
public class ProductSkuImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private static final String MERGE_COUNTER = "Images upserted";
    private static final String SKIP_COUNTER = "Images skipped";

    private final ProductSkuService productSkuService;

    private final AttributeService attributeService;

    public ProductSkuImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                         final ProductSkuService productSkuService,
                                                         final AttributeService attributeService) {
        super(federationFacade);
        this.productSkuService = productSkuService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return  Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN.equals(uriPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean doImageImport(final JobStatusListener statusListener, final String fileName, final String code, final String suffix, final String locale) {

        final ProductSku productSku = productSkuService.findProductSkuBySkuCode(code);

        if (productSku == null) {
            statusListener.notifyWarning("product sku with code {} not found.", code);
            statusListener.count(SKIP_COUNTER);
            return false;
        }

        validateAccessBeforeUpdate(productSku, ProductSku.class);

        final String attributeCode = AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");
        AttrValueProductSku imageAttributeValue = (AttrValueProductSku) productSku.getAttributeByCode(attributeCode);
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
            imageAttributeValue = productSkuService.getGenericDao().getEntityFactory().getByIface(AttrValueProductSku.class);
            imageAttributeValue.setProductSku(productSku);
            imageAttributeValue.setAttributeCode(attribute.getCode());
            productSku.getAttributes().add(imageAttributeValue);
        } else if (isInsertOnly()) {
            statusListener.count(SKIP_COUNTER);
            return false;
        }
        imageAttributeValue.setVal(fileName);
        imageAttributeValue.setIndexedVal(fileName);
        statusListener.notifyMessage("file {} attached as {} to product sku {}",
                fileName,
                attributeCode,
                productSku.getCode());

        try {
            productSkuService.update(productSku);
            statusListener.count(MERGE_COUNTER);
            return true;
        } catch (DataIntegrityViolationException e) {
            statusListener.notifyError("image {} for product sku with code {} could not be added (db error).", e, fileName, productSku.getCode());
            statusListener.count(SKIP_COUNTER);
            return false;
        }
    }
}
