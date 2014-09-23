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

package org.yes.cart.impl {
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDTOImpl")]
public class CustomerOrderDeliveryDTOImpl {


    public var customerOrderDeliveryId:Number;

    public var deliveryNum:String;

    public var refNo:String;

    public var deliveryStatus:String;

    public var carrierSlaName:String;

    public var carrierName:String;

    public var ordernum:String;

    public var shippingAddress:String;

    public var billingAddress:String;

    public var currency:String;

    public var shopName:String;

    public var pgLabel:String;

    public var supportCaptureMore:Boolean;

    public var supportCaptureLess:Boolean;

    public var deliveryGroup:Boolean;

    public var detail:ArrayCollection;

    public var listPrice:Number;
    public var price:Number;
    public var promoApplied:Boolean;
    public var appliedPromo:String;

    public function CustomerOrderDeliveryDTOImpl() {
    }


    public function toString():String {
        return "CustomerOrderDeliveryDTOImpl{customerOrderDeliveryId=" + String(customerOrderDeliveryId)
                + ",ordernum=" + String(ordernum) + ",pgLabel=" + String(pgLabel)
                + ",currency=" + String(currency) + ",deliveryNum=" + String(deliveryNum)
                + ",refNo=" + String(refNo) + "}";
    }
}
}