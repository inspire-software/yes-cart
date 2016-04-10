/**
 * Contains all models related to shop.
 */


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


