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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueSystem;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.System;
import org.yes.cart.domain.entity.impl.AttrValueEntitySystem;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SystemServiceImpl implements SystemService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemServiceImpl.class);

    private final GenericDAO<System, Long> systemDao;

    private final GenericDAO<AttrValueEntitySystem, Long> attrValueEntitySystemDao;

    private final AttributeService attributeService;

    private final RuntimeAttributeService runtimeAttributeService;

    private final Cache PREF_CACHE;

    private String imagevaultDefaultRoot = null;
    private String filevaultDefaultRoot = null;
    private String sysfilevaultDefaultRoot = null;

    private String previewShopURLTemplate = null;
    private String previewShopURICss = null;

    /**
     * Construct system services, which is determinate shop set.
     *
     * @param systemDao               system dao
     * @param attributeService        attribute service.
     * @param runtimeAttributeService runtime attribute service
     * @param cacheManager            cache manager to use
     */
    public SystemServiceImpl(final GenericDAO<System, Long> systemDao,
                             final GenericDAO<AttrValueEntitySystem, Long> attrValueEntitySystemDao,
                             final AttributeService attributeService,
                             final RuntimeAttributeService runtimeAttributeService,
                             final CacheManager cacheManager) {
        this.systemDao = systemDao;
        this.attributeService = attributeService;
        this.runtimeAttributeService = runtimeAttributeService;
        this.attrValueEntitySystemDao = attrValueEntitySystemDao;
        PREF_CACHE = cacheManager.getCache("systemService-attributeValue");
    }

    private String getStringFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (String) wrapper.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValue(final String key) {

        final String value = getStringFromValueWrapper(PREF_CACHE.get(key));
        if (value == null) {
            final Map<String, AttrValueSystem> attrs = proxy().findAttributeValues();
            preloadAttributeValues(attrs);
            AttrValue attrValue = attrs.get(key);
            if (attrValue != null) {
                return attrValue.getVal();
            }
        }
        return value;
    }

    private void preloadAttributeValues(final Map<String, AttrValueSystem> attrs) {
        for (final Map.Entry<String, AttrValueSystem> entry : attrs.entrySet()) {
            PREF_CACHE.put(entry.getKey(), entry.getValue().getVal());
            PREF_CACHE.put(entry.getValue().getAttrvalueId(), entry.getValue().getVal());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValueOrDefault(final String key, final String defaultValue) {
        final String original = getAttributeValue(key);
        if (StringUtils.isBlank(original)) {
            return defaultValue;
        }
        return original;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createOrGetAttributeValue(final String key, final String eType) {
        final String value = this.getAttributeValueOrDefault(key, null);
        if (value == null) {
            synchronized (SystemService.class) {
                final Map<String, AttrValueSystem> current = findAttributeValues();
                if (!current.containsKey(key)) {
                    runtimeAttributeService.create(key, AttributeGroupNames.SYSTEM, eType);
                }
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, AttrValueSystem> findAttributeValues() {
        final System system = getSystem();
        if (system == null) {
            return Collections.emptyMap();
        }
        return system.getAttributes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void updateAttributeValue(final String key, final String value) {

        AttrValueSystem attrVal = attrValueEntitySystemDao.findSingleByCriteria(" where e.attributeCode = ?1", key);

        if (attrVal == null) {

            LOG.debug("updating system preference {} with {} (previous value was absent)", key, value);

            final System system = getSystem();

            if (system != null) {

                Attribute attr = attributeService.findByAttributeCode(key);

                if (attr != null) {

                    attrVal = systemDao.getEntityFactory().getByIface(AttrValueSystem.class);
                    attrVal.setVal(value);
                    attrVal.setAttributeCode(attr.getCode());
                    attrVal.setSystem(system);
                    system.getAttributes().put(key, attrVal);
                } else {
                    LOG.warn("Unable to update system preference because {} attribute does not exists", key);
                }
            } else {
                LOG.error("TSYSTEM entry is not found");
            }
        } else {

            LOG.debug("updating system preference {} with {} (previous value was {})", key, value, attrVal.getVal());

            attrVal.setVal(value);
        }

        attrValueEntitySystemDao.saveOrUpdate((AttrValueEntitySystem) attrVal);
        attrValueEntitySystemDao.flushClear();

        if (attrVal != null) {
            PREF_CACHE.put(key, value);
            PREF_CACHE.put(attrVal.getAttrvalueId(), attrVal.getVal());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultShopURL() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_SHOP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPreviewShopURLTemplate() {
        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_PREVIEW_URL_TEMPLATE);
        if (StringUtils.isBlank(attrValue)) {
            if (StringUtils.isBlank(this.previewShopURLTemplate)) {
                LOG.warn("Preview shop URL template {} is not configured, using  'http://{primaryShopURL}:8080/'",
                        AttributeNamesKeys.System.SYSTEM_PREVIEW_URL_TEMPLATE);
                return "http://{primaryShopURL}:8080/";
            }
            return this.previewShopURLTemplate;
        }

        return addTailUrlSeparator(attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPreviewShopURICss() {
        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_PREVIEW_URI_CSS);
        if (StringUtils.isBlank(attrValue)) {
            if (StringUtils.isBlank(this.previewShopURICss)) {
                LOG.warn("Preview shop URI CSS {} is not configured, using 'wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css'",
                        AttributeNamesKeys.System.SYSTEM_PREVIEW_URI_CSS);
                return "wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css";
            }
            return this.previewShopURICss;
        }

        return attrValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMailResourceDirectory() {
        return addTailFileSeparator(proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_MAILTEMPLATES_FSPOINTER));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultResourceDirectory() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_FSPOINTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            if (StringUtils.isBlank(this.imagevaultDefaultRoot)) {
                throw new RuntimeException(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT
                        + " is not specified. Please specify absolute path. E.g.: file://path/to/imagevault/");
            }
            return this.imagevaultDefaultRoot;
        }

        return addTailFileSeparator(attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_FILE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            if (StringUtils.isBlank(this.filevaultDefaultRoot)) {
                throw new RuntimeException(AttributeNamesKeys.System.SYSTEM_FILE_VAULT
                        + " is not specified. Please specify absolute path. E.g.: file://path/to/filevault/");
            }
            return this.filevaultDefaultRoot;
        }

        return addTailFileSeparator(attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemFileRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_SYSFILE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            if (StringUtils.isBlank(this.sysfilevaultDefaultRoot)) {
                throw new RuntimeException(AttributeNamesKeys.System.SYSTEM_SYSFILE_VAULT
                        + " is not specified. Please specify absolute path. E.g.: file://path/to/sysfilevault/");
            }
            return this.sysfilevaultDefaultRoot;
        }

        return addTailFileSeparator(attrValue);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getEtagExpirationForImages() {
        final String expirationTimeout = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_IMAGES_TIME);
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getEtagExpirationForPages() {
        final String expirationTimeout = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_PAGES_TIME);
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }


    private String addTailFileSeparator(final String str) {
        if (!str.endsWith(File.separator)) {
            return str + File.separator;
        }
        return str;
    }

    private String addTailUrlSeparator(final String str) {
        if (!str.endsWith("/")) {
            return str + "/";
        }
        return str;
    }


    private System getSystem() {
        final List<System> sys = systemDao.findAll();
        if (sys.isEmpty()) {
            return null;
        }
        return sys.get(0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO getGenericDao() {
        return systemDao;
    }

    private SystemService proxy;

    private SystemService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy
     */
    public SystemService getSelf() {
        // Strping AOP
        return null;
    }


    public void setConfig(final Resource config) throws IOException {

        final Properties properties = new Properties();
        properties.load(config.getInputStream());

        this.imagevaultDefaultRoot = properties.getProperty("imagevault.default", null);
        this.filevaultDefaultRoot = properties.getProperty("filevault.default", null);
        this.sysfilevaultDefaultRoot = properties.getProperty("sysfilevault.default", null);

        this.previewShopURLTemplate = properties.getProperty("admin.cms.preview.shop-url-template.default", null);
        this.previewShopURICss = properties.getProperty("admin.cms.preview.shop-uri-css.default", null);

    }



}
