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

package org.yes.cart.domain.message;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Registration message. Used for user or customer notification in case of registration
 * or password reset
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RegistrationMessage extends Serializable {

    /**
     * Email or the person registered.
     *
     * @return email
     */
    String getEmail();

    /**
     * Email or the person registered.
     *
     * @param email email
     */
    void setEmail(String email);

    /**
     * Registration shop PK.
     *
     * @return shop PK
     */
    long getShopId();

    /**
     * Registration shop PK.
     *
     * @param shopId shop PK
     */
    void setShopId(long shopId);

    /**
     * Locale used when registering.
     *
     * @return locale
     */
    String getLocale();

    /**
     * Locale used when registering.
     *
     * @param locale locale
     */
    void setLocale(String locale);

    /**
     * Password for new account.
     *
     * @return password
     */
    String getPassword();

    /**
     * Password for new account.
     *
     * @param password password
     */
    void setPassword(String password);

    /**
     * Get shop mail from addr.
     *
     * @return shop mail from addr.
     */
    String getShopMailFrom();

    /**
     * Set shop mail from addr.
     *
     * @param shopMailFrom shop mail from addr.
     */
    void setShopMailFrom(String shopMailFrom);


    /**
     * Get template name.
     * @return  template name.
     */
    String getTemplateName();

    /**
     * Set  template name.
     * @param templateName  template name.
     */
    void setTemplateName(String templateName);


    /**
     * Get path to template folder.
     * Example /some/path/shop/mailtemplates/ must hold folders with concrete templates
     *
     * @return path to template folder.
     */
    List<String> getMailTemplatePathChain();

    /**
     * Set path to template folder.
     *
     * @param mailTemplatePathChain  path to template folder.
     */
    public void setMailTemplatePathChain(List<String> mailTemplatePathChain);


    /**
     * Get shop code.
     *
     * @return shop code
     */
    String getShopCode();

    /**
     * Set sho pcode.
     *
     * @param shopCode shop code.
     */
    void setShopCode(String shopCode);

    /**
     * Get shop name.
     *
     * @return sho pname.
     */
    String getShopName();

    /**
     * Set shop name.
     *
     * @param shopName shop name
     */
    void setShopName(String shopName);

    /**
     * Get shop urls.
     *
     * @return shop urls.
     */
    Set<String> getShopUrl();

    /**
     * Set shop urls.
     *
     * @param shopUrl shop urls.
     */
    void setShopUrl(Set<String> shopUrl);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

}
