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
import {Util} from './util';
import {AttrValueSystemVO, Pair} from '../model/index';
import {ErrorEventBus} from './error-event-bus.service';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class SystemService {

  private _serviceBaseUrl = Config.API + 'service/system';  // URL to web api

  /**
   * Construct system service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('SystemService constructed');
  }


  /**
   * Get attributes for given system.
   * @param id
   * @returns {Observable<R>}
   */
  getSystemPreferences() {
    return this.http.get(this._serviceBaseUrl + '/preferences')
      .map(res => <AttrValueSystemVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveShopAttributes(attrs:Array<Pair<AttrValueSystemVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/preferences', body, options)
      .map(res => <AttrValueSystemVO[]> res.json())
      .catch(this.handleError);
  }

  private handleError (error:any) {

    console.error('SystemService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
