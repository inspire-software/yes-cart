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

export interface FulfilmentCentreInfoVO {

  warehouseId : number;
  code : string;
  name : string;
  description : string;
  countryCode : string;
  stateCode : string;
  city : string;
  postcode : string;

  defaultStandardStockLeadTime : number;
  defaultBackorderStockLeadTime : number;
  multipleShippingSupported : boolean;

  displayNames : Pair<string, string>[];

}

export interface FulfilmentCentreShopLinkVO {

  warehouseId : number;
  shopId : number;
  disabled : boolean;

}

export interface FulfilmentCentreVO extends FulfilmentCentreInfoVO {

  fulfilmentShops : Array<FulfilmentCentreShopLinkVO>;

}

export interface ShopFulfilmentCentreVO extends FulfilmentCentreInfoVO {

  fulfilmentShop : FulfilmentCentreShopLinkVO;

}

export interface InventoryVO {

  skuWarehouseId : number;

  skuCode : string;
  skuName : string;

  warehouseCode : string;
  warehouseName : string;

  quantity : number;
  reserved : number;

}

