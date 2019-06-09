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
import { CountryVO, StateVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class LocationService {

  private _serviceBaseUrl = Config.API + 'service/location';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('LocationService constructed');
  }

  /**
   * Get list of all countries,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllCountries() {
    return this.http.get(this._serviceBaseUrl + '/country/all', Util.requestOptions())
      .map(res => <CountryVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get country,
   * @param id country id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCountryById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/country/' + id, Util.requestOptions())
      .map(res => <CountryVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create country.
   * @param country country
   * @returns {Observable<R>}
   */
  saveCountry(country:CountryVO) {

    let body = JSON.stringify(country);

    if (country.countryId > 0) {
      return this.http.post(this._serviceBaseUrl + '/country', body, Util.requestOptions())
        .map(res => <CountryVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/country', body, Util.requestOptions())
        .map(res => <CountryVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove country.
   * @param country country
   * @returns {Observable<R>}
   */
  removeCountry(country:CountryVO) {

    return this.http.delete(this._serviceBaseUrl + '/country/' + country.countryId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get list of all country states,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllStates(country:CountryVO) {
    return this.http.get(this._serviceBaseUrl + '/state/all/' + country.countryId, Util.requestOptions())
      .map(res => <StateVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get state,
   * @param id state id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getStateById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/state/' + id, Util.requestOptions())
      .map(res => <StateVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create state.
   * @param state state
   * @returns {Observable<R>}
   */
  saveState(state:StateVO) {
    let body = JSON.stringify(state);

    if (state.stateId > 0) {
      return this.http.post(this._serviceBaseUrl + '/state', body, Util.requestOptions())
        .map(res => <StateVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/state', body, Util.requestOptions())
        .map(res => <StateVO> this.json(res))
        .catch(this.handleError);
    }
  }

  /**
   * Remove state.
   * @param state state
   * @returns {Observable<R>}
   */
  removeState(state:StateVO) {

    return this.http.delete(this._serviceBaseUrl + '/state/' + state.stateId, Util.requestOptions())
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

    LogUtil.error('LocationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
