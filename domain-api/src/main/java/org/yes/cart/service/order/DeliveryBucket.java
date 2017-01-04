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

package org.yes.cart.service.order;

/**
 * Delivery bucket holds basic information about order splitting. Each ordered items
 * is placed in a bucket, which them become the basis for order splitting mechanism.
 *
 * Bucket must implement equals() and hashCode() correctly so that it can be used in
 * maps and adequately compared.
 *
 * This interface inherits Comparable so that it can be priritised as well.
 *
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 08:53
 */
public interface DeliveryBucket extends Comparable<DeliveryBucket> {

    /**
     * Delivery group {@link org.yes.cart.domain.entity.CustomerOrderDelivery} *_DELIVERY_GROUP of
     * this bucket.
     *
     * @return delivery group marker
     */
    String getGroup();

    /**
     * Supplier Shop code (shop that dispatches the goods) {@link org.yes.cart.domain.entity.Shop#getCode()}
     *
     * @return shop code that will fulfill this delivery
     */
    String getSupplier();

    /**
     * Additional qualifier to make bucket unique.
     *
     * @return qualifier
     */
    String getQualifier();

}
