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
    return this.http.get(this._serviceBaseUrl + '/country/all')
      .map(res => <CountryVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get country,
   * @param id country id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCountryById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/country/' + id)
      .map(res => <CountryVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create country.
   * @param country country
   * @returns {Observable<R>}
   */
  saveCountry(country:CountryVO) {

    let body = JSON.stringify(country);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (country.countryId > 0) {
      return this.http.post(this._serviceBaseUrl + '/country', body, options)
        .map(res => <CountryVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/country', body, options)
        .map(res => <CountryVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Remove country.
   * @param country country
   * @returns {Observable<R>}
   */
  removeCountry(country:CountryVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/country/' + country.countryId, options)
      .catch(this.handleError);
  }


  /**
   * Get list of all country states,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllStates(country:CountryVO) {
    return this.http.get(this._serviceBaseUrl + '/state/all/' + country.countryId)
      .map(res => <StateVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Get state,
   * @param id state id
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getStateById(id:number) {
    return this.http.get(this._serviceBaseUrl + '/state/' + id)
      .map(res => <StateVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create state.
   * @param state state
   * @returns {Observable<R>}
   */
  saveState(state:StateVO) {
    let body = JSON.stringify(state);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (state.stateId > 0) {
      return this.http.post(this._serviceBaseUrl + '/state', body, options)
        .map(res => <StateVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/state', body, options)
        .map(res => <StateVO> res.json())
        .catch(this.handleError);
    }
  }

  /**
   * Remove state.
   * @param state state
   * @returns {Observable<R>}
   */
  removeState(state:StateVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/state/' + state.stateId, options)
      .catch(this.handleError);
  }

  private handleError (error:any) {

    LogUtil.error('LocationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
