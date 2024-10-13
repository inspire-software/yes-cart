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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.*;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:27
 */
public interface VoCustomerOrderService {

    /**
     * Get all orders for given filter
     *
     * @param lang language used for PG localizations
     * @param filter filter
     *
     * @return orders
     *
     * @throws Exception errors
     */
    VoSearchResult<VoCustomerOrderInfo> getFilteredOrders(String lang, VoSearchContext filter) throws Exception;

    /**
     * Get order by id
     *
     * @param lang language used for PG localizations
     * @param orderId order pk
     *
     * @return order
     *
     * @throws Exception errors
     */
    VoCustomerOrder getOrderById(String lang, long orderId) throws Exception;

    /**
     * Perform order transition.
     *
     * @param transition    transition key
     * @param ordernum      order number
     * @param context       optional data (e.g. message, deliveryref)
     *
     * @return transition result
     *
     * @throws Exception errors
     */
    VoCustomerOrderTransitionResult transitionOrder(String transition, String ordernum, Map<String, String> context) throws Exception;

    /**
     * Perform order transition.
     *
     * @param transition    transition key
     * @param ordernum      order number
     * @param deliverynum   order deliverynumber
     * @param context       optional data (e.g. message, deliveryref)
     *
     * @return transition result
     *
     * @throws Exception errors
     */
    VoCustomerOrderTransitionResult transitionDelivery(String transition, String ordernum, String deliverynum, Map<String, String> context) throws Exception;

    /**
     * Perform order transition.
     *
     * @param transition    transition key
     * @param ordernum      order number
     * @param deliverynum   order deliverynumber
     * @param lineId        Line ID
     * @param context       optional data (e.g. message, deliveryref)
     *
     * @return transition result
     *
     * @throws Exception errors
     */
    VoCustomerOrderTransitionResult transitionOrderLine(String transition, String ordernum, String deliverynum, String lineId, Map<String, String> context) throws Exception;

    /**
     * Perform manual export action.
     *
     * @param lang language used for PG localizations
     * @param id order pk
     * @param export true to allow export, false to clear export eligibility
     *
     * @return order
     *
     * @throws Exception errors
     */
    VoCustomerOrder exportOrder(String lang, long id, boolean export) throws Exception;

}
