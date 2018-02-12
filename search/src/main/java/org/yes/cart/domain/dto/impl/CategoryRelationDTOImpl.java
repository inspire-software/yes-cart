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

    private String displayName;

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
                                   final LocalDateTime availablefrom,
                                   final LocalDateTime availableto) {
        this.categoryId = categoryId;
        this.parentId = parentId;
        this.linkToId = linkToId;
        this.rank = rank;
        this.name = name;
        this.guid = guid;
        this.displayName = displayName;
        this.availablefrom = availablefrom;
        this.availableto = availableto;
    }

    /**
     * {@inheritDoc}
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * {@inheritDoc}
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    /**
     * {@inheritDoc}
     */
    public Long getLinkToId() {
        return linkToId;
    }

    /**
     * {@inheritDoc}
     */
    public void setLinkToId(final Long linkToId) {
        this.linkToId = linkToId;
    }

    /**
     * {@inheritDoc}
     */
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    /**
     * {@inheritDoc}
     */
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return guid;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
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
