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
  CarrierInfoVO, CarrierVO, CarrierSlaVO, CarrierSlaInfoVO,
  ShopCarrierAndSlaVO, SearchContextVO, SearchResultVO
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
export class ShippingService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('ShippingService constructed');
  }

  /**
   * Get list of all carriers, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredCarriers(filter:SearchContextVO):Observable<SearchResultVO<CarrierInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CarrierInfoVO>>(this._serviceBaseUrl + '/shipping/carriers/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get carrier, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getCarrierById(carrierId:number):Observable<CarrierVO> {
    return this.http.get<CarrierVO>(this._serviceBaseUrl + '/shipping/carriers/' + carrierId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all carriers, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getShopCarriers(shopId:number):Observable<ShopCarrierAndSlaVO[]> {
    return this.http.get<ShopCarrierAndSlaVO[]>(this._serviceBaseUrl + '/shops/' + shopId + '/carriers', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update carrier.
   * @param carrier carrier
   * @returns {Observable<T>}
   */
  saveCarrier(carrier:CarrierVO):Observable<CarrierVO> {

    let body = JSON.stringify(carrier);

    if (carrier.carrierId > 0) {
      return this.http.put<CarrierVO>(this._serviceBaseUrl + '/shipping/carriers', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<CarrierVO>(this._serviceBaseUrl + '/shipping/carriers', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Create carrier on the fly.
   * @param carrier name of carrier
   * @param shopId shop id
   * @returns {Observable<T>}
   */
  createCarrier(carrier:CarrierInfoVO, shopId : number):Observable<CarrierVO> {
    let body = JSON.stringify(carrier);

    return this.http.post<CarrierVO>(this._serviceBaseUrl + '/shops/' + shopId + '/carriers', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update carrier.
   * @param carriers carriers
   * @returns {Observable<T>}
   */
  saveShopCarriers(carriers:Array<ShopCarrierAndSlaVO>):Observable<ShopCarrierAndSlaVO[]> {

    let body = JSON.stringify(carriers);

    return this.http.put<ShopCarrierAndSlaVO[]>(this._serviceBaseUrl + '/shops/carriers', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Remove carrier.
   * @param carrier carrier
   * @returns {Observable<T>}
   */
  removeCarrier(carrier:CarrierVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/shipping/carriers/' + carrier.carrierId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getCarrierSlas(carrierId:number):Observable<CarrierSlaVO[]> {
    return this.http.get<CarrierSlaVO[]>(this._serviceBaseUrl + '/shipping/carriers/' + carrierId + '/slas', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredCarrierSlas(filter:SearchContextVO):Observable<SearchResultVO<CarrierSlaInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CarrierSlaInfoVO>>(this._serviceBaseUrl + '/shipping/carrierslas/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create carrier SLA.
   * @param sla SLA
   * @returns {Observable<T>}
   */
  saveCarrierSla(sla:CarrierSlaVO):Observable<CarrierSlaVO> {

    let body = JSON.stringify(sla);

    if (sla.carrierslaId > 0) {
      return this.http.put<CarrierSlaVO>(this._serviceBaseUrl + '/shipping/carrierslas', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<CarrierSlaVO>(this._serviceBaseUrl + '/shipping/carrierslas', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }



  /**
   * Remove carrier SLA.
   * @param sla SLA
   * @returns {Observable<T>}
   */
  removeCarrierSla(sla:CarrierSlaVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/shipping/carrierslas/' + sla.carrierslaId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  private handleError (error:any) {

    LogUtil.error('ShippingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
