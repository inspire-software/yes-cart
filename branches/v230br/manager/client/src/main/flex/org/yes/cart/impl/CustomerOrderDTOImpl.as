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
import org.yes.cart.util.DomainUtils;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CustomerOrderDTOImpl")]
public class CustomerOrderDTOImpl {


    public var customerorderId:Number;

    public var ordernum:String;

    public var pgLabel:String;

    public var billingAddress:String;

    public var shippingAddress:String;

    public var cartGuid:String;

    public var currency:String;

    public var orderMessage:String;

    public var orderStatus:String;

    public var multipleShipmentOption:Boolean;

    public var orderTimestamp:Date;

    public var email:String;

    public var firstname:String;

    public var lastname:String;

    public var middlename:String;

    public var customerId:Number;

    public var shopId:Number;

    public var code:String;

    public var listPrice:Number;
    public var price:Number;
    public var netPrice:Number;
    public var grossPrice:Number;
    public var promoApplied:Boolean;
    public var appliedPromo:String;

    public var amount:Number;

    public function get fullname():String {
        return firstname + ' ' + (DomainUtils.isNotBlankString(middlename) ? (middlename + ' ') : '') + lastname;
    }

    public function CustomerOrderDTOImpl() {
    }


    public function toString():String {
        return "CustomerOrderDTOImpl{customerorderId=" + String(customerorderId)
                + ",ordernum=" + String(ordernum) + ",pgLabel=" + String(pgLabel)
                + ",currency=" + String(currency) + ",firstname=" + String(firstname)
                + ",lastname=" + String(lastname) + "}";
    }
}
}