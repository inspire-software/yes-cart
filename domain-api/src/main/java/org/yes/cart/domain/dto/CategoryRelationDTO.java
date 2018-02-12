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

package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Guidable;
import org.yes.cart.domain.entity.Identifiable;

import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 16/10/2017
 * Time: 19:45
 */
public interface CategoryRelationDTO extends Identifiable, Guidable {

    /**
     * Get category pk value.
     *
     * @return category pk value
     */
    long getCategoryId();

    /**
     * Set category pk value.
     *
     * @param categoryId pk value
     */
    void setCategoryId(long categoryId);

    /**
     * Get parrent pk value.
     *
     * @return parrent pk value.
     */
    long getParentId();

    /**
     * Set parrent pk value.
     *
     * @param parentId parent pk value.
     */
    void setParentId(long parentId);

    /**
     * Get link to pk value.
     *
     * @return link to pk value.
     */
    Long getLinkToId();

    /**
     * Set link to pk value.
     *
     * @param linkToId link to pk value.
     */
    void setLinkToId(Long linkToId);


    /**
     * Get category rank inside parent category.
     *
     * @return category rank.
     */
    int getRank();

    /**
     * Set category rank.
     *
     * @param rank category rank
     */
    void setRank(int rank);

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
     * Get category name.
     *
     * @return category name.
     */
    String getName();

    /**
     * Set category name.
     *
     * @param name category name.
     */
    void setName(String name);

    /**
     * Get display name, which is hold localization failover.
     * @return display name.
     */
    String getDisplayName();

    /**
     * Set display name.
     * @param displayName display name.
     */
    void setDisplayName(String displayName);

    /**
     * This method check if current category is root of catalog or content root
     *
     * @return true if this category is root
     */
    boolean isRoot();


}
