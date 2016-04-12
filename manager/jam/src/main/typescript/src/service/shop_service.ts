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

import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Util} from './util';
import {ShopVO, ShopLocaleVO, ShopSupportedCurrenciesVO} from './../model/shop';
import {ShopUrlVO} from '../model/shop';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ShopService {

  private _shopUrl = '../service/shop';  // URL to web api

  /**
   * Constrcut shop service, which has methods to work with information related to shop(s).
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
    return this.http.get(this._shopUrl + '/all')
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
    return this.http.get(this._shopUrl + '/' + id)
      .map(res => <ShopVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create empty shop detail.
   * @returns {Promise<ShopVO>}
   */
  createShop() {
    var shopVOTemplate : ShopVO = {'shopId': 0, 'code' : '', 'name': '', 'description' : '', 'fspointer' : ''};
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
      return this.http.put(this._shopUrl, body, options)
        .map(res => <ShopVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.post(this._shopUrl, body, options)
        .map(res => <ShopVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Get localization information for given shop id.
   * @param id given shop id
   * @return {Promise<ShopLocaleVO>}
   */
  getShopLocalization(shopId:number) {
    console.debug('ShopService get shop localization info ' + shopId);
    return this.http.get(this._shopUrl + '/localization/' + shopId)
      .map(res => <ShopLocaleVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Save changes in localisation information
   * @param shopLocaleVO
   * @returns {Promise<ShopLocaleVO>}
     */
  saveShopLocalization(shopLocaleVO:ShopLocaleVO) {
    console.debug('ShopService save localization info ' + JSON.stringify(shopLocaleVO));

    let body = JSON.stringify(shopLocaleVO);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._shopUrl + '/localization', body, options)
      .map(res => <ShopLocaleVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<R>}
     */
  getShopUrls(id:number) {
    return this.http.get(this._shopUrl + '/urls/' + id)
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

    return this.http.post(this._shopUrl + '/urls', body, options)
      .map(res => <ShopUrlVO> res.json())
      .catch(this.handleError);

  }

  /**
   * Get urls for gien shop id.
   * @param id
   * @returns {Observable<R>}
   */
  getShopCurrencies(id:number) {
    return this.http.get(this._shopUrl + '/currencies/' + id)
      .map(res => <ShopSupportedCurrenciesVO> res.json())
      .catch(this.handleError);
  }

  saveShopCurrencies(curr:ShopSupportedCurrenciesVO) {

    let body = JSON.stringify(curr);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._shopUrl + '/currencies', body, options)
      .map(res => <ShopSupportedCurrenciesVO> res.json())
      .catch(this.handleError);

  }


  private handleError (error: Response) {
    // in a real world app, we may send the error to some remote logging infrastructure
    // instead of just logging it to the console
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }

}
