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

package org.yes.cart.model {

/**
 * Data model for porting price navigation XML object to DataGrid rows.
 */
[Bindable]
public class PriceRangeModel {

    private var _currency:String;

    private var _entry:XML;

    private var _tag:Object;


    public function get tag():Object {
        return _tag;
    }

    public function set tag(value:Object):void {
        _tag = value;
    }

    public function get currency():String {
        return _currency;
    }

    public function set currency(value:String):void {
        _currency = value;
    }

    public function get entry():XML {
        return _entry;
    }

    public function set entry(value:XML):void {
        _entry = value;
    }


    public function PriceRangeModel(currency:String, entry:XML, tag:Object) {
        _currency = currency;
        _entry = entry;
        _tag = tag;
    }


    public function toString():String {
        return "PriceRangeList{_currency=" + String(_currency) + ",_entry=" + String(_entry) + ",_tag=" + String(_tag) + "}";
    }
}
}