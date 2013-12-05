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
import org.yes.cart.util.DomainUtils;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.PromotionDTOImpl")]
public class PromotionDTOImpl {

    public var promotionId:Number;

    public var shopCode:String;

    public var currency:String;

    public var rank:Number;

    public var code:String;

    public var promoType:String;

    public var promoAction:String;

    public var canBeCombined:Boolean;

    public var eligibilityCondition:String;

    public var promoActionContext:String;

    public var tag:String;

    public var name:String;

    public var displayNames:Object;

    public var description:String;

    public var displayDescriptions:Object;

    public var enabled:Boolean;

    public var enabledFrom:Date;

    public var enabledTo:Date;


    public function PromotionDTOImpl() {
        rank = 0;
        enabled = false;
    }
}
}
