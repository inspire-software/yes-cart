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


import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Config} from '../config/env.config';
import {ManagerVO} from '../model/index';
import {Observable}     from 'rxjs/Observable';
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
    console.debug('ManagementService constructed');
  }

  /**
   * Get current user info,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getMyself() {
    return this.http.get(this._serviceBaseUrl + '/myself')
      .map(res => <ManagerVO> res.json())
      .catch(this.handleError);
  }


  private handleError (error:any) {
    // in a real world app, we may send the error to some remote logging infrastructure
    // instead of just logging it to the console
    console.error('ManagementService Server error: ' + error['message'], error);
    if (error['message'].indexOf('JSON Parse error')) {
      return Observable.throw(error['message'] || 'Server error');
    }
    return Observable.throw(error.json().error || 'Server error');
  }

}
