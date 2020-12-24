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
import {
  ContentWithBodyVO, ContentVO, AttrValueContentVO, ContentBodyVO,
  Pair, SearchContextVO, SearchResultVO
} from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

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
  constructor (private http: HttpClient) {
    LogUtil.debug('ContentService constructed');
  }

  /**
   * Get list of all content, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getAllShopContent(shopId:number):Observable<ContentVO[]> {
    return this.http.get<ContentVO[]>(this._serviceBaseUrl + '/shops/' + shopId + '/content', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getShopBranchContent(shopId:number, root:number, expand:string[] | number[]):Observable<ContentVO[]> {
    if (expand != null && expand.length > 0) {
      let param = '';
      expand.forEach(node => {
        param += node + '|';
      });
      return this.http.get<ContentVO[]>(this._serviceBaseUrl +  '/shops/' + shopId + '/content/' + root + '/branches?expand=' + encodeURIComponent(param),
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
    return this.http.get<ContentVO[]>(this._serviceBaseUrl +  '/shops/' + shopId + '/content/' + root + '/branches',
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all category ids leading to given category, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getShopBranchesContentPaths(shopId:number, expand:string[] | number[]):Observable<number[]> {
    let param = '';
    expand.forEach(node => {
      param += node + '|';
    });
    return this.http.get<number[]>(this._serviceBaseUrl +  '/shops/' + shopId + '/content/branchpaths/?expand=' + encodeURIComponent(param),
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all filtered content, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredContent(shopId:number, filter:SearchContextVO):Observable<SearchResultVO<ContentVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<ContentVO>>(this._serviceBaseUrl + '/shops/' + shopId + '/content/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get content, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getContentById(contentId:number):Observable<ContentWithBodyVO> {
    return this.http.get<ContentWithBodyVO>(this._serviceBaseUrl + '/content/' + contentId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create/update content.
   * @param content content
   * @returns {Observable<T>}
   */
  saveContent(content:ContentVO):Observable<ContentWithBodyVO> {

    let body = JSON.stringify(content);

    if (content.contentId > 0) {
      return this.http.put<ContentWithBodyVO>(this._serviceBaseUrl + '/content/', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<ContentWithBodyVO>(this._serviceBaseUrl + '/content/', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }



  /**
   * Remove content.
   * @param content content
   * @returns {Observable<T>}
   */
  removeContent(content:ContentVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/content/' + content.contentId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<T>}
   */
  getContentAttributes(id:number):Observable<AttrValueContentVO[]> {
    return this.http.get<AttrValueContentVO[]>(this._serviceBaseUrl + '/content/' + id + '/attributes',
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveContentAttributes(attrs:Array<Pair<AttrValueContentVO, boolean>>):Observable<AttrValueContentVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueContentVO[]>(this._serviceBaseUrl + '/content/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get attributes for given content id.
   * @param id
   * @returns {Observable<T>}
   */
  getContentBody(id:number):Observable<ContentBodyVO[]> {
    return this.http.get<ContentBodyVO[]>(this._serviceBaseUrl + '/content/' + id + '/body', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get mail preview for given order data.
   *
   * @param shopId
   * @param template
   * @param customerorder
   * @param format html or txt
   * @returns {Observable<T>}
   */
  getMailPreview(shopId:number, template:string, customerorder: string, format: string = 'html'):Observable<string> {
    return this.http.get(this._serviceBaseUrl + '/shops/' + shopId  + '/mail/' + template + '/preview?order=' + customerorder + '&format=' + format,
        { headers: Util.requestOptions({ includeAuth: true, type: 'text/plain; charset=utf-8', accept: null } ), responseType: 'text' })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('ContentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
