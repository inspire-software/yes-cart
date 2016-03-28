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

  displayTitles : Array<Pair<string, string>>;
  displayMetakeywords : Array<Pair<string, string>>;
  displayMetadescriptions : Array<Pair<string, string>>;

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


