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

package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Guidable;
import org.yes.cart.domain.entity.Identifiable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:41
 */
public interface ContentDTO extends Identifiable, Guidable {

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
     * Get parent name value.
     *
     * @return parent name value.
     */
    String getParentName();

    /**
     * Set parent name value.
     *
     * @param parentName parent name value.
     */
    void setParentName(String parentName);

    /**
     * Get content rank inside parent content.
     *
     * @return content rank.
     */
    int getRank();

    /**
     * Set content rank.
     *
     * @param rank content rank
     */
    void setRank(int rank);


    /**
     * Get content name.
     *
     * @return content name.
     */
    String getName();

    /**
     * Set content name.
     *
     * @param name content name.
     */
    void setName(String name);

    /**
     * Display name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get display name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);

    /**
     * Get content description.
     *
     * @return content description.
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description description
     */
    void setDescription(String description);

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
     * Children of this content
     *
     * @return Children list.
     */
    List<ContentDTO> getChildren();

    /**
     * Set children list.
     *
     * @param children children list.
     */
    void setChildren(List<ContentDTO> children);


    /**
     * Get attributes.
     *
     * @return list of attributes
     */
    Set<AttrValueContentDTO> getAttributes();

    /**
     * Set list of attributes.
     *
     * @param attribute list of attributes
     */
    void setAttributes(Set<AttrValueContentDTO> attribute);


    /**
     * Get seo uri.
     * @return uri
     */
    String getUri();

    /**
     * Set seo uri;
     * @param uri  seo uri to  use
     */
    void setUri(String uri);

    /**
     * Get title.
     * @return  title
     */

    String getTitle();

    /**
     * Set seo title
     * @param title seo title to use
     */
    void setTitle(String title);

    /**
     * Display titles.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayTitles();

    /**
     * Set display titles
     *
     * @param titles localised locale => name pairs
     */
    void setDisplayTitles(Map<String, String> titles);

    /**
     * Get meta key words.
     * @return meta key words
     */

    String getMetakeywords();

    /**
     * Set meta key words to use.
      * @param metakeywords      key words
     */
    void setMetakeywords(String metakeywords);


    /**
     * Display metakeywords.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetakeywords();

    /**
     * Set display metakeywords
     *
     * @param metakeywords localised locale => name pairs
     */
    void setDisplayMetakeywords(Map<String, String> metakeywords);


    /**
     * Get seo description
     * @return seo description.
     */
    String getMetadescription();

    /**
     * Set seo description.
     * @param metadescription description to use
     */
    void setMetadescription(String metadescription);



    /**
     * Display metadescription.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetadescriptions();

    /**
     * Set display metadescription
     *
     * @param metadescription localised locale => name pairs
     */
    void setDisplayMetadescriptions(Map<String, String> metadescription);


}
