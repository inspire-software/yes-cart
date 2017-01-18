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
import { CarrierLocaleVO, CarrierVO, ShopCarrierVO, CarrierSlaVO } from '../model/index';
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

  private _serviceBaseUrl = Config.API + 'service/shipping';  // URL to web api

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
  getAllCarriers() {
    return this.http.get(this._serviceBaseUrl + '/carrier/all')
      .map(res => <CarrierVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all carriers, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopCarriers(shopId:number) {
    return this.http.get(this._serviceBaseUrl + '/carrier/shop/' + shopId)
      .map(res => <ShopCarrierVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Create/update carrier.
   * @param carrier carrier
   * @returns {Observable<R>}
   */
  saveCarrier(carrier:CarrierVO) {

    let body = JSON.stringify(carrier);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (carrier.carrierId > 0) {
      return this.http.post(this._serviceBaseUrl + '/carrier', body, options)
        .map(res => <CarrierVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/carrier', body, options)
        .map(res => <CarrierVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Create carrier on the fly.
   * @param carrier name of carrier
   * @param shopId shop id
   * @returns {Observable<R>}
     */
  createCarrier(carrier:CarrierLocaleVO, shopId : number) {
    let body = JSON.stringify(carrier);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl + '/carrier/shop/' + shopId, body, options)
      .map(res => <CarrierVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Update carrier.
   * @param carriers carriers
   * @returns {Observable<R>}
   */
  saveShopCarriers(carriers:Array<ShopCarrierVO>) {

    let body = JSON.stringify(carriers);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/carrier/shop', body, options)
      .map(res => <Array<ShopCarrierVO>> res.json())
      .catch(this.handleError);
  }


  /**
   * Remove carrier.
   * @param carrier carrier
   * @returns {Observable<R>}
   */
  removeCarrier(carrier:CarrierVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/carrier/' + carrier.carrierId, options)
      .catch(this.handleError);
  }


  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCarrierSlas(carrierId:number) {
    return this.http.get(this._serviceBaseUrl + '/carriersla/all/' + carrierId)
      .map(res => <CarrierSlaVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all carriers SLA, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredCarrierSlas(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/carriersla/filtered/' + max, body, options)
      .map(res => <CarrierSlaVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Create carrier SLA.
   * @param sla SLA
   * @returns {Observable<R>}
   */
  saveCarrierSla(sla:CarrierSlaVO) {

    let body = JSON.stringify(sla);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (sla.carrierslaId > 0) {
      return this.http.post(this._serviceBaseUrl + '/carriersla', body, options)
        .map(res => <CarrierSlaVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/carriersla', body, options)
        .map(res => <CarrierSlaVO> res.json())
        .catch(this.handleError);
    }
  }



  /**
   * Remove carrier SLA.
   * @param sla SLA
   * @returns {Observable<R>}
   */
  removeCarrierSla(sla:CarrierSlaVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/carriersla/' + sla.carrierslaId, options)
      .catch(this.handleError);
  }


  private handleError (error:any) {

    LogUtil.error('ShippingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
