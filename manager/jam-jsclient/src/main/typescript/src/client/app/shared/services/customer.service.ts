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
  CustomerVO, CustomerInfoVO, AttrValueCustomerVO,
  AddressBookVO, AddressVO,
  Pair, SearchContextVO, SearchResultVO
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
export class CustomerService {

  private _serviceBaseUrl = Config.API + 'service/customer';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('CustomerService constructed');
  }

  /**
   * Get list of all customers, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredCustomer(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/filtered', body,
          Util.requestOptions())
        .map(res => <SearchResultVO<CustomerInfoVO>> this.json(res))
        .catch(this.handleError);
  }


  /**
   * Get list of all customers types, which are accessible to manage or view,
   * @param customerId pk
   * @param lang language
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCustomerTypes(customerId:number, lang:string) {

    return this.http.get(this._serviceBaseUrl + '/types/' + lang + '/' + customerId + '/', Util.requestOptions())
      .map(res => <Pair<string, string>[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get customer, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCustomerById(customerId:number) {
    return this.http.get(this._serviceBaseUrl + '/' + customerId, Util.requestOptions())
      .map(res => <CustomerVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create/update customer.
   * @param customer customer
   * @returns {Observable<R>}
   */
  saveCustomer(customer:CustomerVO) {

    let body = JSON.stringify(customer);

    if (customer.customerId > 0) {
      return this.http.post(this._serviceBaseUrl + '/', body, Util.requestOptions())
        .map(res => <CustomerVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/', body, Util.requestOptions())
        .map(res => <CustomerVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove customer.
   * @param customer customer
   * @returns {Observable<R>}
   */
  removeCustomer(customer:CustomerInfoVO) {

    return this.http.delete(this._serviceBaseUrl + '/' + customer.customerId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get attributes for given customer id.
   * @param id
   * @returns {Observable<R>}
   */
  getCustomerAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/attributes/' + id, Util.requestOptions())
      .map(res => <AttrValueCustomerVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveCustomerAttributes(attrs:Array<Pair<AttrValueCustomerVO, boolean>>) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + '/attributes', body, Util.requestOptions())
      .map(res => <AttrValueCustomerVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Reset password in a shop delivery to next state.
   * @returns {Observable<R>}
   */
  resetPassword(customer:CustomerInfoVO, shopId:number) {

    return this.http.post(this._serviceBaseUrl + '/reset/' + customer.customerId + '/' + shopId, null, Util.requestOptions())
        .catch(this.handleError);
  }


  /**
   * Get laddress book of a customer, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAddressBook(customer:CustomerInfoVO, formattingShopId:number, lang:string) {

    return this.http.get(this._serviceBaseUrl + '/addressbook/' + customer.customerId + '/' + formattingShopId + '/' + lang, Util.requestOptions())
      .map(res => <AddressBookVO> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Create/update address.
   * @param address address
   * @returns {Observable<R>}
   */
  saveAddress(address:AddressVO) {

    let body = JSON.stringify(address);

    if (address.addressId > 0) {
      return this.http.post(this._serviceBaseUrl + '/addressbook', body, Util.requestOptions())
        .map(res => <AddressVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/addressbook', body, Util.requestOptions())
        .map(res => <AddressVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove address.
   * @param address address
   * @returns {Observable<R>}
   */
  removeAddress(address:AddressVO) {

    return this.http.delete(this._serviceBaseUrl + '/addressbook/' + address.addressId, Util.requestOptions())
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

    LogUtil.error('CustomerService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
