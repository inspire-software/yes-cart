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

import { AttrValueVO } from './attribute.model';

export interface CustomerInfoVO {

  customerId : number;
  email : string;

  salutation : string;
  firstname : string;
  lastname : string;
  middlename : string;

  tag : string;
  customerType : string;
  pricingPolicy : string;

}


export interface AttrValueCustomerVO extends AttrValueVO {

  customerId : number;

}

export interface CustomerShopLinkVO {

  customerId : number;
  shopId : number;
  disabled : boolean;

}


export interface CustomerVO extends CustomerInfoVO {

  checkoutBocked: boolean;
  checkoutBockedForOrdersOver: number;
  ordersRequireApproval: boolean;
  ordersRequireApprovalForOrdersOver: number;

  attributes: AttrValueCustomerVO[];
  customerShops: CustomerShopLinkVO[];

}



