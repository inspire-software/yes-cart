/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import { Pair } from './common.model';

export interface PriceListVO {

  skuPriceId : number;

  regularPrice : number;
  minimalPrice : number;
  salePrice : number;

  salefrom : Date;
  saleto : Date;

  quantity : number;
  currency : string;

  skuCode : string;
  skuName : string;
  shopCode : string;

  tag : string;

  pricingPolicy : string;

}

export interface PromotionVO {

  promotionId : number;

  code : string;
  shopCode : string;
  currency : string;

  rank : number;

  name : string;
  description : string;

  displayNames : Pair<string, string>[];
  displayDescriptions : Pair<string, string>[];

  promoType : string;
  promoAction : string;

  eligibilityCondition : string;
  promoActionContext : string;

  couponTriggered : boolean;
  canBeCombined : boolean;
  enabled : boolean;

  enabledFrom : Date;
  enabledTo : Date;

  tag : string;

}

export interface PromotionCouponVO {

  promotioncouponId : number;
  promotionId : number;

  code : string;
  usageLimit : number;
  usageLimitPerCustomer : number;
  usageCount : number;

}

export interface TaxVO {

  taxId : number;

  taxRate : number;
  exclusiveOfPrice : boolean;

  shopCode : string;
  currency : string;
  code : string;

  guid : string;

  description : string;

}

export interface TaxConfigVO {

  taxConfigId : number;
  taxId : number;

  productCode : string;
  stateCode : string;
  countryCode : string;

  guid : string;

}
