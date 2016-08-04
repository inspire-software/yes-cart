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


import {Pair} from './common.model';
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
 * Represent localization information for shop.
 */
export interface ShopLocaleVO  {

  shopId : number;

  uri : string;
  title : string;
  metakeywords : string;
  metadescription : string;

  displayTitles : any;
  displayMetakeywords : any;
  displayMetadescriptions : any;

}

/**
 * Record in shopo urls.
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


