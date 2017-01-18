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
import { ContentWithBodyVO, ContentVO, AttrValueContentVO, ContentBodyVO, Pair } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ContentService {

  private _serviceBaseUrl = Config.API + 'service/content';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('ContentService constructed');
  }

  /**
   * Get list of all content, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllShopContent(shopId:number) {
    return this.http.get(this._serviceBaseUrl + '/shop/' + shopId)
      .map(res => <ContentVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get list of all filtered content, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredContent(shopId:number, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/shop/' + shopId + '/filtered/' + max, body, options)
      .map(res => <ContentVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get content, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getContentById(contentId:number) {
    return this.http.get(this._serviceBaseUrl + '/' + contentId)
      .map(res => <ContentWithBodyVO> res.json())
      .catch(this.handleError);
  }


  /**
   * Create/update content.
   * @param content content
   * @returns {Observable<R>}
   */
  saveContent(content:ContentVO) {

    let body = JSON.stringify(content);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (content.contentId > 0) {
      return this.http.post(this._serviceBaseUrl + '/', body, options)
        .map(res => <ContentWithBodyVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/', body, options)
        .map(res => <ContentWithBodyVO> res.json())
        .catch(this.handleError);
    }
  }



  /**
   * Remove content.
   * @param content content
   * @returns {Observable<R>}
   */
  removeContent(content:ContentVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/' + content.contentId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<R>}
   */
  getContentAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/attributes/' + id)
      .map(res => <AttrValueContentVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveContentAttributes(attrs:Array<Pair<AttrValueContentVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/attributes', body, options)
      .map(res => <AttrValueContentVO[]> res.json())
      .catch(this.handleError);
  }



  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<R>}
   */
  getContentBody(id:number) {
    return this.http.get(this._serviceBaseUrl + '/body/' + id)
      .map(res => <ContentBodyVO[]> res.json())
      .catch(this.handleError);
  }



  private handleError (error:any) {

    LogUtil.error('ContentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
