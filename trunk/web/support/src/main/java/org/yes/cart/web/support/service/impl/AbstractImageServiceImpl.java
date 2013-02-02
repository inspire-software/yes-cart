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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:44 PM
 */
public abstract class AbstractImageServiceImpl implements AttributableImageService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImageServiceImpl.class);

    /**
     * Get default image uri.
     *
     * @param imageName          name of image
     * @param width              image width
     * @param height             image height
     * @param servletContextPath http servlet request
     * @param object             product/sku/category
     * @return image uri.
     */
    protected String getImageURI(final String imageName,
                              final String width,
                              final String height,
                              final String servletContextPath,
                              final Object object) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(servletContextPath);
        stringBuilder.append(getImageRepositoryUrlPattern(object));
        try {
            stringBuilder.append(URLEncoder.encode(imageName, "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            LOG.error(uee.getMessage(), uee);
            stringBuilder.append(Constants.NO_IMAGE);
        }
        stringBuilder.append('?');
        stringBuilder.append(WebParametersKeys.WIDTH);
        stringBuilder.append('=');
        stringBuilder.append(width);
        stringBuilder.append('&');
        stringBuilder.append(WebParametersKeys.HEIGHT);
        stringBuilder.append('=');
        stringBuilder.append(height);
        return stringBuilder.toString();
    }


    /**
     * Get attribute value.
     * @param attributable given attributable.
     * @param attrName  attribute name
     * @return attribute value if found, otherwise noimage will be returned.
     */
    protected String getImageAttributeValue(final Attributable attributable,
                                            final String attrName,
                                            final String defaultValue) {
        final AttrValue attrValue = attributable.getAttributeByCode(attrName);
        if (attrValue == null || StringUtils.isBlank(attrValue.getVal())) {
            return defaultValue;
        }
        return attrValue.getVal();

    }


    /** {@inheritDoc} */
    public String getImage(final Attributable attributable, final String httpServletContextPath,
                           final String width, final String height, final String attrName, String attrVal) {

        if (StringUtils.isBlank(attrVal)) {
            attrVal =  getImageAttributeValue(attributable, attrName, Constants.NO_IMAGE);
        }

        return getImageURI(attrVal, width, height, httpServletContextPath, attributable);
    }


    /**
     *
     * @return image repository url pattern.
     * @param object to determinate url pattern
     */
    public abstract String getImageRepositoryUrlPattern(Object object);


}
