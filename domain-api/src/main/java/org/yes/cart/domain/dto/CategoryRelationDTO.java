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
import org.yes.cart.domain.i18n.I18NModel;

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
     * Get parent pk value.
     *
     * @return parent pk value.
     */
    long getParentId();

    /**
     * Get link to pk value.
     *
     * @return link to pk value.
     */
    Long getLinkToId();


    /**
     * Get category rank inside parent category.
     *
     * @return category rank.
     */
    int getRank();

    /**
     * Flag to denote if object is disabled on not.
     *
     * @return true if object is disabled
     */
    boolean isDisabled();

    /**
     * Get available from date.  Null value means no start.
     *
     * @return available from date.
     */
    LocalDateTime getAvailablefrom();

    /**
     * Get available to date.  Null value means no end date.
     *
     * @return available to date.
     */
    LocalDateTime getAvailableto();

    /**
     * Get category name.
     *
     * @return category name.
     */
    String getName();

    /**
     * Get display name, which is hold localization failover.
     *
     * @return display name.
     */
    I18NModel getDisplayName();

    /**
     * This method check if current category is root of catalog or content root
     *
     * @return true if this category is root
     */
    boolean isRoot();


}
