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
import {CategoryVO, BasicCategoryVO} from '../model/index';
import {ErrorEventBus} from './error-event-bus.service';
import {Util} from './util';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class CategoryService {

  private _serviceBaseUrl = Config.API + 'service/category';  // URL to web api

  /**
   * Construct category service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('CategoryService constructed');
  }

  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllCategories() {
    return this.http.get(this._serviceBaseUrl + '/all')
      .map(res => <CategoryVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Create category on the fly.
   * @param newCat name of category
   * @param parentId parent id
   * @returns {Observable<R>}
     */
  createCategory(newCat:BasicCategoryVO, parentId : number) {
    var cat = newCat.guid ? {'guid' : newCat.guid, 'name' : newCat.name, 'parentId' : parentId } : {'name' : newCat.name, 'parentId' : parentId };
    let body = JSON.stringify(cat);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl, body, options)
      .map(res => <CategoryVO> res.json())
      .catch(this.handleError);
  }

  private handleError (error:any) {

    console.error('CategoryService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message || 'Server error');
  }

}
