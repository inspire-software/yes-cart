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

import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Config} from '../config/env.config';
import {Util} from './util';
import {ShopVO, ShopUrlVO, ShopLocaleVO, ShopSupportedCurrenciesVO, ShopLanguagesVO, CategoryVO} from '../model/index';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ShopService {

  private _serviceBaseUrl = Config.API + 'service/shop';  // URL to web api

  /**
   * Construct shop service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('ShopService constructed');
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllShops() {
    return this.http.get(this._serviceBaseUrl + '/all')
      .map(res => <ShopVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get detail shop information by given id.
   * @param id giveh shop id.
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShop(id:number) {
    console.debug('ShopService get shop by id ' + id);
    return this.http.get(this._serviceBaseUrl + '/' + id)
      .map(res => <ShopVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create empty shop detail.
   * @returns {Promise<ShopVO>}
   */
  createShop() {
    var shopVOTemplate : ShopVO = {'shopId': 0, 'disabled': false, 'code' : '', 'name': '', 'description' : '', 'fspointer' : ''};
    var newShop : ShopVO = Util.clone(shopVOTemplate);
    return Promise.resolve(newShop);
  }

  /**
   * Save or create given shop detal - the root of shop related information.
   * @param shop
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  saveShop(shop:ShopVO) {
    console.debug('ShopService save shop ' + shop.shopId);

    let body = JSON.stringify(shop);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    if (shop.shopId === 0) {
      return this.http.put(this._serviceBaseUrl, body, options)
        .map(res => <ShopVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl, body, options)
        .map(res => <ShopVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Save or create given shop detal - the root of shop related information.
   * @param shop
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDisabledFlag(shop:ShopVO, state:boolean) {
    console.debug('ShopService change state shop ' + shop.shopId + ' to ' + state ? 'online' : 'offline');

    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/online/' + shop.shopId + '/' + state, null, options)
      .map(res => <ShopVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Get localization information for given shop id.
   * @param id given shop id
   * @return {Promise<ShopLocaleVO>}
   */
  getShopLocalization(shopId:number) {
    console.debug('ShopService get shop localization info ' + shopId);
    return this.http.get(this._serviceBaseUrl + '/localization/' + shopId)
      .map(res => <ShopLocaleVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Save changes in localisation information
   * @param shopLocaleVO
   * @returns {Promise<ShopLocaleVO>}
     */
  saveShopLocalization(shopLocaleVO:ShopLocaleVO) {
    console.debug('ShopService save localization info', shopLocaleVO);

    let body = JSON.stringify(shopLocaleVO);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/localization', body, options)
      .map(res => <ShopLocaleVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<R>}
     */
  getShopUrls(id:number) {
    return this.http.get(this._serviceBaseUrl + '/urls/' + id)
      .map(res => <ShopUrlVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Save changes in list of shop urls
   * @param shopUrl
   * @returns {Promise<ShopUrlVO>}
     */
  saveShopUrls(shopUrl:ShopUrlVO) {

    let body = JSON.stringify(shopUrl);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/urls', body, options)
      .map(res => <ShopUrlVO> res.json())
      .catch(this.handleError);

  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopCurrencies(id:number) {
    return this.http.get(this._serviceBaseUrl + '/currencies/' + id)
      .map(res => <ShopSupportedCurrenciesVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Update supported currencies.
   * @param curr
   * @returns {Observable<R>}
     */
  saveShopCurrencies(curr:ShopSupportedCurrenciesVO) {
    let body = JSON.stringify(curr);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/currencies', body, options)
      .map(res => <ShopSupportedCurrenciesVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Get urls for given shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopLanguages(id:number) {
    return this.http.get(this._serviceBaseUrl + '/languages/' + id)
      .map(res => <ShopLanguagesVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Get all categories assigned to given shop.
   * @param id shop id
   * @returns {Observable<R>}
     */
  getShopCategories(id:number) {
    return this.http.get(this._serviceBaseUrl + '/categories/' + id)
      .map(res => <CategoryVO[]> res.json())
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
    console.debug('Save assigned categories ' + body);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/categories/' + shopId, body, options)
      .map(res => <CategoryVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Update supported currencies.
   * @param curr
   * @returns {Observable<R>}
     */
  saveShopLanguages(curr:ShopLanguagesVO) {
    let body = JSON.stringify(curr);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/languages', body, options)
      .map(res => <ShopLanguagesVO> res.json())
      .catch(this.handleError);
  }


  private handleError (error: any) {
    // in a real world app, we may send the error to some remote logging infrastructure
    // instead of just logging it to the console
    console.error('ShopService Server error: ' + error['message'], error);
    if (error['message'].indexOf('JSON Parse error')) {
      return Observable.throw(error['message'] || 'Server error');
    }
    return Observable.throw(error.json().error || 'Server error');
  }

}
