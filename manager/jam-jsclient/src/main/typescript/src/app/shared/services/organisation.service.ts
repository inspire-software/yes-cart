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
import { RoleVO, ManagerInfoVO, ManagerVO, SearchContextVO, SearchResultVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

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
  constructor (private http: HttpClient) {
    LogUtil.debug('OrganisationService constructed');
  }

  /**
   * Get list of all managers,
   * @returns {Observable<T>}
   */
  getFilteredManagers(filter:SearchContextVO):Observable<SearchResultVO<ManagerInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<ManagerInfoVO>>(this._serviceBaseUrl + '/managers/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get manager by email
   * @returns {Observable<T>}
   */
  getManagerById(id:number):Observable<ManagerVO> {
    return this.http.get<ManagerVO>(this._serviceBaseUrl + '/managers/' + id + '/', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create manager.
   * @param manager manager
   * @returns {Observable<T>}
   */
  saveManager(manager:ManagerVO):Observable<ManagerVO> {

    let body = JSON.stringify(manager);

    if (manager.managerId > 0) {
      return this.http.put<ManagerVO>(this._serviceBaseUrl + '/managers', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<ManagerVO>(this._serviceBaseUrl + '/managers', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Remove manager.
   * @param email manager email
   * @returns {Observable<T>}
   */
  removeManager(id:number):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/managers/' + id + '/', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }

  /**
   * Reset manager password.
   * @param email manager email
   * @returns {Observable<T>}
   */
  resetPassword(id:number):Observable<boolean> {

    return this.http.post(this._serviceBaseUrl + '/managers/' + id + '/reset', null, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }

  /**
   * Update manager on/off flag.
   * @param id manager id
   * @param state manager account state
   * @returns {Observable<T>}
   */
  updateDisabledFlag(id:number, state:boolean):Observable<boolean> {
    LogUtil.debug('ManagementService change manager state for ' + id + ' to ' + state ? 'online' : 'offline');

    let body = JSON.stringify({ disabled: state });

    return this.http.post(this._serviceBaseUrl +  '/managers/' + id + '/account', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }

  /**
   * Get list of all roles,
   * @returns {Observable<T>}
   */
  getAllRoles():Observable<RoleVO[]> {
    return this.http.get<RoleVO[]>(this._serviceBaseUrl + '/roles', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create role.
   * @param role role
   * @returns {Observable<T>}
   */
  saveRole(role:RoleVO):Observable<RoleVO> {

    let body = JSON.stringify(role);

    if (role.roleId > 0) {
      return this.http.put<RoleVO>(this._serviceBaseUrl + '/roles', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<RoleVO>(this._serviceBaseUrl + '/roles', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Remove role.
   * @param role role
   * @returns {Observable<T>}
   */
  removeRole(role:RoleVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/roles/' + role.code, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  private handleError (error:any) {

    LogUtil.error('OrganisationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
