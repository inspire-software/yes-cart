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


import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Config } from '../config/env.config';
import { FulfilmentCentreInfoVO, FulfilmentCentreVO, ShopFulfilmentCentreVO, InventoryVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class FulfilmentService {

  private _serviceBaseUrl = Config.API + 'service/fulfilment';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('FulfilmentService constructed');
  }

  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllFulfilmentCentres() {
    return this.http.get(this._serviceBaseUrl + '/centre/all')
      .map(res => <FulfilmentCentreVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopFulfilmentCentres(shopId:number) {
    return this.http.get(this._serviceBaseUrl + '/centre/shop/' + shopId)
      .map(res => <ShopFulfilmentCentreVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Create/update centre.
   * @param centre centre
   * @returns {Observable<R>}
   */
  saveFulfilmentCentre(centre:FulfilmentCentreVO) {

    let body = JSON.stringify(centre);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (centre.warehouseId > 0) {
      return this.http.post(this._serviceBaseUrl + '/centre', body, options)
        .map(res => <FulfilmentCentreVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/centre', body, options)
        .map(res => <FulfilmentCentreVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Create centre on the fly.
   * @param centre name of centre
   * @param shopId shop id
   * @returns {Observable<R>}
     */
  createFulfilmentCentre(centre:FulfilmentCentreInfoVO, shopId : number) {
    let body = JSON.stringify(centre);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl + '/centre/shop/' + shopId, body, options)
      .map(res => <FulfilmentCentreVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Update centres.
   * @param centres fulfilment centres
   * @returns {Observable<R>}
   */
  saveShopFulfilmentCentres(carriers:Array<ShopFulfilmentCentreVO>) {

    let body = JSON.stringify(carriers);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/centre/shop', body, options)
      .map(res => <Array<ShopFulfilmentCentreVO>> res.json())
      .catch(this.handleError);
  }


  /**
   * Remove centre.
   * @param centre carrier
   * @returns {Observable<R>}
   */
  removeFulfilmentCentre(centre:FulfilmentCentreVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/centre/' + centre.warehouseId, options)
      .catch(this.handleError);
  }


  /**
   * Get list of all inventory, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredInventory(centre:FulfilmentCentreInfoVO, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/inventory/centre/' + centre.warehouseId + '/filtered/' + max, body, options)
      .map(res => <InventoryVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Get inventory, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getInventoryById(inventoryId:number) {
    return this.http.get(this._serviceBaseUrl + '/inventory/' + inventoryId)
      .map(res => <InventoryVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Create/update inventory.
   * @param inventory inventory
   * @returns {Observable<R>}
   */
  saveInventory(inventory:InventoryVO) {

    let body = JSON.stringify(inventory);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (inventory.skuWarehouseId > 0) {
      return this.http.post(this._serviceBaseUrl + '/inventory', body, options)
        .map(res => <InventoryVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/inventory', body, options)
        .map(res => <InventoryVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Remove inventory.
   * @param inventory inventory
   * @returns {Observable<R>}
   */
  removeInventory(inventory:InventoryVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/inventory/' + inventory.skuWarehouseId, options)
      .catch(this.handleError);
  }



  private handleError (error:any) {

    LogUtil.error('FulfilmentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
