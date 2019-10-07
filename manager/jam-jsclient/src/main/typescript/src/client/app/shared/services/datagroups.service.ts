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
import { DataGroupVO, DataDescriptorVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class DataGroupsService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('DataGroupsService constructed');
  }

  /**
   * Get list of all groups,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllDataGroups() {
    return this.http.get(this._serviceBaseUrl + '/impex/datagroup/all', Util.requestOptions())
      .map(res => <DataGroupVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get group,
   * @param id group id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getDataGroupById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/impex/datagroup/' + id, Util.requestOptions())
      .map(res => <DataGroupVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create group.
   * @param group group
   * @returns {Observable<R>}
   */
  saveDataGroup(group:DataGroupVO) {

    let body = JSON.stringify(group);

    if (group.datagroupId > 0) {
      return this.http.post(this._serviceBaseUrl + '/impex/datagroup', body, Util.requestOptions())
        .map(res => <DataGroupVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/impex/datagroup', body, Util.requestOptions())
        .map(res => <DataGroupVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove group.
   * @param group group
   * @returns {Observable<R>}
   */
  removeDataGroup(group:DataGroupVO) {

    return this.http.delete(this._serviceBaseUrl + '/impex/datagroup/' + group.datagroupId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get list of all descriptors,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllDataDescriptors() {
    return this.http.get(this._serviceBaseUrl + '/impex/datadescriptor/all', Util.requestOptions())
      .map(res => <DataDescriptorVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get descriptor,
   * @param id descriptor id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getDataDescriptorById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/impex/datadescriptor/' + id, Util.requestOptions())
      .map(res => <DataDescriptorVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create descriptor.
   * @param descriptor descriptor
   * @returns {Observable<R>}
   */
  saveDataDescriptor(descriptor:DataDescriptorVO) {
    let body = JSON.stringify(descriptor);

    if (descriptor.datadescriptorId > 0) {
      return this.http.post(this._serviceBaseUrl + '/impex/datadescriptor', body, Util.requestOptions())
        .map(res => <DataDescriptorVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/impex/datadescriptor', body, Util.requestOptions())
        .map(res => <DataDescriptorVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Remove descriptor.
   * @param descriptor descriptor
   * @returns {Observable<R>}
   */
  removeDataDescriptor(descriptor:DataDescriptorVO) {

    return this.http.delete(this._serviceBaseUrl + '/impex/datadescriptor/' + descriptor.datadescriptorId, Util.requestOptions())
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

    LogUtil.error('DataGroupsService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
