/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.domain.entity;

import org.yes.cart.domain.i18n.I18NModel;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:38
 */
public interface Content extends Auditable, Attributable, Rankable, Nameable, Seoable  {

    /**
     * Get content pk value.
     *
     * @return content pk value
     */
    long getContentId();

    /**
     * Set content pk value.
     *
     * @param contentId pk value
     */
    void setContentId(long contentId);

    /**
     * Get parent pk value.
     *
     * @return parent pk value.
     */
    long getParentId();

    /**
     * Set parent pk value.
     *
     * @param parentId parent pk value.
     */
    void setParentId(long parentId);

    /**
     * display name.
     *
     * @return display name.
     */
    I18NModel getDisplayName();

    /**
     * Get display name
     *
     * @param name display name
     */
    void setDisplayName(I18NModel name);

    /**
     * Get content UI template variation.
     *
     * @return content UI template variation.
     */
    String getUitemplate();

    /**
     * Set content UI template variation.
     *
     * @param uitemplate template variation.
     */
    void setUitemplate(String uitemplate);

    /**
     * Flag to denote if object is disabled on not.
     *
     * @return true if object is disabled
     */
    boolean isDisabled();

    /**
     * Flag to denote if object is disabled on not.
     *
     * @param disabled true if object is disabled
     */
    void setDisabled(boolean disabled);

    /**
     * Get available from date.  Null value means no start.
     *
     * @return available from date.
     */
    LocalDateTime getAvailablefrom();

    /**
     * Set available from date.
     *
     * @param availablefrom available from date.
     */
    void setAvailablefrom(LocalDateTime availablefrom);

    /**
     * Get available to date.  Null value means no end date.
     *
     * @return available to date.
     */
    LocalDateTime getAvailableto();

    /**
     * Set available to date.
     *
     * @param availableto available to date.
     */
    void setAvailableto(LocalDateTime availableto);

    /**
     * Returns true if product is enabled and now is within from/to date range.
     *
     * @param now    time now
     *
     * @return true if the product is available now
     */
    boolean isAvailable(LocalDateTime now);

    /**
     * Get all bodies in all languages.
     *
     * @return language - body map
     */
    Map<String, String> getBodies();

    /**
     * Set all bodies in all languages.
     *
     * @param bodies language - body map
     */
    void setBodies(Map<String, String> bodies);

    /**
     * Get body of specific language.
     *
     * @param language language
     *
     * @return body
     */
    String getBody(String language);

    /**
     * Set body of specific language.
     *
     * @param language language
     * @param body body
     */
    void putBody(String language, String body);

    /**
     * Get all content attributes.
     *
     * @return collection of content attributes.
     */
    Collection<AttrValueContent> getAttributes();


    /**
     * Set collection of content attributes.
     *
     * @param attribute collection of content  attributes
     */
    void setAttributes(Collection<AttrValueContent> attribute);

    /**
     * This method check if current content is root of catalog or content root
     *
     * @return true if this content is root
     */
    boolean isRoot();

}


