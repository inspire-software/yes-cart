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

package org.yes.cart.service.domain;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueSystem;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface SystemService {

    /**
     * Get etag expiration in minutes for images.
     *
     * @return etag expiration in minutes if configured, otherwise default value 0 will be returned.
     */
    Integer getEtagExpirationForImages();

    /**
     * Get etag expiration in minutes for pages.
     *
     * @return etag expiration in minutes if configured, otherwise default value 0 will be returned.
     */
    Integer getEtagExpirationForPages();

    /**
     * Get attribute value
     *
     * @param key key
     *
     * @return value if found, otherwise is null
     */

    String getAttributeValue(String key);

    /**
     * Get attribute value
     *
     * @param key key
     * @param defaultValue default value if null or empty
     *
     * @return value if found, otherwise is null
     */

    String getAttributeValueOrDefault(String key, String defaultValue);

    /**
     * Get all system attributes.
     *
     * @return system attributes
     */
    Map<String, AttrValueSystem> findAttributeValues();

    /**
     * Get all system attributes.
     *
     * @return system attributes
     */
    Map<String, AttrValueSystem> getAttributeValues();

    /**
     * Need to know default shop URL, in case if shop can not be resolved by domain name.
     *
     * @return URL of default shop
     */
    String getDefaultShopURL();

    /**
     * Get the mail templates directory.
     * Mail template directory has following structure:
     * <p/>
     * /mail/template/directory
     * <p/>
     * /mail/template/directory/shopCode1/templatename - has two templates text and html
     * /mail/template/directory/shopCode1/templatename/resources has resources to inline into html template     *
     * /mail/template/directory/shopCode2/templatename - has two templates text and html
     * /mail/template/directory/shopCode2/templatename/resources has resources to inline into html template
     *
     * @return mail templates directory with tail File.separator
     */
    String getMailResourceDirectory();

    /**
     * Get default (failover) directory.
     *
     * @return absolute path to default directory.
     */
    String getDefaultResourceDirectory();

    /**
     * Get the image repository directory
     *
     * @return path to image repository directory.
     */
    String getImageRepositoryDirectory();


    /**
     * Is Google checkout enabled.
     * @return    true if google checkout enabled.
     */
    boolean isGoogleCheckoutEnabled();


    /**
     * set system attribute value. This method will create new one , in case if it not exists or
     * just update
     * @param key attribute key
     * @param value attribute value
     */
    void updateAttributeValue(String key, String value);

    /**
     * Get generic dao
     *
     * @return generic dao
     */
    GenericDAO getGenericDao();


}
