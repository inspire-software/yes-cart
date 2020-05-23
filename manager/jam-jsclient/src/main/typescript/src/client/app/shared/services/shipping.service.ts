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
import { CarrierInfoVO, CarrierVO, CarrierSlaVO, CarrierSlaInfoVO, ShopCarrierAndSlaVO, SearchContextVO, SearchResultVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

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
  constructor (private http: Http) {
    LogUtil.debug('ShippingService constructed');
  }

  /**
   * Get list of all carriers, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredCarriers(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/shipping/carriers/search', body, Util.requestOptions())
      .map(res => <SearchResultVO<CarrierInfoVO>> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get carrier, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCarrierById(carrierId:number) {
    return this.http.get(this._serviceBaseUrl + '/shipping/carriers/' + carrierId, Util.requestOptions())
      .map(res => <CarrierVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all carriers, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopCarriers(shopId:number) {
    return this.http.get(this._serviceBaseUrl + '/shops/' + shopId + '/carriers', Util.requestOptions())
      .map(res => <ShopCarrierAndSlaVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update carrier.
   * @param carrier carrier
   * @returns {Observable<R>}
   */
  saveCarrier(carrier:CarrierVO) {

    let body = JSON.stringify(carrier);

    if (carrier.carrierId > 0) {
      return this.http.put(this._serviceBaseUrl + '/shipping/carriers', body, Util.requestOptions())
        .map(res => <CarrierVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl + '/shipping/carriers', body, Util.requestOptions())
        .map(res => <CarrierVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Create carrier on the fly.
   * @param carrier name of carrier
   * @param shopId shop id
   * @returns {Observable<R>}
     */
  createCarrier(carrier:CarrierInfoVO, shopId : number) {
    let body = JSON.stringify(carrier);

    return this.http.post(this._serviceBaseUrl + '/shops/' + shopId + '/carriers', body, Util.requestOptions())
      .map(res => <CarrierVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update carrier.
   * @param carriers carriers
   * @returns {Observable<R>}
   */
  saveShopCarriers(carriers:Array<ShopCarrierAndSlaVO>) {

    let body = JSON.stringify(carriers);

    return this.http.put(this._serviceBaseUrl + '/shops/carriers', body, Util.requestOptions())
      .map(res => <Array<ShopCarrierAndSlaVO>> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Remove carrier.
   * @param carrier carrier
   * @returns {Observable<R>}
   */
  removeCarrier(carrier:CarrierVO) {

    return this.http.delete(this._serviceBaseUrl + '/shipping/carriers/' + carrier.carrierId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCarrierSlas(carrierId:number) {
    return this.http.get(this._serviceBaseUrl + '/shipping/carriers/' + carrierId + '/slas', Util.requestOptions())
      .map(res => <CarrierSlaVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredCarrierSlas(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/shipping/carriersla/search', body,
        Util.requestOptions())
      .map(res => <SearchResultVO<CarrierSlaInfoVO>> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create carrier SLA.
   * @param sla SLA
   * @returns {Observable<R>}
   */
  saveCarrierSla(sla:CarrierSlaVO) {

    let body = JSON.stringify(sla);

    if (sla.carrierslaId > 0) {
      return this.http.put(this._serviceBaseUrl + '/shipping/carrierslas', body, Util.requestOptions())
        .map(res => <CarrierSlaVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl + '/shipping/carrierslas', body, Util.requestOptions())
        .map(res => <CarrierSlaVO> this.json(res))
        .catch(this.handleError);
    }
  }



  /**
   * Remove carrier SLA.
   * @param sla SLA
   * @returns {Observable<R>}
   */
  removeCarrierSla(sla:CarrierSlaVO) {

    return this.http.delete(this._serviceBaseUrl + '/shipping/carrierslas/' + sla.carrierslaId, Util.requestOptions())
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

    LogUtil.error('ShippingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
