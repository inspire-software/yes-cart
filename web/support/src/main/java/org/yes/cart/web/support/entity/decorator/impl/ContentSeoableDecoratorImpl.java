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

package org.yes.cart.web.support.entity.decorator.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/04/2019
 * Time: 11:33
 */
public class ContentSeoableDecoratorImpl extends AbstractSeoableDecoratorImpl<Content> {

    public ContentSeoableDecoratorImpl(final Content content, final String lang) {
        super(new AbstractSeoableDecoratorImpl.AbstractFailoverSeo<Content>(content) {
            @Override
            protected String getTitle(final Content seoable) {
                return seoable.getName();
            }

            @Override
            protected String getDisplayTitle(final Content seoable) {
                return seoable.getDisplayName();
            }

            @Override
            protected String getMetadescription(final Content seoable) {
                return seoable.getDescription();
            }

            @Override
            protected String getDisplayMetadescription(final Content seoable) {

                final String av = seoable.getAttributeValueByCode(AttributeNamesKeys.Category.CATEGORY_DESCRIPTION_PREFIX + lang);

                if (StringUtils.isNotBlank(av)) {
                    final Map<String, String> values = new HashMap<>();
                    values.put(lang, removeTags(av));
                    return new StringI18NModel(values).toString();
                }
                return null;
            }
        });
    }
}
