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
import mx.core.IUID;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductTypeDTOImpl")]

public class ProductTypeDTOImpl implements IUID {
    
    
    public var producttypeId:Number;

    public var name:String;

    public var description:String;

    public var uitemplate:String;

    public var uisearchtemplate:String;

    public var service:Boolean;

    public var ensemble:Boolean;

    public var shipable:Boolean;

    public var downloadable:Boolean;

    public var digital:Boolean;

    
    public function ProductTypeDTOImpl() {
    }


    public function toString():String {
        return "ProductTypeDTOImpl{producttypeId=" + String(producttypeId)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",uitemplate=" + String(uitemplate)
                + ",uisearchtemplate=" + String(uisearchtemplate)
                + ",service=" + String(service)
                + ",ensemble=" + String(ensemble) + ",shipable=" + String(shipable) + "}";
    }

    public function get uid():String {
        return "ProductTypeDTOImpl-"+String(producttypeId);
    }

    public function set uid(value:String):void {
    }

    
}
}