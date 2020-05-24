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
import { Http, Response } from '@angular/http';
import { Config } from '../config/env.config';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { ShopVO, ShopUrlVO, ShopAliasVO, ShopSeoVO, ShopSupportedCurrenciesVO, ShopLanguagesVO, ShopLocationsVO, AttrValueShopVO, CategoryVO, Pair, ShopSummaryVO, SubShopVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

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
  constructor (private http: Http) {
    LogUtil.debug('ShopService constructed');
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllShops() {
    return this.http.get(this._serviceBaseUrl, Util.requestOptions())
      .map(res => <ShopVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get detail shop information by given id.
   * @param id giveh shop id.
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShop(id:number) {
    LogUtil.debug('ShopService get shop by id ' + id);
    return this.http.get(this._serviceBaseUrl + '/' + id, Util.requestOptions())
      .map(res => <ShopVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getSubShops(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/subs', Util.requestOptions())
      .map(res => <ShopVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create empty shop detail.
   * @returns {Promise<ShopVO>}
   */
  createShop() {
    let shopVOTemplate : ShopVO = {'shopId': 0, 'disabled': false, 'code' : '', 'masterId': undefined, 'masterCode': null, 'name': '', 'description' : '', 'fspointer' : ''};
    let newShop : ShopVO = Util.clone(shopVOTemplate);
    return Promise.resolve(newShop);
  }


  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  saveSubShop(shop:SubShopVO) {
    LogUtil.debug('ShopService save sub shop ', shop);

    let body = JSON.stringify(shop);

    return this.http.post(this._serviceBaseUrl + '/subs', body, Util.requestOptions())
      .map(res => <ShopVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  saveShop(shop:ShopVO) {
    LogUtil.debug('ShopService save shop ', shop);

    let body = JSON.stringify(shop);

    if (shop.shopId === 0) {
      return this.http.post(this._serviceBaseUrl, body, Util.requestOptions())
        .map(res => <ShopVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl, body, Util.requestOptions())
        .map(res => <ShopVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Save or create given shop detail - the root of shop related information.
   * @param shop shop
   * @param state enabled or disabled
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDisabledFlag(shop:ShopVO, state:boolean) {
    LogUtil.debug('ShopService change state shop ', shop.shopId, state);

    let body = JSON.stringify({ disabled: state });

    return this.http.post(this._serviceBaseUrl + '/' + shop.shopId + '/status', body, Util.requestOptions())
      .map(res => <ShopVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get summary information for given shop id.
   * @param shopId given shop id
   * @param lang language
   * @return {Promise<ShopSummaryVO>}
   */
  getShopSummary(shopId:number, lang:string) {
    LogUtil.debug('ShopService get shop summary info ', shopId, lang);
    return this.http.get(this._serviceBaseUrl + '/' + shopId + '/summary?lang=' + lang, Util.requestOptions())
      .map(res => <ShopSummaryVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get localization information for given shop id.
   * @param shopId given shop id
   * @return {Promise<ShopSeoVO>}
   */
  getShopSeo(shopId:number) {
    LogUtil.debug('ShopService get shop seo info ', shopId);
    return this.http.get(this._serviceBaseUrl + '/' + shopId + '/seo', Util.requestOptions())
      .map(res => <ShopSeoVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Save changes in localisation information
   * @param shopSeoVO
   * @returns {Promise<ShopSeoVO>}
     */
  saveShopSeo(shopSeoVO:ShopSeoVO) {
    LogUtil.debug('ShopService save localization info', shopSeoVO);

    let body = JSON.stringify(shopSeoVO);
    return this.http.put(this._serviceBaseUrl + '/seo', body, Util.requestOptions())
      .map(res => <ShopSeoVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<R>}
     */
  getShopUrls(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/urls', Util.requestOptions())
      .map(res => <ShopUrlVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Save changes in list of shop urls
   * @param shopUrl
   * @returns {Promise<ShopUrlVO>}
     */
  saveShopUrls(shopUrl:ShopUrlVO) {

    let body = JSON.stringify(shopUrl);

    return this.http.put(this._serviceBaseUrl + '/urls', body, Util.requestOptions())
      .map(res => <ShopUrlVO> this.json(res))
      .catch(this.handleError);

  }


  /**
   * Get aliases for gien shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopAliases(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/aliases', Util.requestOptions())
      .map(res => <ShopAliasVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Save changes in list of shop aliases
   * @param shopAlias
   * @returns {Promise<ShopAliasVO>}
   */
  saveShopAliases(shopAlias:ShopAliasVO) {

    let body = JSON.stringify(shopAlias);

    return this.http.put(this._serviceBaseUrl + '/aliases', body, Util.requestOptions())
      .map(res => <ShopAliasVO> this.json(res))
      .catch(this.handleError);

  }


  /**
   * Get currencies for given shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopCurrencies(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/currencies', Util.requestOptions())
      .map(res => <ShopSupportedCurrenciesVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Update supported currencies.
   * @param curr
   * @returns {Observable<R>}
     */
  saveShopCurrencies(curr:ShopSupportedCurrenciesVO) {
    let body = JSON.stringify(curr);
    return this.http.put(this._serviceBaseUrl + '/currencies', body, Util.requestOptions())
      .map(res => <ShopSupportedCurrenciesVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get languages for given shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopLanguages(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/languages', Util.requestOptions())
      .map(res => <ShopLanguagesVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Update supported languages.
   * @param langs
   * @returns {Observable<R>}
   */
  saveShopLanguages(langs:ShopLanguagesVO) {
    let body = JSON.stringify(langs);
    return this.http.put(this._serviceBaseUrl + '/languages', body, Util.requestOptions())
      .map(res => <ShopLanguagesVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get urls for given shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopLocations(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/locations', Util.requestOptions())
      .map(res => <ShopLocationsVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported locations.
   * @param locs
   * @returns {Observable<R>}
   */
  saveShopLocations(locs:ShopLocationsVO) {
    let body = JSON.stringify(locs);
    return this.http.put(this._serviceBaseUrl + '/locations', body, Util.requestOptions())
      .map(res => <ShopLocationsVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get all categories assigned to given shop.
   * @param id shop id
   * @returns {Observable<R>}
   */
  getShopCategories(id:number) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/categories', Util.requestOptions())
      .map(res => <CategoryVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Save changes in shop categories.
   * @param shopId
   * @param cats
   * @returns {Observable<R>}
   */
  saveShopCategories(shopId:number, cats : CategoryVO[]) {
    let body = JSON.stringify(cats);
    LogUtil.debug('Save assigned categories ', cats);
    return this.http.put(this._serviceBaseUrl + '/' + shopId + '/categories', body, Util.requestOptions())
      .map(res => <CategoryVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get attributes for given shop id.
   * @param id
   * @param includeSecure
   * @returns {Observable<R>}
   */
  getShopAttributes(id:number, includeSecure:boolean) {
    return this.http.get(this._serviceBaseUrl + '/' + id + '/attributes?includeSecure=' + includeSecure, Util.requestOptions())
      .map(res => <AttrValueShopVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @param includeSecure
   * @returns {Observable<R>}
   */
  saveShopAttributes(attrs:Array<Pair<AttrValueShopVO, boolean>>, includeSecure:boolean) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + '/attributes?includeSecure=' + includeSecure, body, Util.requestOptions())
      .map(res => <ShopLocationsVO[]> this.json(res))
      .catch(this.handleError);
  }


  private json(res: Response): any {
    let contentType = res.headers.get('Content-Type');
    LogUtil.debug('Processing JSON response', contentType, res.text().includes('loginForm'));
    if (contentType != null && contentType.includes('text/html') && res.text().includes('loginForm')) {
      throw new Error('MODAL_ERROR_MESSAGE_AUTH');
    }
    return res.json();
  }


  private handleError (error:any) {

    LogUtil.error('ShopService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
