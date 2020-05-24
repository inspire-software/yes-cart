
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
public class CacheInfoDTO implements Serializable {

    private String cacheName;

    private long cacheSize;

    private long inMemorySize;

    private long inMemorySizeMax;

    private long timeToLiveSeconds;

    private long timeToIdleSeconds;

    private boolean eternal;

    private String memoryStoreEvictionPolicy;

    private boolean overflowToDisk;

    private long  diskStoreSize;

    private long  calculateInMemorySize;

    private long  calculateOnDiskSize;

    private long  hits;

    private long  misses;

    private boolean disabled;

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

    public long getDiskStoreSize() {
        return diskStoreSize;
    }

    public void setDiskStoreSize(long diskStoreSize) {
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

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getInMemorySize() {
        return inMemorySize;
    }

    public void setInMemorySize(long inMemorySize) {
        this.inMemorySize = inMemorySize;
    }

    public long getInMemorySizeMax() {
        return inMemorySizeMax;
    }

    public void setInMemorySizeMax(final long inMemorySizeMax) {
        this.inMemorySizeMax = inMemorySizeMax;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(final long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(final long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public boolean isEternal() {
        return eternal;
    }

    public void setEternal(final boolean eternal) {
        this.eternal = eternal;
    }

    public String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public void setMemoryStoreEvictionPolicy(final String memoryStoreEvictionPolicy) {
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    public boolean isOverflowToDisk() {
        return overflowToDisk;
    }

    public void setOverflowToDisk(final boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(final long hits) {
        this.hits = hits;
    }

    public long getMisses() {
        return misses;
    }

    public void setMisses(final long misses) {
        this.misses = misses;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public CacheInfoDTO(final String cacheName,
                        final long cacheSize,
                        final long inMemorySize,
                        final long inMemorySizeMax,
                        final boolean overflowToDisk,
                        final boolean eternal,
                        final long timeToLiveSeconds,
                        final long timeToIdleSeconds,
                        final String memoryStoreEvictionPolicy,
                        final long diskStoreSize,
                        final long hits,
                        final long misses,
                        final long calculateInMemorySize,
                        final long calculateOnDiskSize,
                        final boolean disabled) {
        this.cacheName = cacheName;
        this.cacheSize = cacheSize;
        this.inMemorySize = inMemorySize;
        this.inMemorySizeMax = inMemorySizeMax;
        this.overflowToDisk = overflowToDisk;
        this.eternal = eternal;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.timeToIdleSeconds = timeToIdleSeconds;
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
        this.diskStoreSize = diskStoreSize;
        this.calculateInMemorySize = calculateInMemorySize;
        this.calculateOnDiskSize = calculateOnDiskSize;
        this.hits = hits;
        this.misses = misses;
        this.disabled = disabled;
    }

    public CacheInfoDTO() {
    }
}
