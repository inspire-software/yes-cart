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
import { Util } from './util';
import { LogUtil } from './../log/index';
import {
  AttrValueSystemVO, Pair, CacheInfoVO, ClusterNodeVO, JobStatusVO,
  ModuleVO, ConfigurationVO
} from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

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
  constructor (private http: HttpClient) {
    LogUtil.debug('SystemService constructed');
  }


  /**
   * Get attributes for given system.
   * @param includeSecure
   * @returns {Observable<T>}
   */
  getSystemPreferences(includeSecure:boolean):Observable<AttrValueSystemVO[]> {
    return this.http.get<AttrValueSystemVO[]>(this._serviceBaseUrl + '/preferences?includeSecure=' + includeSecure,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Update supported attributes.
   * @param attrs
   * @param includeSecure
   * @returns {Observable<T>}
   */
  saveSystemAttributes(attrs:Array<Pair<AttrValueSystemVO, boolean>>, includeSecure:boolean):Observable<AttrValueSystemVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueSystemVO[]>(this._serviceBaseUrl + '/preferences?includeSecure=' + includeSecure, body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get cache info.
   * @returns {Observable<T>}
   */
  getCacheInfo():Observable<CacheInfoVO[]> {
    return this.http.get<CacheInfoVO[]>(this._serviceBaseUrl + '/cache', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Evict all cache.
   * @returns {Observable<T>}
   */
  evictAllCache():Observable<CacheInfoVO[]> {

    return this.http.delete<CacheInfoVO[]>(this._serviceBaseUrl + '/cache', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Evict single cache.
   * @param name cache name
   * @returns {Observable<T>}
   */
  evictSingleCache(name:string):Observable<CacheInfoVO[]> {

    return this.http.delete<CacheInfoVO[]>(this._serviceBaseUrl + '/cache/' + name + '/', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Save cache flag.
   * @param name cache name
   * @param enable whether to turn it on or off
   * @returns {Observable<T>}
   */
  saveCacheFlag(name:string, enable:boolean):Observable<CacheInfoVO[]> {

    let body = JSON.stringify({ statisticsDisabled: !enable });

    return this.http.put<CacheInfoVO[]>(this._serviceBaseUrl + '/cache/' + name + '/status', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get cluster info.
   * @returns {Observable<T>}
   */
  getClusterInfo():Observable<ClusterNodeVO[]> {
    return this.http.get<ClusterNodeVO[]>(this._serviceBaseUrl + '/cluster', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get modules info.
   * @returns {Observable<T>}
   */
  getModuleInfo(node:string):Observable<ModuleVO[]> {
    return this.http.get<ModuleVO[]>(this._serviceBaseUrl + '/cluster/' + node + '/modules', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get configuration info.
   * @returns {Observable<T>}
   */
  getConfigurationInfo():Observable<ConfigurationVO[]> {
    return this.http.get<ConfigurationVO[]>(this._serviceBaseUrl + '/cluster/configurations', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Reload system configurations and evict all cache.
   * @returns {Observable<T>}
   */
  reloadConfigurations():Observable<ClusterNodeVO[]> {

    return this.http.put<ClusterNodeVO[]>(this._serviceBaseUrl + '/cluster/configurations', null, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get index job info.
   * @param token job token
   * @returns {Observable<T>}
   */
  getIndexJobStatus(token:string):Observable<JobStatusVO> {
    return this.http.get<JobStatusVO>(this._serviceBaseUrl + '/index/' + token + '/status', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Reindex all products.
   * @returns {Observable<T>}
   */
  reindexAllProducts():Observable<JobStatusVO> {

    return this.http.post<JobStatusVO>(this._serviceBaseUrl + '/index', null, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Reindex all products.
   * @param id shop id
   * @returns {Observable<T>}
   */
  reindexShopProducts(id:number):Observable<JobStatusVO> {

    return this.http.post<JobStatusVO>(this._serviceBaseUrl + '/index/shops/' + id, null, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Retrieve supported queries per node.
   * @returns {Observable<T>}
   */
  supportedQueries():Observable<Pair<string, Array<string>>[]> {

    return this.http.get<Pair<string, Array<string>>[]>(this._serviceBaseUrl + '/query', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Run query of specified type against node.
   * @param node node
   * @param typ query type
   * @param query query
   * @returns {Observable<T>}
   */
  runQuery(node:string, typ:string, query:string):Observable<Array<Array<string>>> {

    let body = JSON.stringify({ type: typ, query: query });

    return this.http.post<Array<Array<string>>>(this._serviceBaseUrl + '/query/' + node + '/', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('SystemService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
