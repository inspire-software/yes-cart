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
  PaymentGatewayInfoVO, PaymentGatewayVO, PaymentGatewayParameterVO,
  Pair
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
export class PaymentService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('PaymentService constructed');
  }

  /**
   * Get list of all payment gateways, which are accessible to manage or view,
   * @param lang language
   * @returns {Observable<T>}
   */
  getPaymentGateways(lang:string):Observable<PaymentGatewayInfoVO[]> {
    return this.http.get<PaymentGatewayInfoVO[]>(this._serviceBaseUrl + '/paymentgateways?enabledOnly=false&lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all payment gateways enabled for shop, which are accessible to manage or view,
   * @param lang language
   * @param shopCode shop code
   * @returns {Observable<T>}
   */
  getPaymentGatewaysForShop(lang:string, shopCode:string):Observable<PaymentGatewayInfoVO[]> {
    return this.http.get<PaymentGatewayInfoVO[]>(this._serviceBaseUrl + '/shops/' + shopCode + '/paymentgateways?lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all payment gateways enabled on system, which are accessible to manage or view,
   * @param lang language
   * @returns {Observable<T>}
   */
  getAllowedPaymentGatewaysForShops(lang:string):Observable<PaymentGatewayInfoVO[]> {
    return this.http.get<PaymentGatewayInfoVO[]>(this._serviceBaseUrl + '/paymentgateways?enabledOnly=true&lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all payment gateways, which are accessible to manage or view,
   * @param lang language
   * @param shopCode shop code
   * @param includeSecure
   * @returns {Observable<T>}
   */
  getPaymentGatewaysWithParameters(lang:string, shopCode:string, includeSecure:boolean):Observable<PaymentGatewayVO[]> {

    let _path = '';
    if (shopCode != null) {
      _path = '/shops/' + shopCode + '/paymentgateways/details?includeSecure=' + includeSecure + '&lang=' + lang;
    } else {
      _path = '/paymentgateways/details?includeSecure=' + includeSecure + '&lang=' + lang;
    }

    return this.http.get<PaymentGatewayVO[]>(this._serviceBaseUrl + _path, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Update PG on/off flag.
   * @param shopCode
   * @param pgLabel
   * @param state
   * @returns {Observable<T>}
   */
  updateDisabledFlag(shopCode: string, pgLabel:string, state:boolean):Observable<boolean> {
    LogUtil.debug('PaymentService change state PG ' + pgLabel + ' for ' + shopCode + ' to ' + state ? 'online' : 'offline');

    let body = JSON.stringify({ disabled: state });

    let _path = '';
    if (shopCode != null) {
      _path = '/shops/' + shopCode + '/paymentgateways/' + pgLabel + '/status';
    } else {
      _path = '/paymentgateways/' + pgLabel + '/status';
    }

    return this.http.post(this._serviceBaseUrl +_path, body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Update supported attributes.
   * @param shopCode shop code
   * @param pgLabel
   * @param attrs
   * @param includeSecure
   * @returns {Observable<T>}
   */
  savePaymentGatewayParameters(shopCode: string, pgLabel:string, attrs:Array<Pair<PaymentGatewayParameterVO, boolean>>, includeSecure:boolean):Observable<PaymentGatewayParameterVO[]> {
    let body = JSON.stringify(attrs);

    let _path = '';
    if (shopCode != null) {
      _path = '/shops/' + shopCode + '/paymentgateways/' + pgLabel + '?includeSecure=' + includeSecure;
    } else {
      _path = '/paymentgateways/' + pgLabel + '?includeSecure=' + includeSecure;
    }

    return this.http.post<PaymentGatewayParameterVO[]>(this._serviceBaseUrl + _path , body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('PaymentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
