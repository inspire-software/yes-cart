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
  EtypeVO, AttributeGroupVO, AttributeVO,
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
export class AttributeService {

  private _serviceBaseUrl = Config.API + 'service/attributes';  // URL to web api

  /**
   * Construct attribute service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('AttributeService constructed');
  }

  /**
   * Get list of all etypes,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllEtypes() {
    return this.http.get(this._serviceBaseUrl + '/etype/all', Util.requestOptions())
      .map(res => <EtypeVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all groups,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllGroups() {
    return this.http.get(this._serviceBaseUrl + '/group/all', Util.requestOptions())
      .map(res => <AttributeGroupVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all filtered attribute, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredAttributes(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/attribute/filtered', body,
              Util.requestOptions())
      .map(res => <SearchResultVO<AttributeVO>> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get list of all product types that use given attribute,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductTypesByAttributeCode(code:string) {
    return this.http.get(this._serviceBaseUrl + '/attribute/producttype/' + code, Util.requestOptions())
      .map(res => <Pair<number,string>[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get attribute,
   * @param id attribute id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAttributeById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/attribute/' + id, Util.requestOptions())
      .map(res => <AttributeVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create attribute.
   * @param attribute attribute
   * @returns {Observable<R>}
   */
  saveAttribute(attribute:AttributeVO) {
    let body = JSON.stringify(attribute);

    if (attribute.attributeId > 0) {
      return this.http.post(this._serviceBaseUrl + '/attribute', body, Util.requestOptions())
        .map(res => <AttributeVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/attribute', body, Util.requestOptions())
        .map(res => <AttributeVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove attribute.
   * @param attribute attribute
   * @returns {Observable<R>}
   */
  removeAttribute(attribute:AttributeVO) {

    return this.http.delete(this._serviceBaseUrl + '/attribute/' + attribute.attributeId, Util.requestOptions())
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

    LogUtil.error('AttributeService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
