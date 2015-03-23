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

package org.yes.cart.service.order;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * Order delivery assembler responsible for shipment creation.
 * Order delivery can be split by several reasons like security,
 * different products availability, inventory, etc.
 * Default delivery assembler can split shipments as shown in table
 * <table>
 * <tr><td> Availability &rarr;
 * <br> Inventory &darr;           </td><td>Pre Order        </td><td>Back Order</td><td>Standard</td> <td>Always</td></tr>
 * <tr><td> Inventory available             </td><td>D1(note 1) or D2 </td><td>D1        </td><td>D1      </td> <td>D4    </td></tr>
 * <tr><td> No Inventory available          </td><td>D3(note 2) or D2 </td><td>D3        </td><td>D3      </td> <td>D4    </td></tr>
 * <p/>
 * </table>
 * <p/>
 * Delivery group 1 - can be shipped
 * Delivery group 2 - awaiting for date, than check kinventory
 * Delivery group 3 - awaiting for inventory
 * Delivery group 4 - electronic delivery
 * <p/>
 * Note 1 - in case if current date more that product start availibility date and inventory available
 * Note 2 - in case if current date more that product start availibility date and no inventory
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DeliveryAssembler {


    /**
     * Fill deliveries for {@link org.yes.cart.domain.entity.CustomerOrder} from given {@link org.yes.cart.shoppingcart.ShoppingCart}.
     *
     * @param shoppingCart        given shopping cart
     * @param order               without deliveries
     * @param onePhysicalDelivery true if need to create one physical delivery.
     * @return order with attached deliveries
     */
    CustomerOrder assembleCustomerOrder(CustomerOrder order, ShoppingCart shoppingCart, boolean onePhysicalDelivery) throws OrderAssemblyException;

    /**
     * Is order can be with multiple deliveries.
     *
     * @param order given order
     * @return true in case if order can has multiple physical deliveries.
     */
    boolean isOrderMultipleDeliveriesAllowed(CustomerOrder order);


}
