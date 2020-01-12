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
import { Http, Response } from '@angular/http';
import { Config } from '../config/env.config';
import {
  FulfilmentCentreInfoVO, FulfilmentCentreVO, ShopFulfilmentCentreVO,
  InventoryVO,
  SearchContextVO, SearchResultVO
} from '../model/index';
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
  getFilteredFulfilmentCentres(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/centre/filtered', body,
      Util.requestOptions())
      .map(res => <SearchResultVO<FulfilmentCentreVO>> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopFulfilmentCentres(shopId:number) {
    return this.http.get(this._serviceBaseUrl + '/centre/shop/' + shopId, Util.requestOptions())
      .map(res => <ShopFulfilmentCentreVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFulfilmentCentreById(centreId:number) {
    return this.http.get(this._serviceBaseUrl + '/centre/' + centreId, Util.requestOptions())
      .map(res => <ShopFulfilmentCentreVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update centre.
   * @param centre centre
   * @returns {Observable<R>}
   */
  saveFulfilmentCentre(centre:FulfilmentCentreVO) {

    let body = JSON.stringify(centre);

    if (centre.warehouseId > 0) {
      return this.http.post(this._serviceBaseUrl + '/centre', body, Util.requestOptions())
        .map(res => <FulfilmentCentreVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/centre', body, Util.requestOptions())
        .map(res => <FulfilmentCentreVO> this.json(res))
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

    return this.http.put(this._serviceBaseUrl + '/centre/shop/' + shopId, body, Util.requestOptions())
      .map(res => <FulfilmentCentreVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update centres.
   * @param centres fulfilment centres
   * @returns {Observable<R>}
   */
  saveShopFulfilmentCentres(centres:Array<ShopFulfilmentCentreVO>) {

    let body = JSON.stringify(centres);

    return this.http.post(this._serviceBaseUrl + '/centre/shop', body, Util.requestOptions())
      .map(res => <Array<ShopFulfilmentCentreVO>> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Remove centre.
   * @param centre carrier
   * @returns {Observable<R>}
   */
  removeFulfilmentCentre(centre:FulfilmentCentreVO) {

    return this.http.delete(this._serviceBaseUrl + '/centre/' + centre.warehouseId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get list of all inventory, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredInventory(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/inventory/centre/filtered', body,
          Util.requestOptions())
      .map(res => <SearchResultVO<InventoryVO>> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get inventory, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getInventoryById(inventoryId:number) {
    return this.http.get(this._serviceBaseUrl + '/inventory/' + inventoryId, Util.requestOptions())
      .map(res => <InventoryVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create/update inventory.
   * @param inventory inventory
   * @returns {Observable<R>}
   */
  saveInventory(inventory:InventoryVO) {

    let body = JSON.stringify(inventory);

    if (inventory.skuWarehouseId > 0) {
      return this.http.post(this._serviceBaseUrl + '/inventory', body, Util.requestOptions())
        .map(res => <InventoryVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/inventory', body, Util.requestOptions())
        .map(res => <InventoryVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove inventory.
   * @param inventory inventory
   * @returns {Observable<R>}
   */
  removeInventory(inventory:InventoryVO) {

    return this.http.delete(this._serviceBaseUrl + '/inventory/' + inventory.skuWarehouseId, Util.requestOptions())
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

    LogUtil.error('FulfilmentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
