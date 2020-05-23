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
import {
  ContentWithBodyVO, ContentVO, AttrValueContentVO, ContentBodyVO,
  Pair, SearchContextVO, SearchResultVO
} from '../model/index';
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

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

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
    return this.http.get(this._serviceBaseUrl + '/shops/' + shopId + '/content', Util.requestOptions())
      .map(res => <ContentVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopBranchContent(shopId:number, root:number, expand:string[]) {
    if (expand != null && expand.length > 0) {
      let param = '';
      expand.forEach(node => {
        param += node + '|';
      });
      return this.http.get(this._serviceBaseUrl +  '/shops/' + shopId + '/content/' + root + '/branches?expand=' + encodeURIComponent(param), Util.requestOptions())
        .map(res => <ContentVO[]> this.json(res))
        .catch(this.handleError);
    }
    return this.http.get(this._serviceBaseUrl +  '/shops/' + shopId + '/content/' + root + '/branches', Util.requestOptions())
      .map(res => <ContentVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all category ids leading to given category, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getShopBranchesContentPaths(shopId:number, expand:number[]) {
    let param = '';
    expand.forEach(node => {
      param += node + '|';
    });
    return this.http.get(this._serviceBaseUrl +  '/shops/' + shopId + '/content/branchpaths/?expand=' + encodeURIComponent(param), Util.requestOptions())
      .map(res => <number[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all filtered content, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredContent(shopId:number, filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/shops/' + shopId + '/content/search', body,
          Util.requestOptions())
      .map(res => <SearchResultVO<ContentVO>> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get content, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getContentById(contentId:number) {
    return this.http.get(this._serviceBaseUrl + '/content/' + contentId, Util.requestOptions())
      .map(res => <ContentWithBodyVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create/update content.
   * @param content content
   * @returns {Observable<R>}
   */
  saveContent(content:ContentVO) {

    let body = JSON.stringify(content);

    if (content.contentId > 0) {
      return this.http.put(this._serviceBaseUrl + '/content/', body, Util.requestOptions())
        .map(res => <ContentWithBodyVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.post(this._serviceBaseUrl + '/content/', body, Util.requestOptions())
        .map(res => <ContentWithBodyVO> this.json(res))
        .catch(this.handleError);
    }
  }



  /**
   * Remove content.
   * @param content content
   * @returns {Observable<R>}
   */
  removeContent(content:ContentVO) {

    return this.http.delete(this._serviceBaseUrl + '/content/' + content.contentId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<R>}
   */
  getContentAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/content/' + id + '/attributes', Util.requestOptions())
      .map(res => <AttrValueContentVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveContentAttributes(attrs:Array<Pair<AttrValueContentVO, boolean>>) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + '/content/attributes', body, Util.requestOptions())
      .map(res => <AttrValueContentVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<R>}
   */
  getContentBody(id:number) {
    return this.http.get(this._serviceBaseUrl + '/content/' + id + '/body')
      .map(res => <ContentBodyVO[]> this.json(res))
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

    LogUtil.error('ContentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
