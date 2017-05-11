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
import org.yes.cart.web.support.service.AttributableFileService;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.*;

/**
 * User: Denis Pavlov
 */
public abstract class AbstractFileServiceImpl implements AttributableFileService {

    private final Logger LOG = LoggerFactory.getLogger(AbstractFileServiceImpl.class);

    private final Cache FILE_URI_CACHE;
    private final Cache OBJECT_FILES_CACHE;

    AbstractFileServiceImpl() {
        FILE_URI_CACHE = null;
        OBJECT_FILES_CACHE = null;
    }

    protected AbstractFileServiceImpl(final CacheManager cacheManager) {
        FILE_URI_CACHE = cacheManager.getCache("web.fileService-fileURI");
        OBJECT_FILES_CACHE = cacheManager.getCache("web.fileService-objectFiles");
    }

    /**
     * Sub classes should define which attribute key must be used for attributable.
     *
     * Default implementation takes attributable ID and converts it to String.
     *
     * NOTE: that for file service that serves more than one entity a more complex key
     * must be used
     *
     * @param attributable attributable entity
     *
     * @return additional key
     */
    protected String getEntityObjectCacheKey(Attributable attributable) {
        return String.valueOf(attributable.getId());
    }

    private String createCacheKey(final String... params) {
        return StringUtils.join(params, '-');
    }

    private <T> T getFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        return null;
    }


    /**
     * Get default file uri.
     *
     *
     * @param object             product/sku/category
     * @param servletContextPath http servlet request
     * @param locale             file locale
     * @param fileName          name of file
     * @return file uri.
     */
    public String getFileURI(final Object object,
                             final String servletContextPath,
                             final String locale,
                             final String fileName) {

        final String urlPattern = getRepositoryUrlPattern(object);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(servletContextPath);
        stringBuilder.append(urlPattern);
        try {
            stringBuilder.append(HttpUtil.encodeUtf8UriParam(fileName));
        } catch (Exception uee) {
            LOG.error(uee.getMessage(), uee);
            stringBuilder.append(Constants.NO_FILE);
        }
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
    protected String getFileAttributeValue(final Attributable attributable,
                                           final String attrName,
                                           final String defaultValue) {
        final String attrValue = attributable.getAttributeValueByCode(attrName);
        if (StringUtils.isBlank(attrValue)) {
            return defaultValue;
        }
        return attrValue;

    }


    /** {@inheritDoc} */
    public String getFile(final Attributable attributable,
                          final String httpServletContextPath,
                          final String locale,
                          final String attrName,
                          String attrVal) {

        final String urlPattern = getRepositoryUrlPattern(attributable);

        final String cacheKey = createCacheKey(getEntityObjectCacheKey(attributable), urlPattern, locale, attrName, attrVal);
        String file = getFromValueWrapper(FILE_URI_CACHE.get(cacheKey));

        if (file == null) {
            if (StringUtils.isBlank(attrVal)) {
                attrVal =  getFileAttributeValue(attributable, attrName, Constants.NO_FILE);
            }

            file = getFileURI(attributable, httpServletContextPath, locale, attrVal);
            FILE_URI_CACHE.put(cacheKey, file);
        }
        return file;
    }

    private Pair<String, String> createNoDefaultFilePair(final Attributable attributable) {

        return new Pair<String, String>(getAttributePrefix(attributable) + "0", Constants.NO_FILE);

    }

    /** {@inheritDoc} */
    public List<Pair<String, String>> getFileAttributeFileNames(final Attributable attributable, final String lang) {

        final String prefix = getAttributePrefix(attributable);
        final String cacheKey = createCacheKey(getEntityObjectCacheKey(attributable), prefix, lang);
        List<Pair<String, String>> files = getFromValueWrapper(OBJECT_FILES_CACHE.get(cacheKey));
        if (files == null) {
            files = getFileAttributeFileNamesInternal(attributable, lang, prefix);
            OBJECT_FILES_CACHE.put(cacheKey, files);
        }
        return files;
    }

    List<Pair<String, String>> getFileAttributeFileNamesInternal(final Attributable attributable, final String lang, final String prefix) {

        final Collection<AttrValue> values = attributable.getAllAttributes();
        if (CollectionUtils.isEmpty(values)) {
            return Collections.singletonList(createNoDefaultFilePair(attributable));
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
            return Collections.singletonList(createNoDefaultFilePair(attributable));
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
     * @return file repository url pattern.
     */
    protected abstract String getRepositoryUrlPattern(Object attributableOrStrategy);

    /**
     *
     * @param attributableOrStrategy to determine prefix
     *
     * @return file attribute prefix
     */
    protected abstract String getAttributePrefix(Object attributableOrStrategy);

}
