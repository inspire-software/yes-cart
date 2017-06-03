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
import { UserVO, ManagerVO, ManagerInfoVO, LicenseAgreementVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ManagementService {

  private _serviceBaseUrl = Config.API + 'service/management';  // URL to web api

  /**
   * Construct management service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('ManagementService constructed');
  }

  /**
   * Get current user info,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getMyself() {
    return this.http.get(this._serviceBaseUrl + '/myself')
      .map(res => {
        let manager = <ManagerVO> res.json();
        let name = this.getUserName(manager);
        let ui = this.getUserInterface(manager);
        let user:UserVO = { manager: manager, name: name, ui: ui };
        return user;
      })
      .catch(this.handleError);
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
  getUserInterface(manager:ManagerVO):string {
    let ui = 'ADMIN';
    manager.managerRoles.forEach(role => {
       if ('ROLE_SMCALLCENTER' == role.code) {
         ui = 'CC';
       } else if ('ROLE_SMADMIN' == role.code) {
         ui = 'SYSADMIN';
       }
    });
    return ui;
  }

  /**
   * Get current user info,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getMyUI() {
    return this.http.get(this._serviceBaseUrl + '/myui')
      .map(res => {
        let vals = <any> res.json();
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
        return vals;
      })
      .catch(this.handleError);
  }


  /**
   * Get current user info,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getMyAgreement() {
    return this.http.get(this._serviceBaseUrl + '/license')
      .map(res => <LicenseAgreementVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Get current user info,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  acceptMyAgreement() {

    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/license', null, options)
        .map(res => <LicenseAgreementVO> res.json())
        .catch(this.handleError);
  }

  private handleError (error:any) {

    LogUtil.error('ManagementService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
