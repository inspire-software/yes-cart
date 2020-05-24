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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 19:46
 */
@XmlRootElement(name = "cart-validation")
public class CartValidationRO {

    private boolean checkoutBlocked;
    private List<CartValidationItemRO> items;

    @XmlAttribute(name = "checkout-blocked")
    public boolean isCheckoutBlocked() {
        return checkoutBlocked;
    }

    public void setCheckoutBlocked(final boolean checkoutBlocked) {
        this.checkoutBlocked = checkoutBlocked;
    }

    @XmlElement(name = "cart-validation-item")
    public List<CartValidationItemRO> getItems() {
        return items;
    }

    public void setItems(final List<CartValidationItemRO> items) {
        this.items = items;
    }
}
