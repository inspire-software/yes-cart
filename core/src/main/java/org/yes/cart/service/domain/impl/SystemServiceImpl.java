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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueSystem;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.System;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SystemServiceImpl implements SystemService {

    private final GenericDAO<System, Long> systemDao;

    private final AttributeService attributeService;

    /**
     * Construct system services, which is determinate shop set.
     * @param systemDao system dao
     * @param attributeService attribute service.
     */
    public SystemServiceImpl(
            final GenericDAO<System, Long> systemDao,
            final AttributeService attributeService) {
        this.systemDao = systemDao;
        this.attributeService = attributeService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "systemService-attributeValue")
    public String getAttributeValue(final String key) {
        return getAttributeValue(key, getSystem().getAttributes());
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValueOrDefault(final String key, final String defaultValue) {
        final String original = proxy().getAttributeValue(key);
        if (StringUtils.isBlank(original)) {
            return defaultValue;
        }
        return original;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, AttrValueSystem> getAttributeValues() {
        return getSystem().getAttributes();
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value ={
            "systemService-attributeValue"
    }, allEntries = true)
    public void updateAttributeValue(final String key, final String value) {

        AttrValueSystem attrVal = getSystem().getAttributes().get(key);

        final System system = getSystem();
        if (attrVal == null) {
            Attribute attr = attributeService.findByAttributeCode(key);
            if (attr != null) {

                attrVal = systemDao.getEntityFactory().getByIface(AttrValueSystem.class);
                attrVal.setVal(value);
                attrVal.setAttribute(attr);
                attrVal.setSystem(system);
                system.getAttributes().put(key, attrVal);
            }
        } else {
            attrVal.setVal(value);
        }
        systemDao.saveOrUpdate(system);
    }

    /**
     * Is Google checkout enabled.
     * @return    true if google checkout enabled.
     */
    public boolean isGoogleCheckoutEnabled() {
        final String allGws = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL);
        return StringUtils.isNotBlank(allGws) &&  allGws.contains("googleCheckoutPaymentGatewayLabel");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultShopURL() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_SHOP);
    }

    /**
     * {@inheritDoc}
     */
    public String getMailResourceDirectory() {
        return addTailFileSeparator(proxy().getAttributeValue(AttributeNamesKeys.SYSTEM_MAILTEMPLATES_FSPOINTER));
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultResourceDirectory() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_FSPOINTER);
    }

    /**
     * {@inheritDoc}
     */
    public String getImageRepositoryDirectory() {
        return addTailFileSeparator(
                proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT));
    }



    /**
     * {@inheritDoc}
     */
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


    private System getSystem() {
        return systemDao.findAll().get(0);
    }


    /**
     * Get the value of attribute from attribute value map.
     *
     * @param attrName attribute name
     * @param values   map of attribute name and {@link AttrValue}
     * @return null if attribute not present in map, otherwise value of attribute
     */
    public static String getAttributeValue(final String attrName, final Map<String, AttrValueSystem> values) {
        AttrValue attrValue = values.get(attrName);
        if (attrValue != null) {
            return attrValue.getVal();
        }
        return null;
    }

    /**
     * Get attribute value
     *
     * @param attrName   attribute name
     * @param attributes collection of attribute
     * @return value if fount otherwise null
     */
    public static String getAttributeValue(final String attrName, final Collection<? extends AttrValueSystem> attributes) {
        for (AttrValue attrValue : attributes) {
            if (attrName.equals(attrValue.getAttribute().getName())) {
                return attrValue.getVal();
            }
        }
        return null;
    }

    /** {@inheritDoc} */
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


}
