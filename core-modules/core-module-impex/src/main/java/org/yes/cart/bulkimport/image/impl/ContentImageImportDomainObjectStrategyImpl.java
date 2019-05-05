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
import org.yes.cart.domain.entity.AttrValueContent;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.federation.FederationFacade;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 16:14
 */
public class ContentImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private final ContentService contentService;
    private final AttributeService attributeService;

    public ContentImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                      final ContentService contentService,
                                                      final AttributeService attributeService) {
        super(federationFacade);
        this.contentService = contentService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return Constants.CONTENT_IMAGE_REPOSITORY_URL_PATTERN.equals(uriPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean doImageImport(final JobStatusListener statusListener, final String fileName, final String code, final String suffix, final String locale) {

        final Content content = contentService.findContentIdBySeoUriOrGuid(code);
        if (content == null) {
            final String warn = MessageFormat.format("content with code {0} not found.", code);
            statusListener.notifyWarning(warn);
            return false;
        }

        validateAccessBeforeUpdate(content, Content.class);

        final String attributeCode = AttributeNamesKeys.Category.CATEGORY_IMAGE_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");
        AttrValueContent imageAttributeValue = (AttrValueContent) content.getAttributeByCode(attributeCode);
        if (imageAttributeValue == null) {
            final List<Attribute> imageAttributes = attributeService.getAvailableImageAttributesByGroupCode(AttributeGroupNames.CATEGORY);
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
                return false;
            }
            imageAttributeValue = contentService.getGenericDao().getEntityFactory().getByIface(AttrValueContent.class);
            imageAttributeValue.setContent(content);
            imageAttributeValue.setAttributeCode(attribute.getCode());
            content.getAttributes().add(imageAttributeValue);
        } else if (isInsertOnly()) {
            return false;
        }
        imageAttributeValue.setVal(fileName);
        imageAttributeValue.setIndexedVal(fileName);
        final String info = MessageFormat.format("file {0} attached as {1} to content {2}", fileName, attributeCode, content.getName());
        statusListener.notifyMessage(info);

        try {
            contentService.update(content);
            return true;

        } catch (DataIntegrityViolationException e) {
            final String err = MessageFormat.format("image {0} for content with code {1} could not be added (db error).", fileName, content.getGuid());
            statusListener.notifyError(err, e);
            return false;

        }

    }
}
