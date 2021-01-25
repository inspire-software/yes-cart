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
import { UserVO, ManagerVO, ManagerInfoVO, LicenseAgreementVO, JWT, JWTAuth } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { UserEventBus } from './user-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { StorageUtil } from './../storage/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError, of } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ManagementService {

  private static JWT_KEY = 'CW000000';

  private _serviceBaseUrl = Config.API + 'service/management';  // URL to web api
  private _authBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct management service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('ManagementService constructed');
  }

  /**
   * Login endpoint
   * @param {string} user
   * @param {string} password
   * @param {string} organisation
   * @return {Observable<any | any>}
   */
  attemptResume():Observable<JWTAuth> {

    let fullJwtRaw:string = StorageUtil.readValue(ManagementService.JWT_KEY, null);
    if (fullJwtRaw) {
      let fullJwt:JWTAuth = JSON.parse(fullJwtRaw);
      if (fullJwt.status == 200 && fullJwt.decoded && fullJwt.decoded.exp * 1000 > new Date().getTime()) {

        // Current token is valid - try extend
        UserEventBus.getUserEventBus().emitJWT(fullJwt);
        return this.refreshToken();

      }
    }

    // Current token is invalid - trigger login screen
    UserEventBus.getUserEventBus().emitJWT({
      status: 401,
      message: 'MODAL_LOGIN_FAILED_AUTH',
      attemptResume: true
    });

    return throwError('MODAL_LOGIN_FAILED_AUTH');

  }

  /**
   * LogOff endpoint
   * @return {Observable<any | any>}
   */
  logoff():Observable<JWTAuth> {

    return this.http.delete(this._authBaseUrl + '/authenticate', { headers: Util.requestOptions() })
      .pipe(
        catchError(error => {

          LogUtil.debug('logoff JWT catch', error);

          UserEventBus.getUserEventBus().emitJWT(null);
          UserEventBus.getUserEventBus().emit(null);
          StorageUtil.removeValue(ManagementService.JWT_KEY);

          let message = Util.determineErrorMessage(error);

          return throwError(message.message || 'Server error');

        }),
        map((res:JWT) => {
          let jwt = res;

          let fullJwt:JWTAuth = {
            status: 200
          };

          LogUtil.debug('logoff JWT', jwt);

          UserEventBus.getUserEventBus().emitJWT(null);
          UserEventBus.getUserEventBus().emit(null);
          StorageUtil.removeValue(ManagementService.JWT_KEY);

          return fullJwt;
        })
      );
  }

  /**
   * Login endpoint
   * @param {string} user
   * @param {string} password
   * @param {string} organisation
   * @return {Observable<any | any>}
   */
  login(user:string, password:string, organisation:string = null):Observable<JWTAuth> {

    let body = {
      username: user,
      password: password,
      organisation: organisation
    };

    return this.http.post<JWT>(this._authBaseUrl + '/authenticate', body,
          { headers: Util.requestOptions({ includeAuth:false }) })
      .pipe(
        catchError(errorRsp => {

          LogUtil.debug('login JWT catch', errorRsp);
          let message = Util.determineErrorMessage(errorRsp);

          if (message.code == 401) {

            UserEventBus.getUserEventBus().emitJWT({
              status: 401,
              message: message.key ? message.key : 'AUTH_CREDENTAILS_INVALID'
            });

            return throwError('AUTH_CREDENTAILS_INVALID');
          }

          UserEventBus.getUserEventBus().emitJWT({
            status: message.code,
            message: message.message
          });

          return throwError('message.message' || 'Server error');

        }),
        map((res:JWT) => {
          let jwt = res;

          let decodedJWT = this.decodeJwt(jwt);

          let fullJwt:JWTAuth = {
            jwt: jwt.token,
            decoded: decodedJWT,
            status: 200
          };

          LogUtil.debug('login JWT', fullJwt);

          UserEventBus.getUserEventBus().emitJWT(fullJwt);
          StorageUtil.saveValue(ManagementService.JWT_KEY, JSON.stringify(fullJwt));

          return fullJwt;
        })
      );
  }

  /**
   * Login endpoint
   * @param {string} user
   * @param {string} password
   * @param {string} npassword
   * @param {string} cpassword
   * @param {string} organisation
   * @return {Observable<any | any>}
   */
  changePassword(user:string, password:string, npassword:string, cpassword:string, organisation:string = null):Observable<JWT> {

    let body = {
      username: user,
      password: password,
      npassword: npassword,
      cpassword: cpassword,
      organisation: organisation
    };

    return this.http.post<any>(this._authBaseUrl + '/changepwd', body,
      { headers: Util.requestOptions({ includeAuth:false }) })
      .pipe(
        catchError(this.handleError),
        map(res => {

          LogUtil.debug('changepwd', res);

          if (res && res.status == 200) {
            return { token: 'ok' };
          }

          return null;
        })
      );
  }

  /**
   * Login endpoint
   * @return {Observable<any | any>}
   */
  refreshToken():Observable<JWTAuth> {
    return this.http.post<JWT>(this._authBaseUrl + '/refreshtoken', null, { headers: Util.requestOptions() })
      .pipe(
        catchError(error => {

          LogUtil.debug('refreshToken JWT catch', error);
          let message = Util.determineErrorMessage(error);

          if (message.code == 401) {

            UserEventBus.getUserEventBus().emitJWT({
              status: 401,
              message: 'MODAL_LOGIN_FAILED_AUTH',
              attemptResume: true
            });

            return throwError('MODAL_LOGIN_FAILED_AUTH');
          }

          let fullJwt:JWTAuth = {
            status: message.code,
            message: message.message
          };

          UserEventBus.getUserEventBus().emitJWT(fullJwt);

          return throwError(message.message || 'Server error');

        }),
        map((res:JWT) => {
          let jwt = res;

          let decodedJWT = this.decodeJwt(jwt);

          let fullJwt:JWTAuth = {
            jwt: jwt.token,
            decoded: decodedJWT,
            status: 200
          };

          LogUtil.debug('refreshToken JWT', fullJwt);

          UserEventBus.getUserEventBus().emitJWT(fullJwt);
          StorageUtil.saveValue(ManagementService.JWT_KEY, JSON.stringify(fullJwt));

          return fullJwt;
        })
      );
  }

  /**
   * Get current user info,
   * @returns {Observable<T>}
   */
  getMyself():Observable<UserVO> {

    return this.http.get<ManagerVO>(this._serviceBaseUrl + '/myself', { headers: Util.requestOptions() })
      .pipe(
        catchError(this.handleError),
        map((res:ManagerVO) => {
          let manager = res;
          let name = this.getUserName(manager);
          let ui = this.getUserInterface(manager);
          let user:UserVO = { manager: manager, name: name, ui: ui };
          return user;
        })
      );
  }

  /**
   * Determine display user name for given manager.
   *
   * @param manager manager
   *
   * @return string
   */
  getUserName(manager:ManagerInfoVO):string {
    let currentUser:ManagerInfoVO = manager;
    if (manager != null) {
      if (currentUser.firstName != null && /.*\S+.*/.test(currentUser.firstName)) {
        return currentUser.firstName;
      } else if (currentUser.lastName != null && /.*\S+.*/.test(currentUser.lastName)) {
        return currentUser.lastName;
      } else {
        return currentUser.email;
      }
    }
    return 'anynomous';
  }

  /**
   * Determine best suited UI interface.
   *
   * @param manager manager
   *
   * @return ui name
   */
  getUserInterface(manager:ManagerVO):any {
    let ui = {
      'CCC': false,
      'PIM': false,
      'CMS': false,
      'MRK': false,
      'INV': false,
      'REP': false,
      'SHP': false,
      'SHO': false,
      'ORG': false,
      'OPS': false,
      'SYS': false
    };
    manager.managerRoles.forEach(role => {
      switch (role.code) {
        case 'ROLE_SMADMIN':
        case 'ROLE_SMSHOPADMIN':
          ui.CCC = true;
          ui.PIM = true;
          ui.CMS = true;
          ui.MRK = true;
          ui.INV = true;
          ui.REP = true;
          ui.SHP = true;
          ui.SHO = true;
          ui.ORG = true;
          ui.OPS = true;
          ui.SYS = true;
          break;
        case 'ROLE_SMSUBSHOPUSER':
          ui.CCC = true;
          ui.SHO = true;
          break;
        case 'ROLE_SMSHOPUSER':
          ui.SHO = true;
          break;
        case 'ROLE_SMWAREHOUSEADMIN':
        case 'ROLE_SMWAREHOUSEUSER':
          ui.INV = true;
          break;
        case 'ROLE_SMCALLCENTER':
        case 'ROLE_SMCALLCENTERCUSTOMER':
        case 'ROLE_SMCALLCENTERORDERAPPROVE':
        case 'ROLE_SMCALLCENTERORDERCONFIRM':
        case 'ROLE_SMCALLCENTERORDERPROCESS':
          ui.CCC = true;
          break;
        case 'ROLE_SMCONTENTADMIN':
        case 'ROLE_SMCONTENTUSER':
          ui.CMS = true;
          break;
        case 'ROLE_SMMARKETINGADMIN':
        case 'ROLE_SMMARKETINGUSER':
          ui.MRK = true;
          break;
        case 'ROLE_SMSHIPPINGADMIN':
        case 'ROLE_SMSHIPPINGUSER':
          ui.SHP = true;
          break;
        case 'ROLE_SMCATALOGADMIN':
        case 'ROLE_SMCATALOGUSER':
        case 'ROLE_SMPIMADMIN':
        case 'ROLE_SMPIMUSER':
          ui.PIM = true;
          break;

      }
    });
    return ui;
  }

  /**
   * Get current user info,
   * @returns {Observable<T>}
   */
  getMyUI():Observable<any> {
    return this.http.get(this._serviceBaseUrl + '/myui', { headers: Util.requestOptions() })
      .pipe(
        catchError(this.handleError),
        map((res:any) => {
          let vals = res;
          if (vals.hasOwnProperty('SYSTEM_PANEL_HELP_DOCS')) {
            let valDoc = vals['SYSTEM_PANEL_HELP_DOCS'];
            if (valDoc != null && valDoc != '') {
              Config.UI_DOC_LINK = valDoc;
            }
          }
          if (vals.hasOwnProperty('SYSTEM_PANEL_HELP_COPYRIGHT')) {
            let valCopy = vals['SYSTEM_PANEL_HELP_COPYRIGHT'];
            if (valCopy != null && valCopy != '') {
              Config.UI_COPY_NOTE = valCopy;
            }
          }
          if (vals.hasOwnProperty('SYSTEM_PANEL_LABEL')) {
            let valLabel = vals['SYSTEM_PANEL_LABEL'];
            if (valLabel != null && valLabel != '') {
              Config.UI_LABEL = valLabel;
            }
          }
          if (vals.hasOwnProperty('CONTEXT_PATH')) {
            let valCtx = vals['CONTEXT_PATH'];
            if (valCtx != null) {
              Config.CONTEXT_PATH = valCtx;
            }
          }
          return vals;
        })
      );
  }


  /**
   * Get current user info,
   * @returns {Observable<T>}
   */
  getMyAgreement():Observable<LicenseAgreementVO> {
    return this.http.get<LicenseAgreementVO>(this._serviceBaseUrl + '/license', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get current user info,
   * @returns {Observable<T>}
   */
  acceptMyAgreement():Observable<LicenseAgreementVO> {

    return this.http.post<LicenseAgreementVO>(this._serviceBaseUrl + '/license', null, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }

  private atobPolyfill(input:string):any {

    let chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
    let str = String(input).replace(/=+$/, '');
    if (str.length % 4 == 1) {
      return '';
    }
    let output = '';
    for (
      // initialize result and counters
      let bc = 0, bs = 0, buffer, idx = 0;
      // get next character
      buffer = str.charAt(idx++);
      // character found in table? initialize bit storage and add its ascii value;
      ~buffer && (bs = bc % 4 ? bs * 64 + buffer : buffer,
        // and if not first of each 4 characters,
        // convert the first 8 bits to one ascii character
      bc++ % 4) ? output += String.fromCharCode(255 & bs >> (-2 * bc & 6)) : 0
    ) {
      // try to find character in table (0-63, not found => -1)
      buffer = chars.indexOf(buffer);
    }
    return output;

  }

  private decodeJwt(token:JWT):any {

    if (token && token.token) {

      let body = token.token.split('.')[1];

      let atob = window && window.atob || this.atobPolyfill;

      var base64 = decodeURIComponent(atob(body).split('').map(function (c: string): string {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      return JSON.parse(base64);

    }
    return null;

  }


  private handleError (error:any) {

    LogUtil.error('ManagementService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
