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

export interface CartTotalVO {

  listSubTotal: number;
  saleSubTotal: number;
  nonSaleSubTotal: number;
  priceSubTotal: number;
  orderPromoApplied: boolean;
  appliedOrderPromo: string;

  subTotal: number;
  subTotalTax: number;
  subTotalAmount: number;

  deliveryListCost: number;
  deliveryCost: number;
  deliveryPromoApplied: boolean;
  appliedDeliveryPromo: string;
  deliveryTax: number;
  deliveryCostAmount: number;

  total: number;
  totalTax: number;
  listTotalAmount: number;
  totalAmount: number;

}

export interface CartShoppingContextVO {

  customerName: string;

  shopId: number;
  shopCode: string;
  customerShopId: number;
  customerShopCode: string;

  countryCode: string;
  stateCode: string;

  customerEmail: string;
  customerShops: string[];

  taxInfoChangeViewEnabled: boolean;
  taxInfoEnabled: boolean;
  taxInfoUseNet: boolean;
  taxInfoShowAmount: boolean;

  hidePrices: boolean;

  latestViewedSkus: string[];
  latestViewedCategories: string[];

  resolvedIp: string;

}

export interface CartOrderInfoVO {

  paymentGatewayLabel: string;

  multipleDelivery: boolean;
  multipleDeliveryAvailable: Pair<string, boolean>[];

  separateBillingAddress: boolean;
  separateBillingAddressEnabled: boolean;

  billingAddressNotRequired: boolean;
  deliveryAddressNotRequired: boolean;

  carrierSlaId: Pair<string, string>[];
  billingAddressId: number;
  deliveryAddressId: number;

  details: Pair<string, string>[];

  orderMessage: string;

}

export interface CartItemVO {

  productSkuCode: string;
  productName: string;

  quantity: number;
  supplierCode: string;
  deliveryGroup: string;

  price: number;
  salePrice: number;
  listPrice: number;

  netPrice: number;
  grossPrice: number;
  taxRate: number;
  taxCode: string;
  taxExclusiveOfPrice: boolean;

  gift: boolean;
  promoApplied: boolean;
  fixedPrice: boolean;
  appliedPromo: string;

}

export interface CartVO {

  cartItems: CartItemVO[];
  items: CartItemVO[];
  gifts: CartItemVO[];

  shipping: CartItemVO[];

  coupons: string[];
  appliedCoupons: string[];

  guid: string;
  ordernum: string;
  currentLocale: string;
  currencyCode: string;

  modifiedTimestamp: number;
  modified: boolean;
  processingStartTimestamp: number;

  logonState: number;

  shoppingContext:CartShoppingContextVO;

  orderInfo:CartOrderInfoVO;

  total:CartTotalVO;

}
