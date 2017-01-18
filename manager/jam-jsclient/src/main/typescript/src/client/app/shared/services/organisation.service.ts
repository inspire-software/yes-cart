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
import { Http, Headers, RequestOptions } from '@angular/http';
import { Config } from '../config/env.config';
import { RoleVO, ManagerInfoVO, ManagerVO } from '../model/index';
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

  private _serviceBaseUrl = Config.API + 'service/organisation';  // URL to web api

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
  getAllManagers() {
    return this.http.get(this._serviceBaseUrl + '/managers/all')
      .map(res => <ManagerInfoVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get manager by email
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getManagerByEmail(email:string) {
    return this.http.get(this._serviceBaseUrl + '/manager/' + email + '/')
      .map(res => <ManagerVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create manager.
   * @param manager manager
   * @returns {Observable<R>}
   */
  saveManager(manager:ManagerVO) {

    let body = JSON.stringify(manager);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (manager.managerId > 0) {
      return this.http.post(this._serviceBaseUrl + '/manager', body, options)
        .map(res => <ManagerVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/manager', body, options)
        .map(res => <ManagerVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Remove manager.
   * @param manager manager
   * @returns {Observable<R>}
   */
  removeManager(email:string) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/manager/' + email + '/', options)
      .catch(this.handleError);
  }

  /**
   * Reset manager password.
   * @param email manager email
   * @returns {Observable<R>}
   */
  resetPassword(email:string) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/manager/reset/' + email + '/', options)
      .catch(this.handleError);
  }

  /**
   * Update manager on/off flag.
   * @param manager manager email
   * @param state manager state
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDisabledFlag(manager:string, state:boolean) {
    LogUtil.debug('ManagementService change manager state for ' + manager + ' to ' + state ? 'online' : 'offline');

    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl +  '/manager/offline/' + manager + '/' + state, null, options)
      .catch(this.handleError);
  }

  /**
   * Get list of all roles,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllRoles() {
    return this.http.get(this._serviceBaseUrl + '/role/all')
      .map(res => <RoleVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Create role.
   * @param role role
   * @returns {Observable<R>}
   */
  saveRole(role:RoleVO) {

    let body = JSON.stringify(role);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (role.roleId > 0) {
      return this.http.post(this._serviceBaseUrl + '/role', body, options)
        .map(res => <RoleVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/role', body, options)
        .map(res => <RoleVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Remove role.
   * @param role role
   * @returns {Observable<R>}
   */
  removeRole(role:RoleVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/role/' + role.code, options)
      .catch(this.handleError);
  }


  private handleError (error:any) {

    LogUtil.error('OrganisationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
