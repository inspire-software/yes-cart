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
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CategoryDTOImpl")]
public class CategoryDTOImpl {
    
    public var categoryId:Number ;

    public var parentId:Number;

    public var rank:int;

    public var productTypeId:Number;
    
    public var productTypeName:String;

    public var name:String;

    public var description:String;

    public var uitemplate:String;

    public var availablefrom:Date ;

    public var availabletill:Date ;

    public var   uri:String;

    public var  title:String;

    public var  metakeywords:String;

    public var  metadescription:String;


    public var navigationByAttributes:Boolean ;

    public var navigationByBrand:Boolean ;

    public var navigationByPrice:Boolean ;

    public var navigationByPriceTiers:String ;

    public var attribute:ArrayCollection;

    public var children:ArrayCollection;

    public var changed:Boolean = false;
    
    
    public function CategoryDTOImpl() {
    }

    public function addChild(child:CategoryDTOImpl):CategoryDTOImpl {
        if (children == null) {
            children = new ArrayCollection();
        }
        child.parentId = this.categoryId;
        children.addItem(child);
        return child;
    }


    public function toString():String {
        return "CategoryDTOImpl{categoryId=" + String(categoryId)
                + ",parentId=" + String(parentId)
                + ",rankLint=" + String(rank)
                + ",productTypeId=" + String(productTypeId)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",uitemplate=" + String(uitemplate)
                + ",availablefrom=" + String(availablefrom)
                + ",availabletill=" + String(availabletill)
                + ",navigationByAttributes=" + String(navigationByAttributes)
                + ",navigationByBrand=" + String(navigationByBrand)
                + ",navigationByPrice=" + String(navigationByPrice)
                + ",changed=" + String(changed)
                + ",attribute=\n["+ String(attribute)
                + "]\n,children=" + String(children) + "}";
    }
}
}