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
import { Util } from './util';
import { LogUtil } from './../log/index';
import { AttrValueSystemVO, Pair, CacheInfoVO, ClusterNodeVO, JobStatusVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Observable }     from 'rxjs/Observable';
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
    LogUtil.debug('SystemService constructed');
  }


  /**
   * Get attributes for given system.
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
  saveSystemAttributes(attrs:Array<Pair<AttrValueSystemVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/preferences', body, options)
      .map(res => <AttrValueSystemVO[]> res.json())
      .catch(this.handleError);
  }



  /**
   * Get cache info.
   * @returns {Observable<R>}
   */
  getCacheInfo() {
    return this.http.get(this._serviceBaseUrl + '/cache')
      .map(res => <CacheInfoVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Evict all cache.
   * @returns {Observable<R>}
   */
  evictAllCache() {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/cache', options)
      .map(res => <CacheInfoVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Evict single cache.
   * @param name cache name
   * @returns {Observable<R>}
   */
  evictSingleCache(name:string) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/cache/' + name + '/', options)
      .map(res => <CacheInfoVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Save cache stats flag.
   * @param name cache name
   * @param stats flag whether to turn it on or off
   * @returns {Observable<R>}
   */
  saveCacheStatsFlag(name:string, stats:boolean) {

    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    let path = stats ? 'statson/' : 'statsoff/';

    return this.http.post(this._serviceBaseUrl + '/cache/' + path + name + '/', null, options)
      .map(res => <CacheInfoVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Get cluster info.
   * @returns {Observable<R>}
   */
  getClusterInfo() {
    return this.http.get(this._serviceBaseUrl + '/cluster')
      .map(res => <ClusterNodeVO[]> res.json())
      .catch(this.handleError);
  }



  /**
   * Get index job info.
   * @param token job token
   * @returns {Observable<R>}
   */
  getIndexJobStatus(token:string) {
    return this.http.get(this._serviceBaseUrl + '/index/status/' + token + '/')
      .map(res => <JobStatusVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Reindex all products.
   * @returns {Observable<R>}
   */
  reindexAllProducts() {

    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/index/all', null, options)
      .map(res => <JobStatusVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Reindex all products.
   * @param id shop id
   * @returns {Observable<R>}
   */
  reindexShopProducts(id:number) {

    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/index/shop/' + id, null, options)
      .map(res => <JobStatusVO> res.json())
      .catch(this.handleError);
  }



  /**
   * Reindex all products.
   * @param node node
   * @param typ query type
   * @param query query
   * @returns {Observable<R>}
   */
  runQuery(node:string, typ:string, query:string) {

    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    let path = (typ == 'HQL') ? '/hquery/' : ((typ == 'FT') ? '/ftquery/' : '/dbquery/');

    return this.http.post(this._serviceBaseUrl + path + node + '/', query, options)
      .map(res => <Array<Array<string>>> res.json())
      .catch(this.handleError);
  }


  private handleError (error:any) {

    LogUtil.error('SystemService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
