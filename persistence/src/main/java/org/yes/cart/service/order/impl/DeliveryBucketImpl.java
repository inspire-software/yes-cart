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

package org.yes.cart.service.order.impl;

import org.yes.cart.service.order.DeliveryBucket;

/**
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 15:56
 */
public class DeliveryBucketImpl implements DeliveryBucket {

    private final String group;
    private final String supplier;
    private final String qualifier;

    public DeliveryBucketImpl(final String group, final String supplier) {
        this(group, supplier, null);
    }

    public DeliveryBucketImpl(final String group, final String supplier, final String qualifier) {
        this.group = group;
        this.supplier = supplier;
        this.qualifier = qualifier == null ? "" : qualifier;
        if (group == null || supplier == null) {
            throw new IllegalArgumentException("Bucket must have a valid group ID and supplier");
        }
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getSupplier() {
        return supplier;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public int compareTo(final DeliveryBucket bucket) {
        int compare = supplier.compareTo(bucket.getSupplier());
        if (compare == 0) {
            compare = group.compareTo(bucket.getGroup());
            if (compare == 0) {
                compare = qualifier.compareTo(bucket.getQualifier());
            }
        }
        return compare;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryBucketImpl)) return false;

        final DeliveryBucketImpl that = (DeliveryBucketImpl) o;

        if (!group.equals(that.group)) return false;
        if (!supplier.equals(that.supplier)) return false;
        return qualifier.equals(that.qualifier);

    }

    @Override
    public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + supplier.hashCode();
        result = 31 * result + qualifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return supplier + '_' + group + '_' + qualifier;
    }
}
