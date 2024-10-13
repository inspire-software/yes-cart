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
  CustomerOrderVO, CustomerOrderInfoVO, CustomerOrderDeliveryInfoVO, CustomerOrderTransitionResultVO,
  PaymentVO, SearchContextVO, SearchResultVO, CustomerOrderLineVO
} from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class CustomerOrderService {

  private _serviceBaseUrl = Config.API + 'service/customerorders';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('CustomerOrderService constructed');
  }

  /**
   * Get list of all orders, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredOrders(lang:string, filter:SearchContextVO):Observable<SearchResultVO<CustomerOrderInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CustomerOrderInfoVO>>(this._serviceBaseUrl + '/search?lang=' + lang, body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Get order, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getOrderById(lang:string, orderId:number):Observable<CustomerOrderVO> {
    return this.http.get<CustomerOrderVO>(this._serviceBaseUrl + '/' + orderId + '?lang=' + lang, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Transition order to next state.
   * @param order order
   * @param action action key from order next transitions
   * @param context additional key value pair for action (e.g. message, deliveryref)
   * @returns {Observable<T>}
   */
  transitionOrder(order:CustomerOrderInfoVO, action:string, context:any):Observable<CustomerOrderTransitionResultVO> {

    let body = JSON.stringify({ transition: action, context: context });

    return this.http.post<CustomerOrderTransitionResultVO>(this._serviceBaseUrl + '/transition/' + order.ordernum + '/', body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Transition order delivery to next state.
   * @param order order
   * @param delivery delivery
   * @param action action key from order next transitions
   * @param context additional key value pair for action (e.g. message, deliveryref)
   * @returns {Observable<T>}
   */
  transitionDelivery(order:CustomerOrderInfoVO, delivery:CustomerOrderDeliveryInfoVO, action:string, context:any):Observable<CustomerOrderTransitionResultVO> {

    let body = JSON.stringify({ transition: action, context: context });

    return this.http.post<CustomerOrderTransitionResultVO>(this._serviceBaseUrl + '/transition/' + order.ordernum + '/' + delivery.deliveryNum + '/', body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }

  /**
   * Transition order delivery to next state.
   * @param order order
   * @param delivery delivery
   * @param line order line
   * @param action action key from order next transitions
   * @param context additional key value pair for action (e.g. message, deliveryref)
   * @returns {Observable<T>}
   */
  transitionOrderLine(order:CustomerOrderInfoVO, delivery:CustomerOrderDeliveryInfoVO, line:CustomerOrderLineVO, action:string, context:any):Observable<CustomerOrderTransitionResultVO> {

    let body = JSON.stringify({ transition: action, context: context });

    return this.http.post<CustomerOrderTransitionResultVO>(this._serviceBaseUrl + '/transition/' + order.ordernum + '/' + (delivery ? delivery.deliveryNum : 'x') + '/' + line.skuCode, body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Export order
   * @returns {Observable<T>}
   */
  exportOrder(lang:string, orderId:number, doExport:boolean):Observable<CustomerOrderVO> {

    return this.http.post<CustomerOrderVO>(this._serviceBaseUrl + '/' + orderId + '/orderexport?lang=' + lang + '&export=' + doExport, null,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of all payments, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredPayments(filter:SearchContextVO):Observable<SearchResultVO<PaymentVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<PaymentVO>>(this._serviceBaseUrl + '/payments/search', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('CustomerOrderService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
