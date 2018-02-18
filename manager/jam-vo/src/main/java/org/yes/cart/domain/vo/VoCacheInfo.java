
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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: dpavlov
 */
@Dto
public class VoCacheInfo {

    @DtoField(readOnly = true)
    private String cacheName;

    @DtoField(readOnly = true)
    private int cacheSize;

    @DtoField(readOnly = true)
    private long inMemorySize;

    @DtoField(readOnly = true)
    private long inMemorySizeMax;

    @DtoField(readOnly = true)
    private int timeToLiveSeconds;

    @DtoField(readOnly = true)
    private int timeToIdleSeconds;

    @DtoField(readOnly = true)
    private boolean eternal;

    @DtoField(readOnly = true)
    private String memoryStoreEvictionPolicy;

    @DtoField(readOnly = true)
    private boolean overflowToDisk;

    @DtoField(readOnly = true)
    private int  diskStoreSize;

    @DtoField(readOnly = true)
    private long  calculateInMemorySize;

    @DtoField(readOnly = true)
    private long  calculateOnDiskSize;

    @DtoField(readOnly = true)
    private long  hits;

    @DtoField(readOnly = true)
    private long  misses;

    @DtoField(readOnly = true)
    private boolean stats;

    @DtoField(readOnly = true)
    private String nodeId;

    @DtoField(readOnly = true)
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

    public long getInMemorySizeMax() {
        return inMemorySizeMax;
    }

    public void setInMemorySizeMax(final long inMemorySizeMax) {
        this.inMemorySizeMax = inMemorySizeMax;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(final int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public int getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(final int timeToIdleSeconds) {
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

    public boolean isStats() {
        return stats;
    }

    public void setStats(final boolean stats) {
        this.stats = stats;
    }


    public VoCacheInfo() {
    }
}
