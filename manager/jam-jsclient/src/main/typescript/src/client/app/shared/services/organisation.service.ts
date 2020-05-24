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
import { Http, Response } from '@angular/http';
import { Config } from '../config/env.config';
import { RoleVO, ManagerInfoVO, ManagerVO, SearchContextVO, SearchResultVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Organisation service has all methods to work with users and roles.
 */
@Injectable()
export class OrganisationService {

  private _serviceBaseUrl = Config.API + 'service/organisations';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to role(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('OrganisationService constructed');
  }

  /**
   * Get list of all managers,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredManagers(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/managers/search', body,
      Util.requestOptions())
      .map(res => <SearchResultVO<ManagerInfoVO>> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get manager by email
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getManagerById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/managers/' + id + '/', Util.requestOptions())
      .map(res => <ManagerVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create manager.
   * @param manager manager
   * @returns {Observable<R>}
   */
  saveManager(manager:ManagerVO) {

    let body = JSON.stringify(manager);

    if (manager.managerId > 0) {
      return this.http.put(this._serviceBaseUrl + '/managers', body, Util.requestOptions())
        .map(res => <ManagerVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl + '/managers', body, Util.requestOptions())
        .map(res => <ManagerVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Remove manager.
   * @param email manager email
   * @returns {Observable<R>}
   */
  removeManager(id:number) {

    return this.http.delete(this._serviceBaseUrl + '/managers/' + id + '/', Util.requestOptions())
      .catch(this.handleError);
  }

  /**
   * Reset manager password.
   * @param email manager email
   * @returns {Observable<R>}
   */
  resetPassword(id:number) {

    return this.http.post(this._serviceBaseUrl + '/managers/' + id + '/reset', null, Util.requestOptions())
      .catch(this.handleError);
  }

  /**
   * Update manager on/off flag.
   * @param id manager id
   * @param state manager account state
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDisabledFlag(id:number, state:boolean) {
    LogUtil.debug('ManagementService change manager state for ' + id + ' to ' + state ? 'online' : 'offline');

    let body = JSON.stringify({ disabled: state });

    return this.http.post(this._serviceBaseUrl +  '/managers/' + id + '/account', body, Util.requestOptions())
      .catch(this.handleError);
  }

  /**
   * Get list of all roles,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllRoles() {
    return this.http.get(this._serviceBaseUrl + '/roles', Util.requestOptions())
      .map(res => <RoleVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create role.
   * @param role role
   * @returns {Observable<R>}
   */
  saveRole(role:RoleVO) {

    let body = JSON.stringify(role);

    if (role.roleId > 0) {
      return this.http.put(this._serviceBaseUrl + '/roles', body, Util.requestOptions())
        .map(res => <RoleVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl + '/roles', body, Util.requestOptions())
        .map(res => <RoleVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Remove role.
   * @param role role
   * @returns {Observable<R>}
   */
  removeRole(role:RoleVO) {

    return this.http.delete(this._serviceBaseUrl + '/roles/' + role.code, Util.requestOptions())
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

    LogUtil.error('OrganisationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
