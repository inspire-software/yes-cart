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

    public var salutation:String;

    public var firstname:String;

    public var lastname:String;

    public var middlename:String;

    public var defaultAddress:Boolean;

    public var phone1:String;
    public var phone2:String;

    public var mobile1:String;
    public var mobile2:String;

    public var email1:String;
    public var email2:String;

    public var custom1:String;
    public var custom2:String;
    public var custom3:String;
    public var custom4:String;

    public var customerId:Number;

    public function get fullname():String {
        return (DomainUtils.isNotBlankString(salutation) ? (salutation + ' ') : '') + firstname + ' ' + (DomainUtils.isNotBlankString(middlename) ? (middlename + ' ') : '') + lastname;
    }

    public function get fulladdress():String {
        return addrline1 + (DomainUtils.isNotBlankString(addrline2) ? (' ' + addrline2 + '\n') : '\n') +
                city + ' ' + postcode + '\n' +
                stateCode + '\n' +
                countryCode;
    }

    public function get fullcountry():String {
        return countryCode + '\n' + stateCode;
    }

    public function get phonelist():String {
        var _list:String = '';
        var _nl:Boolean = false;
        if (DomainUtils.isNotBlankString(phone1)) {
            _list += phone1;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(phone2)) {
            _list += (_nl ? '\n' : '') + phone2;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(mobile1)) {
            _list += (_nl ? '\n' : '') + mobile1;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(mobile2)) {
            _list += mobile2;
        }
        return _list;
    }

    public function get customlist():String {
        var _list:String = '';
        var _nl:Boolean = false;
        if (DomainUtils.isNotBlankString(custom1)) {
            _list += custom1;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(custom2)) {
            _list += (_nl ? '\n' : '') + custom2;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(custom3)) {
            _list += (_nl ? '\n' : '') + custom3;
            _nl = true;
        }
        if (DomainUtils.isNotBlankString(custom4)) {
            _list += custom4;
        }
        return _list;
    }

    public function AddressDTOImpl() {
    }
}
}
