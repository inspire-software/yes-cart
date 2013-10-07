
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

package org.yes.cart.domain.dto.impl;

import java.io.Serializable;

/**
 *
 * Class to represent cache information.
 *
 * User: iazarny@yahoo.com
 * Date: 9/30/12
 * Time: 5:30 PM
 */
public class CacheInfoDTOImpl implements Serializable {

    private String cacheName;

    private int cacheSize;

    private long inMemorySize;

    private int  diskStoreSize;

    private long  calculateInMemorySize;

    private long  calculateOnDiskSize;

    private String nodeId;

    private String nodeUri;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeUri() {
        return nodeUri;
    }

    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }

    public int getDiskStoreSize() {
        return diskStoreSize;
    }

    public void setDiskStoreSize(int diskStoreSize) {
        this.diskStoreSize = diskStoreSize;
    }

    public long getCalculateInMemorySize() {
        return calculateInMemorySize;
    }

    public void setCalculateInMemorySize(long calculateInMemorySize) {
        this.calculateInMemorySize = calculateInMemorySize;
    }

    public long getCalculateOnDiskSize() {
        return calculateOnDiskSize;
    }

    public void setCalculateOnDiskSize(long calculateOnDiskSize) {
        this.calculateOnDiskSize = calculateOnDiskSize;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getInMemorySize() {
        return inMemorySize;
    }

    public void setInMemorySize(long inMemorySize) {
        this.inMemorySize = inMemorySize;
    }

    public CacheInfoDTOImpl(final String cacheName, final int cacheSize, final long inMemorySize) {
        this.cacheName = cacheName;
        this.cacheSize = cacheSize;
        this.inMemorySize = inMemorySize;
    }

    public CacheInfoDTOImpl(final String cacheName,
                            final int cacheSize,
                            final long inMemorySize,
                            final int diskStoreSize,
                            final long calculateInMemorySize,
                            final long calculateOnDiskSize) {
        this.cacheName = cacheName;
        this.cacheSize = cacheSize;
        this.inMemorySize = inMemorySize;
        this.diskStoreSize = diskStoreSize;
        this.calculateInMemorySize = calculateInMemorySize;
        this.calculateOnDiskSize = calculateOnDiskSize;
    }


    public CacheInfoDTOImpl(final String cacheName,
                            final int cacheSize,
                            final long inMemorySize,
                            final int diskStoreSize,
                            final long calculateInMemorySize,
                            final long calculateOnDiskSize,
                            final String nodeUri) {
        this.cacheName = cacheName;
        this.cacheSize = cacheSize;
        this.inMemorySize = inMemorySize;
        this.diskStoreSize = diskStoreSize;
        this.calculateInMemorySize = calculateInMemorySize;
        this.calculateOnDiskSize = calculateOnDiskSize;
        this.nodeUri = nodeUri;

    }



    public CacheInfoDTOImpl() {
    }
}
