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

import {PromotionVO} from './pricelists.model';

export interface CustomerOrderInfoVO {

  customerorderId : number;

  shopId : number;
  code : string; // shop code

  ordernum : string;
  cartGuid : string;
  orderStatus : string;
  orderStatusNextOptions : string[];
  orderPaymentStatus : string;
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

export interface CustomerOrderTransitionResultVO {

  errorCode : string;
  orderNum : string;
  shippingNum : string;
  localizationKey : string;
  localizedMessageParameters : any[];
  errorMessage : string;

}

export interface CustomerOrderLineVO {

  customerOrderDeliveryDetId : number;

  skuCode : string;
  skuName : string;

  qty : number;
  price : number;

  listPrice : number;
  salePrice : number;

  gift : boolean;
  promoApplied : boolean;
  appliedPromo : string[];

  netPrice : number;
  grossPrice : number;
  taxRate : number;
  taxCode : string;
  taxExclusiveOfPrice : boolean;

  lineTotal : number;
  lineTax : number;

  deliveryNum : string;
  deliveryStatusLabel : string;

}

export interface CustomerOrderDeliveryInfoVO {

  customerOrderDeliveryId : number;

  ordernum : string;
  deliveryNum : string;
  refNo : string;
  deliveryGroup : string;
  deliveryStatus : string;
  deliveryStatusNextOptions : string[];

  carrierSlaName : string;
  carrierName : string;
  shippingAddress : string;
  billingAddress : string;

  shopName : string;
  pgLabel : string;
  supportCaptureMore : boolean;
  supportCaptureLess : boolean;

  currency : string;
  price : number;
  listPrice : number;

  promoApplied : boolean;
  appliedPromo : string[];

  netPrice : number;
  grossPrice : number;
  taxRate : number;
  taxCode : string;
  taxExclusiveOfPrice : boolean;

}

export interface CustomerOrderVO extends CustomerOrderInfoVO {

  lines : CustomerOrderLineVO[];
  deliveries : CustomerOrderDeliveryInfoVO[];
  promotions : PromotionVO[];

}

export interface PaymentVO {

  customerOrderPaymentId : number;

  cardHolderName : string;
  cardExpireYear : string;
  cardExpireMonth : string;
  cardStartDate : Date;

  paymentAmount : number;
  taxAmount : number;

  orderDate : Date;
  orderCurrency : string;
  orderNumber : string;
  orderShipment : string;
  shopCode : string;

  transactionReferenceId : string;
  transactionRequestToken : string;
  transactionAuthorizationCode : string;
  transactionGatewayLabel : string;
  transactionOperation : string;
  transactionOperationResultCode : string;
  transactionOperationResultMessage : string;
  paymentProcessorResult : string;
  paymentProcessorBatchSettlement : boolean;

  createdTimestamp : Date;
  updatedTimestamp : Date;
  shopperIpAddress : string;

}
