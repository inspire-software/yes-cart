/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoContentService {

    /**
     * Get all categories in the system, filtered according to rights
     * @return list of categories
     * @throws Exception
     */
    List<VoContent> getAll(long shopId) throws Exception;

    /**
     * Get all categories in the system, filtered by criteria and according to rights, up to max
     * @return list of categories
     * @throws Exception
     */
    List<VoContent> getFiltered(long shopId, String filter, int max) throws Exception;


    /**
     * Get summary information for given shop.
     *
     * @param summary summary object to fill data for
     * @param shopId given shop
     * @param lang locale for localised names
     *
     * @throws Exception
     */
    void fillShopSummaryDetails(VoShopSummary summary, long shopId, String lang) throws Exception;


    /**
     * Get content by id.
     *
     * @param id pk
     * @return content vo
     * @throws Exception
     */
    VoContentWithBody getById(long id) throws Exception;

    /**
     * Update content.
     * @param voContent content
     * @return persistent version
     * @throws Exception
     */
    VoContentWithBody update(VoContentWithBody voContent)  throws Exception;

    /**
     * Create new content.
     * @param voContent content
     * @return persistent version
     * @throws Exception
     */
    VoContentWithBody create(VoContent voContent)  throws Exception;

    /**
     * Remove content by id.
     *
     * @param id content
     * @throws Exception
     */
    void remove(long id) throws Exception;


    /**
     * Get supported attributes by given content
     * @param contentId given content id
     * @return attributes
     * @throws Exception
     */
    List<VoAttrValueContent> getContentAttributes(long contentId) throws Exception;


    /**
     * Update the content attributes.
     *
     * @param vo content attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return content attributes.
     * @throws Exception
     */
    List<VoAttrValueContent> update(List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception;


    /**
     * Get supported attributes by given content
     * @param contentId given content id
     * @return attributes
     * @throws Exception
     */
    List<VoContentBody> getContentBody(long contentId) throws Exception;


    /**
     * Update the content attributes.
     *
     * @param vo content attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return content attributes.
     * @throws Exception
     */
    List<VoContentBody> updateContent(List<VoContentBody> vo) throws Exception;

}
