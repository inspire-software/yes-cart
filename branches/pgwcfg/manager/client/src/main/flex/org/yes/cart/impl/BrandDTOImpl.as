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
import mx.core.IUID;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.BrandDTOImpl")]

public class BrandDTOImpl implements IUID {

    public var brandId:Number;


    public var name:String;


    public var description:String;


    public var attribute:ArrayCollection;


    public function BrandDTOImpl() {
    }


    public function toString():String {
        return "BrandDTOImpl{brandId=" + String(brandId)
                + ",name=" + String(name)
                + ",description=" + String(description) + "}";
    }


    public function get uid():String {
        return "BrandDTOImpl-"+String(brandId);
    }

    public function set uid(value:String):void {
    }
}
}