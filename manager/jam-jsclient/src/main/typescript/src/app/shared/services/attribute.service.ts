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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Config } from '../../../environments/environment';
import {
  EtypeVO, AttributeGroupVO, AttributeVO,
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
export class AttributeService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct attribute service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('AttributeService constructed');
  }

  /**
   * Get list of all etypes,
   * @returns {Observable<T>}
   */
  getAllEtypes():Observable<EtypeVO[]> {
    return this.http.get<EtypeVO[]>(this._serviceBaseUrl + '/attributes/etypes', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all groups,
   * @returns {Observable<T>}
   */
  getAllGroups():Observable<AttributeGroupVO[]> {
    return this.http.get<AttributeGroupVO[]>(this._serviceBaseUrl + '/attributes/groups', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all filtered attribute, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredAttributes(filter:SearchContextVO):Observable<SearchResultVO<AttributeVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<AttributeVO>>(this._serviceBaseUrl + '/attributes/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of all product types that use given attribute,
   * @returns {Observable<T>}
   */
  getProductTypesByAttributeCode(code:string):Observable<Pair<number,string>[]> {
    return this.http.get<Pair<number,string>[]>(this._serviceBaseUrl + '/attributes/' + code + '/producttypes',
      { headers: Util.requestOptions() })
    .pipe(catchError(this.handleError));
  }



  /**
   * Get attribute,
   * @param id attribute id
   * @returns {Observable<T>}
   */
  getAttributeById(id:number):Observable<AttributeVO> {
    return this.http.get<AttributeVO>(this._serviceBaseUrl + '/attributes/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create attribute.
   * @param attribute attribute
   * @returns {Observable<T>}
   */
  saveAttribute(attribute:AttributeVO):Observable<AttributeVO> {
    let body = JSON.stringify(attribute);

    if (attribute.attributeId > 0) {
      return this.http.put<AttributeVO>(this._serviceBaseUrl + '/attributes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<AttributeVO>(this._serviceBaseUrl + '/attributes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove attribute.
   * @param attribute attribute
   * @returns {Observable<T>}
   */
  removeAttribute(attribute:AttributeVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/attributes/' + attribute.attributeId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }

  private handleError (error:any) {

    LogUtil.error('AttributeService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
