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

package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CarrierSlaDTOImpl")]
public class CarrierSlaDTOImpl {


    public var carrierslaId:Number;

    public var code:String;

    public var name:String;

    public var displayNames:Object;

    public var description:String;

    public var displayDescriptions:Object;

    public var maxDays:Number;

    public var slaType:String;

    public var script:String;

    public var billingAddressNotRequired:Boolean;

    public var deliveryAddressNotRequired:Boolean;

    public var supportedPaymentGateways:String;

    public var carrierId:Number;


    public function CarrierSlaDTOImpl() {
        maxDays = 1;
    }


    public function toString():String {
        return "CarrierSlaDTOImpl{carrierslaId=" + String(carrierslaId) + ",name=" + String(name) + ",carrierId=" + String(carrierId) + "}";
    }
}
}