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
import { CountryInfoVO, CountryVO, StateVO, SearchContextVO, SearchResultVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class LocationService {

  private _serviceBaseUrl = Config.API + 'service/locations';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('LocationService constructed');
  }

  /**
   * Get list of all countries,
   * @returns {Observable<T>}
   */
  getFilteredCountries(filter:SearchContextVO):Observable<SearchResultVO<CountryInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CountryInfoVO>>(this._serviceBaseUrl + '/countries/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get country,
   * @param id country id
   * @returns {Observable<T>}
   */
  getCountryById(id:number):Observable<CountryVO> {
    return this.http.get<CountryVO>(this._serviceBaseUrl + '/countries/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create country.
   * @param country country
   * @returns {Observable<T>}
   */
  saveCountry(country:CountryVO):Observable<CountryVO> {

    let body = JSON.stringify(country);

    if (country.countryId > 0) {
      return this.http.put<CountryVO>(this._serviceBaseUrl + '/countries', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<CountryVO>(this._serviceBaseUrl + '/countries', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove country.
   * @param country country
   * @returns {Observable<T>}
   */
  removeCountry(country:CountryVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/countries/' + country.countryId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get list of all country states,
   * @returns {Observable<T>}
   */
  getCountryStates(country:CountryVO):Observable<StateVO[]> {
    return this.http.get<StateVO[]>(this._serviceBaseUrl + '/countries/' + country.countryId + '/states', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all country states, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredStates(filter:SearchContextVO):Observable<SearchResultVO<StateVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<StateVO>>(this._serviceBaseUrl + '/states/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get state,
   * @param id state id
   * @returns {Observable<T>}
   */
  getStateById(id:number):Observable<StateVO> {
    return this.http.get<StateVO>(this._serviceBaseUrl + '/states/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create state.
   * @param state state
   * @returns {Observable<T>}
   */
  saveState(state:StateVO):Observable<StateVO> {
    let body = JSON.stringify(state);

    if (state.stateId > 0) {
      return this.http.put<StateVO>(this._serviceBaseUrl + '/states', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<StateVO>(this._serviceBaseUrl + '/states', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Remove state.
   * @param state state
   * @returns {Observable<T>}
   */
  removeState(state:StateVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/states/' + state.stateId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  private handleError (error:any) {

    LogUtil.error('LocationService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
