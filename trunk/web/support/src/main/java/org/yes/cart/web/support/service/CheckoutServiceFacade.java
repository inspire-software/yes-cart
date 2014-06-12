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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service facade that combines all checkout order related operations.
 *
 * User: denispavlov
 * Date: 13/01/2014
 * Time: 08:27
 */
public interface CheckoutServiceFacade {


    /**
     * Find created order by cart guid.
     *
     * @param shoppingCartGuid shopping cart  guid
     * @return created order.
     */
    CustomerOrder findByGuid(String shoppingCartGuid);

    /**
     * Calculate order total using appropriate calculation strategy.
     *
     * @param customerOrder order
     *
     * @return total for this order
     */
    Total getOrderTotal(CustomerOrder customerOrder);

    /**
     * Calculate order total using appropriate calculation strategy.
     *
     * @param customerOrder order
     * @param delivery delivery to calculate total for
     *
     * @return total for this delivery
     */
    Total getOrderDeliveryTotal(CustomerOrder customerOrder, CustomerOrderDelivery delivery);

    /**
     * Find all payment records for this order.
     *
     * NOTE: all payment records include AUTH, CAPTURE etc, so some payment records refer to
     *       the same amount but in different states. Be careful when using this to get
     *       total amounts.
     *
     * @param orderNumber order number
     *
     * @return all payment records
     */
    List<CustomerOrderPayment> findPaymentRecordsByOrderNumber(String orderNumber);

    /**
     * Create single payment to authorize for this order.
     *
     * @param order  order
     *
     * @return payment to process
     */
    Payment createPaymentToAuthorize(CustomerOrder order);

    /**
     * Get payment gateway for given order according to order.pgLabel.
     *
     * @param order order
     *
     * @return payment gateway
     */
    PaymentGateway getOrderPaymentGateway(CustomerOrder order);

    /**
     * Get list of payment gateway descriptors applicable for given shop and provided given cart.
     *
     * @param shop current shop (PG's may be shop specific)
     * @param cart current cart (PG's may be delivery method specific)
     *
     * @return list of payment gateway descriptors and corresponding gateway name in correct locale
     */
    List<Pair<PaymentGatewayDescriptor, String>> getPaymentGatewaysDescriptors(Shop shop, ShoppingCart cart);


    /**
     * Check if payment made for given order has OK status.
     *
     * @param customerOrder customer order
     *
     * @return true if payment is OK
     */
    boolean isOrderPaymentSuccessful(CustomerOrder customerOrder);

    /**
     * Get all order level promo codes as a set.
     *
     * @param customerOrder given order
     *
     * @return set of unique promo codes
     */
    Set<String> getOrderPromoCodes(CustomerOrder customerOrder);

    /**
     * Get all shipping level promo codes as a set.
     *
     * @param orderDelivery given delivery
     *
     * @return set of unique promo codes
     */
    Set<String> getOrderShippingPromoCodes(CustomerOrderDelivery orderDelivery);

    /**
     * Get all item level promo codes as a set.
     *
     * @param orderDeliveryDet given delivery item
     *
     * @return set of unique promo codes
     */
    Set<String> getOrderItemPromoCodes(CustomerOrderDeliveryDet orderDeliveryDet);

    /**
     * Create customer order from shopping cart.
     *
     * NOTE: #getOrderInfo().isMultipleDelivery() will determine number of deliveries.
     *
     * @param shoppingCart shopping cart
     *
     * @return created order.
     */
    CustomerOrder createFromCart(ShoppingCart shoppingCart) throws OrderAssemblyException;

    /**
     * Determine if multiple delivery option should be allowed for this cart.
     * True is returned if this cart contains items that can be delivered using multiple
     * deliveries such as available and pre-order items in one cart.
     *
     * @param shoppingCart shopping cart
     *
     * @return true if allowed
     */
    boolean isMultipleDeliveryAllowedForCart(ShoppingCart shoppingCart);

    /**
     * Persist changes to DB.
     *
     * @param customerOrder order to update
     *
     * @return updated entity
     */
    CustomerOrder update(CustomerOrder customerOrder);
}
