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
  CustomerVO, CustomerInfoVO, AttrValueCustomerVO,
  AddressBookVO, AddressVO,
  Pair, SearchContextVO, SearchResultVO
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
export class CustomerService {

  private _serviceBaseUrl = Config.API + 'service/customers';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('CustomerService constructed');
  }

  /**
   * Get list of all customers, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredCustomer(filter:SearchContextVO):Observable<SearchResultVO<CustomerInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CustomerInfoVO>>(this._serviceBaseUrl + '/search', body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all customers types, which are accessible to manage or view,
   * @param customerId pk
   * @param lang language
   * @returns {Observable<T>}
   */
  getCustomerTypes(customerId:number, lang:string):Observable<Pair<string, string>[]> {

    return this.http.get<Pair<string, string>[]>(this._serviceBaseUrl + '/' + customerId + '/types?lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get customer, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getCustomerById(customerId:number):Observable<CustomerVO> {
    return this.http.get<CustomerVO>(this._serviceBaseUrl + '/' + customerId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create/update customer.
   * @param customer customer
   * @returns {Observable<T>}
   */
  saveCustomer(customer:CustomerVO):Observable<CustomerVO> {

    let body = JSON.stringify(customer);

    if (customer.customerId > 0) {
      return this.http.put<CustomerVO>(this._serviceBaseUrl + '/', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<CustomerVO>(this._serviceBaseUrl + '/', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove customer.
   * @param customer customer
   * @returns {Observable<T>}
   */
  removeCustomer(customer:CustomerInfoVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/' + customer.customerId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given customer id.
   * @param id
   * @returns {Observable<T>}
   */
  getCustomerAttributes(id:number):Observable<AttrValueCustomerVO[]> {
    return this.http.get<AttrValueCustomerVO[]>(this._serviceBaseUrl + '/' + id + '/attributes', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveCustomerAttributes(attrs:Array<Pair<AttrValueCustomerVO, boolean>>):Observable<AttrValueCustomerVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueCustomerVO[]>(this._serviceBaseUrl + '/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Reset password in a shop delivery to next state.
   * @returns {Observable<T>}
   */
  resetPassword(customer:CustomerInfoVO, shopId:number) {

    return this.http.post(this._serviceBaseUrl + '/' + customer.customerId + '/reset/' + shopId, null, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Get laddress book of a customer, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getAddressBook(customer:CustomerInfoVO, formattingShopId:number, lang:string):Observable<AddressBookVO> {

    return this.http.get<AddressBookVO>(this._serviceBaseUrl + '/' + customer.customerId + '/addressbook?shopId=' + formattingShopId + '&lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Create/update address.
   * @param address address
   * @returns {Observable<T>}
   */
  saveAddress(address:AddressVO):Observable<AddressVO> {

    let body = JSON.stringify(address);

    if (address.addressId > 0) {
      return this.http.put<AddressVO>(this._serviceBaseUrl + '/addressbook', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<AddressVO>(this._serviceBaseUrl + '/addressbook', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove address.
   * @param address address
   * @returns {Observable<T>}
   */
  removeAddress(address:AddressVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/addressbook/' + address.addressId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  private handleError (error:any) {

    LogUtil.error('CustomerService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
