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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 06/09/2016
 * Time: 18:01
 */
@Dto
public class VoCustomer extends VoCustomerInfo {

    private boolean checkoutBocked;
    private BigDecimal checkoutBockedForOrdersOver;

    private boolean ordersRequireApproval;
    private BigDecimal ordersRequireApprovalForOrdersOver;

    private List<VoAttrValueCustomer> attributes;
    private List<VoCustomerShopLink> customerShops = new ArrayList<VoCustomerShopLink>();

    public boolean isCheckoutBocked() {
        return checkoutBocked;
    }

    public void setCheckoutBocked(final boolean checkoutBocked) {
        this.checkoutBocked = checkoutBocked;
    }

    public BigDecimal getCheckoutBockedForOrdersOver() {
        return checkoutBockedForOrdersOver;
    }

    public void setCheckoutBockedForOrdersOver(final BigDecimal checkoutBockedForOrdersOver) {
        this.checkoutBockedForOrdersOver = checkoutBockedForOrdersOver;
    }

    public boolean isOrdersRequireApproval() {
        return ordersRequireApproval;
    }

    public void setOrdersRequireApproval(final boolean ordersRequireApproval) {
        this.ordersRequireApproval = ordersRequireApproval;
    }

    public BigDecimal getOrdersRequireApprovalForOrdersOver() {
        return ordersRequireApprovalForOrdersOver;
    }

    public void setOrdersRequireApprovalForOrdersOver(final BigDecimal ordersRequireApprovalForOrdersOver) {
        this.ordersRequireApprovalForOrdersOver = ordersRequireApprovalForOrdersOver;
    }

    public List<VoAttrValueCustomer> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<VoAttrValueCustomer> attributes) {
        this.attributes = attributes;
    }

    public List<VoCustomerShopLink> getCustomerShops() {
        return customerShops;
    }

    public void setCustomerShops(final List<VoCustomerShopLink> customerShops) {
        this.customerShops = customerShops;
    }
}
