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
import { CustomerOrderVO, CustomerOrderInfoVO, CustomerOrderDeliveryInfoVO, CustomerOrderTransitionResultVO, PaymentVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class CustomerOrderService {

  private _serviceBaseUrl = Config.API + 'service/customerorder';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('CustomerOrderService constructed');
  }

  /**
   * Get list of all orders, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredOrders(lang:string, filter:string, statuses:string[], max:number) {

    let body = JSON.stringify({ filter: filter, statuses: statuses });

    return this.http.post(this._serviceBaseUrl + '/filtered/' + max + '/' + lang, body, Util.requestOptions())
        .map(res => <CustomerOrderInfoVO[]> this.json(res))
        .catch(this.handleError);
  }


  /**
   * Get order, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getOrderById(lang:string, orderId:number) {
    return this.http.get(this._serviceBaseUrl + '/order/' + orderId + '/' + lang, Util.requestOptions())
      .map(res => <CustomerOrderVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Transition order to next state.
   * @param order order
   * @param action action key from order next transitions
   * @param manualMsg message provided by manual PG actions
   * @returns {Observable<R>}
   */
  transitionOrder(order:CustomerOrderInfoVO, action:string, manualMsg:string) {

    let body = manualMsg;

    return this.http.post(this._serviceBaseUrl + '/transition/' + action + '/' + order.ordernum + '/', body,
          Util.requestOptions({ type:'text/plain; charset=utf-8' }))
        .map(res => <CustomerOrderTransitionResultVO> this.json(res))
        .catch(this.handleError);
  }


  /**
   * Transition order delivery to next state.
   * @param order order
   * @param delivery delivery
   * @param action action key from order next transitions
   * @param manualMsg message provided by manual PG actions
   * @returns {Observable<R>}
   */
  transitionDelivery(order:CustomerOrderInfoVO, delivery:CustomerOrderDeliveryInfoVO, action:string, manualMsg:string) {

    let body = manualMsg;

    return this.http.post(this._serviceBaseUrl + '/transition/' + action + '/' + order.ordernum + '/' + delivery.deliveryNum + '/', body,
          Util.requestOptions({ type:'text/plain; charset=utf-8' }))
        .map(res => <CustomerOrderTransitionResultVO> this.json(res))
        .catch(this.handleError);
  }


  /**
   * Export order
   * @returns {Observable<R>}
   */
  exportOrder(lang:string, orderId:number, doExport:boolean) {

    return this.http.post(this._serviceBaseUrl + '/orderexport/' + orderId + '/' + lang + '/' + doExport + '/', null, Util.requestOptions())
      .map(res => <CustomerOrderVO> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get list of all payments, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredPayments(filter:string, operations:string[], statuses:string[], max:number) {

    let body = JSON.stringify({ filter: filter, operations: operations, statuses: statuses });

    return this.http.post(this._serviceBaseUrl + '/payments/filtered/' + max + '/', body, Util.requestOptions())
      .map(res => <PaymentVO[]> this.json(res))
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

    LogUtil.error('CustomerOrderService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
