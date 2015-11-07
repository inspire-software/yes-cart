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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 08:12
 */
public class CentralViewResolverContentImpl implements CentralViewResolver {

    private static final Pair<String, String> DEFAULT = new Pair<String, String>(CentralViewLabel.CONTENT, CentralViewLabel.CONTENT);
    private static final Pair<String, String> DEFAULT_DYNO = new Pair<String, String>(CentralViewLabel.DYNOCONTENT, CentralViewLabel.DYNOCONTENT);

    private final ContentService contentService;

    public CentralViewResolverContentImpl(final ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Resolve content view if applicable.
     * <p>
     * Rules:<p>
     * 1. If there is no {@link WebParametersKeys#CONTENT_ID} then this resolver is not applicable, return null<p>
     * 2. If content has template then use template.<p>
     * 3. Otherwise use {@link CentralViewLabel#CATEGORY}<p>
     *
     * @param parameters            request parameters map
     *
     * @return content view label or null (if not applicable)
     */
    @Override
    public Pair<String, String> resolveMainPanelRendererLabel(final Map parameters) {
        if (parameters.containsKey(WebParametersKeys.CONTENT_ID)) {
            final long contentId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.CONTENT_ID)));
            if (contentId > 0L) {
                final String template = contentService.getContentTemplate(contentId);
                if (StringUtils.isNotBlank(template)) {
                    if (CentralViewLabel.DYNOCONTENT.equals(template)) {
                        return DEFAULT_DYNO;
                    }
                    return new Pair<String, String>(template, CentralViewLabel.CONTENT);
                }
                return DEFAULT;
            }
        }
        return null;
    }
}
