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
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.federation.FederationFacade;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:44
 */
public class ProductSkuImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesBulkImportServiceImpl.class);

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

        final ProductSku productSku = productSkuService.getProductSkuBySkuCode(code);

        if (productSku == null) {
            final String warn = MessageFormat.format("product sku with code {0} not found.", code);
            statusListener.notifyWarning(warn);
            LOG.warn(warn);
            return false;
        }

        validateAccessBeforeUpdate(productSku, ProductSku.class);

        final String attributeCode = AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");
        AttrValueProductSku imageAttibute = (AttrValueProductSku) productSku.getAttributeByCode(attributeCode);
        if (imageAttibute == null) {
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
            imageAttibute = productSkuService.getGenericDao().getEntityFactory().getByIface(AttrValueProductSku.class);
            imageAttibute.setProductSku(productSku);
            imageAttibute.setAttribute(attribute);
            productSku.getAttributes().add(imageAttibute);
        }
        imageAttibute.setVal(fileName);
        final String info = MessageFormat.format("file {0} attached as {1} to product sku {2}",
                fileName,
                attributeCode,
                productSku.getCode());
        statusListener.notifyMessage(info);
        LOG.info(info);

        try {
            productSkuService.update(productSku);
            return true;
        } catch (DataIntegrityViolationException e) {
            final String err = MessageFormat.format("image {0} for product sku with code {1} could not be added (db error).", fileName, productSku.getCode());
            LOG.error(err, e);
            statusListener.notifyError(err);
            return false;
        }
    }
}
