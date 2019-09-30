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

package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.CategoryRelationDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 16/10/2017
 * Time: 20:36
 */
public class CategoryRelationDTOImpl implements CategoryRelationDTO {


    private long categoryId;

    private long parentId;

    private Long linkToId;

    private int rank;

    private String name;

    private String guid;

    private I18NModel displayName;

    private boolean disabled;

    private LocalDateTime availablefrom;

    private LocalDateTime availableto;

    public CategoryRelationDTOImpl() {
    }

    public CategoryRelationDTOImpl(final String guid,
                                   final long categoryId,
                                   final long parentId,
                                   final Long linkToId,
                                   final int rank,
                                   final String name,
                                   final String displayName,
                                   final boolean disabled,
                                   final LocalDateTime availablefrom,
                                   final LocalDateTime availableto) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.linkToId = linkToId;
        this.rank = rank;
        this.name = name;
        this.guid = guid;
        this.displayName = new StringI18NModel(displayName);
        this.disabled = disabled;
        this.availablefrom = availablefrom;
        this.availableto = availableto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return categoryId;
    }

    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getParentId() {
        return parentId;
    }

    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLinkToId() {
        return linkToId;
    }

    public void setLinkToId(final Long linkToId) {
        this.linkToId = linkToId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuid() {
        return guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I18NModel getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoot() {
        return (getParentId() == 0L || getParentId() == getCategoryId());
    }

    @Override
    public String toString() {
        return "CategoryRelationDTOImpl{" +
                "categoryId=" + categoryId +
                ", parentId=" + parentId +
                ", rank=" + rank +
                ", name='" + name + '\'' +
                '}';
    }

}
