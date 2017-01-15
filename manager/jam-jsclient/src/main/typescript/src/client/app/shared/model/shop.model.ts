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

/**
 * Contains all models related to shop.
 */


import { Pair } from './common.model';
import { AttrValueVO } from './attribute.model';

/**
 * Represent simple data about shop
 */
export interface ShopVO {
  shopId : number;
  disabled : boolean;
  code : string;
  name :string;
  description : string;
  fspointer : string;
}

/**
 * Represent SEO information for shop.
 */
export interface ShopSeoVO  {

  shopId : number;

  uri : string;
  title : string;
  metakeywords : string;
  metadescription : string;

  displayTitles : Pair<string, string>[];
  displayMetakeywords : Pair<string, string>[];
  displayMetadescriptions : Pair<string, string>[];

}

/**
 * Record in shop urls.
 */
export interface UrlVO {
  urlId : number;
  url : string;
  theme : string;
  primary : boolean;
}

/**
 * Shop urls.
 */
export interface ShopUrlVO {
  shopId : number;
  previewUrl : string;
  previewCss : string;
  urls : Array<UrlVO>;
}

/**
 * Represent supported currencies.
 */
export interface ShopSupportedCurrenciesVO {
  shopId : number;
  all : Array<string>;
  supported : Array<string>;
}


/**
 * Represent supported languages by shop.
 */
export interface ShopLanguagesVO {
  shopId : number;
  all : Array<Pair<string, string>>;
  supported : Array<string>;
}


/**
 * Represent supported locations by shop.
 */
export interface ShopLocationsVO {
  shopId : number;
  all : Array<Pair<string, string>>;
  supportedBilling : Array<string>;
  supportedShipping : Array<string>;
}

export interface AttrValueShopVO extends AttrValueVO {

  shopId : number;

}

/**
 * Represent simple data about shop
 */
export interface ShopSummaryVO {

  shopId : number;
  disabled : boolean;
  code : string;
  name :string;
  themeChain : string;

  categories: Pair<string, boolean>[];

  carriers: Pair<string, boolean>[];

  fulfilmentCentres: Pair<string, boolean>[];

  paymentGateways: Pair<string, boolean>[];
  paymentGatewaysIPsRegEx:string;

  locales: Pair<string, string>[];
  currencies: Pair<string, string>[];

  billingLocations: Pair<string, string>[];
  shippingLocations: Pair<string, string>[];

  previewUrl: string;
  previewCss: string;
  primaryUrlAndThemeChain: Pair<string, string>;
  aliasUrlAndThemeChain: Pair<string, string>[];

  checkoutEnableGuest: Pair<string, boolean>;
  checkoutEnableCoupons: Pair<string, boolean>;
  checkoutEnableMessage: Pair<string, boolean>;
  checkoutEnableQuanityPicker: Pair<string, boolean>;

  taxEnableShow: Pair<string, boolean>;
  taxEnableShowNet: Pair<string, boolean>;
  taxEnableShowAmount: Pair<string, boolean>;

  cookiePolicy: Pair<string, boolean>;
  anonymousBrowsing: Pair<string, boolean>;
  customerSession: Pair<string, string>;
  customerTypes: Pair<string, string>[];
  customerTypesAbleToRegister: Pair<string, string[]>;
  customerTypesRequireRegistrationApproval: Pair<string, string[]>;
  customerTypesRequireRegistrationNotification: Pair<string, string[]>;
  customerTypesSeeTax: Pair<string, string[]>;
  customerTypesChangeTaxView: Pair<string, string[]>;

  emailTemplates: Pair<string, boolean[]>;
  emailTemplatesFrom: Pair<string, string[]>;
  emailTemplatesTo: Pair<string, string[]>;
  emailTemplatesShop: Pair<string, boolean[]>;
}



