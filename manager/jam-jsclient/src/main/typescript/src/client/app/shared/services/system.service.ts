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
import { Util } from './util';
import { LogUtil } from './../log/index';
import { AttrValueSystemVO, Pair, CacheInfoVO, ClusterNodeVO, JobStatusVO, ModuleVO, ConfigurationVO } from '../model/index';
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
   * @param includeSecure
   * @returns {Observable<R>}
   */
  getSystemPreferences(includeSecure:boolean) {
    return this.http.get(this._serviceBaseUrl + (includeSecure ? '/preferences/secure' : '/preferences'), Util.requestOptions())
      .map(res => <AttrValueSystemVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Update supported attributes.
   * @param attrs
   * @param includeSecure
   * @returns {Observable<R>}
   */
  saveSystemAttributes(attrs:Array<Pair<AttrValueSystemVO, boolean>>, includeSecure:boolean) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + (includeSecure ? '/preferences/secure' : '/preferences'), body, Util.requestOptions())
      .map(res => <AttrValueSystemVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get cache info.
   * @returns {Observable<R>}
   */
  getCacheInfo() {
    return this.http.get(this._serviceBaseUrl + '/cache', Util.requestOptions())
      .map(res => <CacheInfoVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Evict all cache.
   * @returns {Observable<R>}
   */
  evictAllCache() {

    return this.http.delete(this._serviceBaseUrl + '/cache', Util.requestOptions())
      .map(res => <CacheInfoVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Evict single cache.
   * @param name cache name
   * @returns {Observable<R>}
   */
  evictSingleCache(name:string) {

    return this.http.delete(this._serviceBaseUrl + '/cache/' + name + '/', Util.requestOptions())
      .map(res => <CacheInfoVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Save cache flag.
   * @param name cache name
   * @param enable whether to turn it on or off
   * @returns {Observable<R>}
   */
  saveCacheFlag(name:string, enable:boolean) {

    let path = enable ? 'on/' : 'off/';

    return this.http.post(this._serviceBaseUrl + '/cache/' + path + name + '/', null, Util.requestOptions())
      .map(res => <CacheInfoVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get cluster info.
   * @returns {Observable<R>}
   */
  getClusterInfo() {
    return this.http.get(this._serviceBaseUrl + '/cluster', Util.requestOptions())
      .map(res => <ClusterNodeVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get modules info.
   * @returns {Observable<R>}
   */
  getModuleInfo(node:string) {
    return this.http.get(this._serviceBaseUrl + '/cluster/' + node + '/modules', Util.requestOptions())
      .map(res => <ModuleVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get configuration info.
   * @returns {Observable<R>}
   */
  getConfigurationInfo() {
    return this.http.get(this._serviceBaseUrl + '/cluster/configurations', Util.requestOptions())
      .map(res => <ConfigurationVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Reload system configurations and evict all cache.
   * @returns {Observable<R>}
   */
  reloadConfigurations() {

    return this.http.post(this._serviceBaseUrl + '/reloadconfigurations', null, Util.requestOptions())
      .map(res => <ClusterNodeVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get index job info.
   * @param token job token
   * @returns {Observable<R>}
   */
  getIndexJobStatus(token:string) {
    return this.http.get(this._serviceBaseUrl + '/index/status/' + token + '/', Util.requestOptions())
      .map(res => <JobStatusVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Reindex all products.
   * @returns {Observable<R>}
   */
  reindexAllProducts() {

    return this.http.post(this._serviceBaseUrl + '/index/all', null, Util.requestOptions())
      .map(res => <JobStatusVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Reindex all products.
   * @param id shop id
   * @returns {Observable<R>}
   */
  reindexShopProducts(id:number) {

    return this.http.post(this._serviceBaseUrl + '/index/shop/' + id, null, Util.requestOptions())
      .map(res => <JobStatusVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Retrieve supported queries per node.
   * @returns {Observable<R>}
   */
  supportedQueries() {

    return this.http.get(this._serviceBaseUrl + '/query/supported/', Util.requestOptions())
      .map(res => <Pair<string, Array<string>>[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Run query of specified type against node.
   * @param node node
   * @param typ query type
   * @param query query
   * @returns {Observable<R>}
   */
  runQuery(node:string, typ:string, query:string) {

    let body = JSON.stringify({ type: typ, query: query });

    console.log('hello', body);

    return this.http.post(this._serviceBaseUrl + '/query/' + node + '/', body,
        Util.requestOptions())
      .map(res => <Array<Array<string>>> this.json(res))
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

    LogUtil.error('SystemService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
