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
import {PaymentGatewayInfoVO} from '../model/index';
import {ErrorEventBus} from './error-event-bus.service';
import {Util} from './util';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class PaymentService {

  private _serviceBaseUrl = Config.API + 'service/payment';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('PaymentService constructed');
  }

  /**
   * Get list of all payment gateways, which are accessible to manage or view,
   * @param lang language
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPaymentGateways(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/all/' + lang)
      .map(res => <PaymentGatewayInfoVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all payment gateways enabled for shop, which are accessible to manage or view,
   * @param lang language
   * @param shopCode shop code
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPaymentGatewaysForShop(lang:string, shopCode:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/shop/' + shopCode + '/' + lang)
      .map(res => <PaymentGatewayInfoVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all payment gateways enabled on system, which are accessible to manage or view,
   * @param lang language
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllowedPaymentGatewaysForShops(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/shop/allowed/' + lang)
      .map(res => <PaymentGatewayInfoVO[]> res.json())
      .catch(this.handleError);
  }

  private handleError (error:any) {

    console.error('PaymentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
