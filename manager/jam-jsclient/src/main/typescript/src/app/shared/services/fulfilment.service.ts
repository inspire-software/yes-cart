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
import {
  FulfilmentCentreInfoVO, FulfilmentCentreVO, ShopFulfilmentCentreVO,
  InventoryVO,
  SearchContextVO, SearchResultVO
} from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class FulfilmentService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('FulfilmentService constructed');
  }

  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredFulfilmentCentres(filter:SearchContextVO):Observable<SearchResultVO<FulfilmentCentreInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<FulfilmentCentreInfoVO>>(this._serviceBaseUrl + '/fulfilment/centres/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getShopFulfilmentCentres(shopId:number):Observable<ShopFulfilmentCentreVO[]> {
    return this.http.get<ShopFulfilmentCentreVO[]>(this._serviceBaseUrl + '/shops/' + shopId + '/centres', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all fulfilment centres, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFulfilmentCentreById(centreId:number):Observable<ShopFulfilmentCentreVO> {
    return this.http.get<ShopFulfilmentCentreVO>(this._serviceBaseUrl + '/fulfilment/centres/' + centreId,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update centre.
   * @param centre centre
   * @returns {Observable<T>}
   */
  saveFulfilmentCentre(centre:FulfilmentCentreVO):Observable<FulfilmentCentreVO> {

    let body = JSON.stringify(centre);

    if (centre.warehouseId > 0) {
      return this.http.put<FulfilmentCentreVO>(this._serviceBaseUrl + '/fulfilment/centres', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<FulfilmentCentreVO>(this._serviceBaseUrl + '/fulfilment/centres', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Create centre on the fly.
   * @param centre name of centre
   * @param shopId shop id
   * @returns {Observable<T>}
   */
  createFulfilmentCentre(centre:FulfilmentCentreInfoVO, shopId : number):Observable<FulfilmentCentreVO> {
    let body = JSON.stringify(centre);

    return this.http.post<FulfilmentCentreVO>(this._serviceBaseUrl + '/shops/' + shopId + '/centres', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update centres.
   * @param centres fulfilment centres
   * @returns {Observable<T>}
   */
  saveShopFulfilmentCentres(centres:Array<ShopFulfilmentCentreVO>):Observable<ShopFulfilmentCentreVO[]> {

    let body = JSON.stringify(centres);

    return this.http.put<ShopFulfilmentCentreVO[]>(this._serviceBaseUrl + '/fulfilment/centres/shops', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Remove centre.
   * @param centre carrier
   * @returns {Observable<T>}
   */
  removeFulfilmentCentre(centre:FulfilmentCentreVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/fulfilment/centres/' + centre.warehouseId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get list of all inventory, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredInventory(filter:SearchContextVO):Observable<SearchResultVO<InventoryVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<InventoryVO>>(this._serviceBaseUrl + '/fulfilment/inventory/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get inventory, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getInventoryById(inventoryId:number):Observable<InventoryVO> {
    return this.http.get<InventoryVO>(this._serviceBaseUrl + '/fulfilment/inventory/' + inventoryId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create/update inventory.
   * @param inventory inventory
   * @returns {Observable<T>}
   */
  saveInventory(inventory:InventoryVO):Observable<InventoryVO> {

    let body = JSON.stringify(inventory);

    if (inventory.skuWarehouseId > 0) {
      return this.http.put<InventoryVO>(this._serviceBaseUrl + '/fulfilment/inventory', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<InventoryVO>(this._serviceBaseUrl + '/fulfilment/inventory', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove inventory.
   * @param inventory inventory
   * @returns {Observable<T>}
   */
  removeInventory(inventory:InventoryVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/fulfilment/inventory/' + inventory.skuWarehouseId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  private handleError (error:any) {

    LogUtil.error('FulfilmentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
