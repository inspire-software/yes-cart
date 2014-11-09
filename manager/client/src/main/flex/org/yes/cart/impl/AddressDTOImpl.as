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

/**
 * User: denispavlov
 * Date: 12-07-26
 * Time: 6:55 PM
 */
package org.yes.cart.impl {
import mx.utils.StringUtil;

import org.yes.cart.util.DomainUtils;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.AddressDTOImpl")]
public class AddressDTOImpl {

    public var addressId:Number;

    public var city:String;

    public var postcode:String;

    public var addrline1:String;

    public var addrline2:String;

    public var addressType:String;

    public var countryCode:String;

    public var stateCode:String;

    public var firstname:String;

    public var lastname:String;

    public var middlename:String;

    public var defaultAddress:Boolean;

    public var phoneList:String;

    public var customerId:Number;

    public function get fullname():String {
        return firstname + ' ' + (DomainUtils.isNotBlankString(middlename) ? (middlename + ' ') : '') + lastname;
    }

    public function get fulladdress():String {
        return addrline1 + (DomainUtils.isNotBlankString(addrline2) ? (addrline2 + '\n') : '\n') +
                city + ' ' + postcode + '\n' +
                stateCode + '\n' +
                countryCode;
    }

    public function get fullcountry():String {
        return countryCode + '\n' + stateCode;
    }

    public function AddressDTOImpl() {
    }
}
}
