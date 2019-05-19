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
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.BrandService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:26
 */
public class BrandImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private final BrandService brandService;
    private final AttributeService attributeService;

    public BrandImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                    final BrandService brandService,
                                                    final AttributeService attributeService) {
        super(federationFacade);
        this.brandService = brandService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN.equals(uriPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean doImageImport(final JobStatusListener statusListener, final String fileName, final String code, final String suffix, final String locale) {

        final Brand brand = brandService.findByNameOrGuid(code);
        if (brand == null) {
            statusListener.notifyWarning("brand with code {} not found.", code);
            return false;
        }

        validateAccessBeforeUpdate(brand, Brand.class);

        final String attributeCode = AttributeNamesKeys.Brand.BRAND_IMAGE_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");

        AttrValueBrand imageAttributeValue = null;
        final Collection<AttrValueBrand> attributes = brand.getAttributes();
        if (attributes != null) {
            for (AttrValueBrand attrValue : attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    imageAttributeValue = attrValue;
                    break;
                }
            }
        }
        if (imageAttributeValue == null) {
            final List<Attribute> imageAttributes = attributeService.getAvailableImageAttributesByGroupCode(AttributeGroupNames.BRAND);
            Attribute attribute = null;
            for (final Attribute imageAttribute : imageAttributes) {
                if (attributeCode.equals(imageAttribute.getCode())) {
                    attribute = imageAttribute;
                    break;
                }
            }
            if (attribute == null) {
                statusListener.notifyWarning("attribute with code {} not found.", attributeCode);
                return false;
            }
            imageAttributeValue = brandService.getGenericDao().getEntityFactory().getByIface(AttrValueBrand.class);
            imageAttributeValue.setBrand(brand);
            imageAttributeValue.setAttributeCode(attribute.getCode());
            brand.getAttributes().add(imageAttributeValue);
        }  else if (isInsertOnly()) {
            return false;
        }
        imageAttributeValue.setVal(fileName);
        imageAttributeValue.setIndexedVal(fileName);
        statusListener.notifyMessage("file {} attached as {} to brand {}", fileName, attributeCode, brand.getName());

        try {
            brandService.update(brand);
            return true;

        } catch (DataIntegrityViolationException e) {
            statusListener.notifyError("image {} for brand with name {} could not be added (db error).", e, fileName, brand.getName());
            return false;

        }

    }
}
