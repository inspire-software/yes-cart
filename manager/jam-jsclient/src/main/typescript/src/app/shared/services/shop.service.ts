/*
 * Copyright 2009 Inspire-Software.com
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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Config } from '../../../environments/environment';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { ShopVO, ShopUrlVO, ShopAliasVO, ShopSeoVO, ShopSupportedCurrenciesVO, ShopLanguagesVO, ShopLocationsVO, AttrValueShopVO, CategoryVO, Pair, ShopSummaryVO, SubShopVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ShopService {

  private _serviceBaseUrl = Config.API + 'service/shops';  // URL to web api

  /**
   * Construct shop service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('ShopService constructed');
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Observable<T>}
   */
  getAllShops():Observable<ShopVO[]> {
    return this.http.get<ShopVO[]>(this._serviceBaseUrl, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get detail shop information by given id.
   * @param id giveh shop id.
   * @returns {Observable<T>}
   */
  getShop(id:number):Observable<ShopVO> {
    LogUtil.debug('ShopService get shop by id ' + id);
    return this.http.get<ShopVO>(this._serviceBaseUrl + '/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Observable<T>}
   */
  getSubShops(id:number):Observable<ShopVO[]> {
    return this.http.get<ShopVO[]>(this._serviceBaseUrl + '/' + id + '/subs', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create empty shop detail.
   * @returns {Promise<ShopVO>}
   */
  createShop():Promise<ShopVO> {
    let shopVOTemplate : ShopVO = {'shopId': 0, 'disabled': false, 'code' : '', 'masterId': undefined, 'masterCode': null, 'name': '', 'description' : '', 'fspointer' : ''};
    let newShop : ShopVO = Util.clone(shopVOTemplate);
    return Promise.resolve(newShop);
  }


  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop
   * @returns {Observable<T>}
   */
  saveSubShop(shop:SubShopVO):Observable<ShopVO> {
    LogUtil.debug('ShopService save sub shop ', shop);

    let body = JSON.stringify(shop);

    return this.http.post<ShopVO>(this._serviceBaseUrl + '/subs', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop
   * @returns {Observable<T>}
   */
  saveShop(shop:ShopVO):Observable<ShopVO> {
    LogUtil.debug('ShopService save shop ', shop);

    let body = JSON.stringify(shop);

    if (shop.shopId === 0) {
      return this.http.post<ShopVO>(this._serviceBaseUrl, body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.put<ShopVO>(this._serviceBaseUrl, body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop shop
   * @param state enabled or disabled
   * @returns {Observable<T>}
   */
  updateDisabledFlag(shop:ShopVO, state:boolean):Observable<ShopVO> {
    LogUtil.debug('ShopService change state shop ', shop.shopId, state);

    let body = JSON.stringify({ disabled: state });

    return this.http.post<ShopVO>(this._serviceBaseUrl + '/' + shop.shopId + '/status', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get summary information for given shop id.
   * @param shopId given shop id
   * @param lang language
   * @return {Promise<ShopSummaryVO>}
   */
  getShopSummary(shopId:number, lang:string):Observable<ShopSummaryVO> {
    LogUtil.debug('ShopService get shop summary info ', shopId, lang);
    return this.http.get<ShopSummaryVO>(this._serviceBaseUrl + '/' + shopId + '/summary?lang=' + lang, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get localization information for given shop id.
   * @param shopId given shop id
   * @return {Promise<ShopSeoVO>}
   */
  getShopSeo(shopId:number):Observable<ShopSeoVO> {
    LogUtil.debug('ShopService get shop seo info ', shopId);
    return this.http.get<ShopSeoVO>(this._serviceBaseUrl + '/' + shopId + '/seo', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Save changes in localisation information
   * @param shopSeoVO
   * @returns {Promise<ShopSeoVO>}
   */
  saveShopSeo(shopSeoVO:ShopSeoVO):Observable<ShopSeoVO> {
    LogUtil.debug('ShopService save localization info', shopSeoVO);

    let body = JSON.stringify(shopSeoVO);
    return this.http.put<ShopSeoVO>(this._serviceBaseUrl + '/seo', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<T>}
   */
  getShopUrls(id:number):Observable<ShopUrlVO> {
    return this.http.get<ShopUrlVO>(this._serviceBaseUrl + '/' + id + '/urls', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Save changes in list of shop urls
   * @param shopUrl
   * @returns {Promise<ShopUrlVO>}
   */
  saveShopUrls(shopUrl:ShopUrlVO):Observable<ShopUrlVO> {

    let body = JSON.stringify(shopUrl);

    return this.http.put<ShopUrlVO>(this._serviceBaseUrl + '/urls', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));

  }


  /**
   * Get aliases for gien shop id.
   * @param id
   * @returns {Observable<T>}
   */
  getShopAliases(id:number):Observable<ShopAliasVO> {
    return this.http.get<ShopAliasVO>(this._serviceBaseUrl + '/' + id + '/aliases', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Save changes in list of shop aliases
   * @param shopAlias
   * @returns {Promise<ShopAliasVO>}
   */
  saveShopAliases(shopAlias:ShopAliasVO):Observable<ShopAliasVO> {

    let body = JSON.stringify(shopAlias);

    return this.http.put<ShopAliasVO>(this._serviceBaseUrl + '/aliases', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));

  }


  /**
   * Get currencies for given shop id.
   * @param id
   * @returns {Observable<T>}
   */
  getShopCurrencies(id:number):Observable<ShopSupportedCurrenciesVO> {
    return this.http.get<ShopSupportedCurrenciesVO>(this._serviceBaseUrl + '/' + id + '/currencies', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Update supported currencies.
   * @param curr
   * @returns {Observable<T>}
   */
  saveShopCurrencies(curr:ShopSupportedCurrenciesVO):Observable<ShopSupportedCurrenciesVO> {
    let body = JSON.stringify(curr);
    return this.http.put<ShopSupportedCurrenciesVO>(this._serviceBaseUrl + '/currencies', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get languages for given shop id.
   * @param id
   * @returns {Observable<T>}
   */
  getShopLanguages(id:number):Observable<ShopLanguagesVO> {
    return this.http.get<ShopLanguagesVO>(this._serviceBaseUrl + '/' + id + '/languages', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Update supported languages.
   * @param langs
   * @returns {Observable<T>}
   */
  saveShopLanguages(langs:ShopLanguagesVO):Observable<ShopLanguagesVO> {
    let body = JSON.stringify(langs);
    return this.http.put<ShopLanguagesVO>(this._serviceBaseUrl + '/languages', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get urls for given shop id.
   * @param id
   * @returns {Observable<T>}
   */
  getShopLocations(id:number):Observable<ShopLocationsVO> {
    return this.http.get<ShopLocationsVO>(this._serviceBaseUrl + '/' + id + '/locations', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported locations.
   * @param locs
   * @returns {Observable<T>}
   */
  saveShopLocations(locs:ShopLocationsVO):Observable<ShopLocationsVO> {
    let body = JSON.stringify(locs);
    return this.http.put<ShopLocationsVO>(this._serviceBaseUrl + '/locations', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get all categories assigned to given shop.
   * @param id shop id
   * @returns {Observable<T>}
   */
  getShopCategories(id:number):Observable<CategoryVO[]> {
    return this.http.get<CategoryVO[]>(this._serviceBaseUrl + '/' + id + '/categories', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Save changes in shop categories.
   * @param shopId
   * @param cats
   * @returns {Observable<T>}
   */
  saveShopCategories(shopId:number, cats : CategoryVO[]):Observable<CategoryVO[]> {
    let body = JSON.stringify(cats);
    LogUtil.debug('Save assigned categories ', cats);
    return this.http.put<CategoryVO[]>(this._serviceBaseUrl + '/' + shopId + '/categories', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get attributes for given shop id.
   * @param id
   * @param includeSecure
   * @returns {Observable<T>}
   */
  getShopAttributes(id:number, includeSecure:boolean):Observable<AttrValueShopVO[]> {
    return this.http.get<AttrValueShopVO[]>(this._serviceBaseUrl + '/' + id + '/attributes?includeSecure=' + includeSecure, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @param includeSecure
   * @returns {Observable<T>}
   */
  saveShopAttributes(attrs:Array<Pair<AttrValueShopVO, boolean>>, includeSecure:boolean):Observable<AttrValueShopVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueShopVO[]>(this._serviceBaseUrl + '/attributes?includeSecure=' + includeSecure, body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('ShopService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
