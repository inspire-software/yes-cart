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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:44 PM
 */
public abstract class AbstractImageServiceImpl implements AttributableImageService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImageServiceImpl.class);

    private final Cache IMAGE_URI_CACHE;
    private final Cache OBJECT_IMAGES_CACHE;

    AbstractImageServiceImpl() {
        IMAGE_URI_CACHE = null;
        OBJECT_IMAGES_CACHE = null;
    }

    protected AbstractImageServiceImpl(final CacheManager cacheManager) {
        IMAGE_URI_CACHE = cacheManager.getCache("web.imageService-imageURI");
        OBJECT_IMAGES_CACHE = cacheManager.getCache("web.imageService-objectImages");
    }


    private Integer createCacheHash(final String ... params) {
        final int prime = 31;
        int result = 1;
        for( String param : params) {
            if (param == null) {
                continue;
            }
            result = result * prime + param.hashCode();
        }
        return result;
    }

    private <T> T getFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        return null;
    }


    /**
     * Get default image uri.
     *
     *
     * @param object             product/sku/category
     * @param servletContextPath http servlet request
     * @param locale             image locale
     * @param width              image width
     * @param height             image height
     * @param imageName          name of image    @return image uri.
     */
    public String getImageURI(final Object object,
                              final String servletContextPath,
                              final String locale,
                              final String width,
                              final String height,
                              final String imageName) {

        final String urlPattern = getImageRepositoryUrlPattern(object);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(servletContextPath);
        stringBuilder.append(urlPattern);
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
     *
     * @param attributable given attributable.
     * @param attrName  attribute name
     *
     * @return attribute value if found, otherwise defaultValue will be returned.
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
    public String getImage(final Attributable attributable,
                           final String httpServletContextPath,
                           final String locale,
                           final String width,
                           final String height,
                           final String attrName,
                           String attrVal) {

        final String urlPattern = getImageRepositoryUrlPattern(attributable);

        final Integer hash = createCacheHash(String.valueOf(attributable.getId()), urlPattern, locale, width, height, attrName, attrVal);
        String image = getFromValueWrapper(IMAGE_URI_CACHE.get(hash));

        if (image == null) {
            if (StringUtils.isBlank(attrVal)) {
                attrVal =  getImageAttributeValue(attributable, attrName, Constants.NO_IMAGE);
            }

            image = getImageURI(attributable, httpServletContextPath, locale, width, height, attrVal);
            IMAGE_URI_CACHE.put(hash, image);
        }
        return image;
    }

    private Pair<String, String> createNoDefaultImagePair(final Attributable attributable) {

        return new Pair<String, String>(getImageAttributePrefix(attributable) + "0", Constants.NO_IMAGE);

    }

    /** {@inheritDoc} */
    public List<Pair<String, String>> getImageAttributeFileNames(final Attributable attributable, final String lang) {

        final String prefix = getImageAttributePrefix(attributable);
        final Integer hash = createCacheHash(String.valueOf(attributable.getId()), prefix, lang);
        List<Pair<String, String>> images = getFromValueWrapper(OBJECT_IMAGES_CACHE.get(hash));
        if (images == null) {
            images = getImageAttributeFileNamesInternal(attributable, lang, prefix);
            OBJECT_IMAGES_CACHE.put(hash, images);
        }
        return images;
    }

    List<Pair<String, String>> getImageAttributeFileNamesInternal(final Attributable attributable, final String lang, final String prefix) {

        final Collection<AttrValue> values = attributable.getAllAttributes();
        if (CollectionUtils.isEmpty(values)) {
            return Collections.singletonList(createNoDefaultImagePair(attributable));
        }
        final Map<String, String> attrToFileMap = new TreeMap<String, String>(); // sort naturally
        for (final AttrValue av : values) {
            final String code = av.getAttribute().getCode();
            if (code.startsWith(prefix) && StringUtils.isNotBlank(av.getVal())) {
                if (code.endsWith(lang)) {
                    // put value for this language and remove possible default values
                    attrToFileMap.put(code, av.getVal());
                    attrToFileMap.remove(code.substring(0, code.length() - lang.length() - 1));
                } else {
                    final char lastChar = code.charAt(code.length() - 1);
                    if (lastChar >= '0' && lastChar <= '9' && !attrToFileMap.containsKey(code + "_" + lang)) {
                        // put default value only if we do not have language specific one
                        attrToFileMap.put(code, av.getVal());
                    }
                }
            }
        }

        if (attrToFileMap.isEmpty()) {
            return Collections.singletonList(createNoDefaultImagePair(attributable));
        }

        final List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>(attrToFileMap.size());
        for (final Map.Entry<String, String> entry : attrToFileMap.entrySet()) {
            pairs.add(new Pair<String, String>(entry.getKey(), entry.getValue()));
        }
        return Collections.unmodifiableList(pairs);
    }

    /**
     * @param attributableOrStrategy to determine pattern
     *
     * @return image repository url pattern.
     */
    protected abstract String getImageRepositoryUrlPattern(Object attributableOrStrategy);

    /**
     *
     * @param attributableOrStrategy to determine prefix
     *
     * @return image attribute prefix
     */
    protected abstract String getImageAttributePrefix(Object attributableOrStrategy);

}
