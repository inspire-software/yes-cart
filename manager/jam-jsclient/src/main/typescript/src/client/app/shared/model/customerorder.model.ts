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

export interface CustomerOrderInfoVO {

  customerorderId : number;

  shopId : number;
  code : string; // shop code

  ordernum : string;
  cartGuid : string;
  orderStatus : string;
  orderStatusNextOptions : string[];
  orderTimestamp : Date;

  pgLabel : string;
  pgName : string;

  billingAddress : string;
  shippingAddress : string;
  orderMessage : string;
  multipleShipmentOption : boolean;

  email : string;
  salutation : string;
  firstname : string;
  lastname : string;
  middlename : string;
  customerId : number;

  currency : string;
  orderTotal : number; // GrossPrice + SUM(delivery.GrossPrice)
  orderPaymentBalance : number; // Payments less Refunds

  price : number;
  netPrice : number;
  grossPrice : number;
  listPrice : number; // without discounts
  promoApplied : boolean;
  appliedPromo : string[];

  orderIp : string;

}
