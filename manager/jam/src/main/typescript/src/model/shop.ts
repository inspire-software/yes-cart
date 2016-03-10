/**
 * Contains all models related to shop.
 */


import {Pair} from './common';
/**
 * Represent simple data about shop
 */
export interface ShopVO {
  shopId : number;
  code : string;
  name :string;
  description : string;
  theme : string;
}

/**
 * Represent localization information for shop.
 */
export interface ShopLocaleVO  {
  shopId : number;
  windowTitle : Array<Pair<string, string>>;
  metaKeywords : Array<Pair<string, string>>;
  metaDesciption : Array<Pair<string, string>>;
}

export interface UrlVO {
  urlId : number;
  url : string;
  theme : string;
}

export interface ShopUrlVO {
  shopId : number;
  urls : Array<UrlVO>;
}


