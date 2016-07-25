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
import {CategoryVO} from '../model/index';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class CategoryService {

  private _shopUrl = Config.API + 'service/category';  // URL to web api

  /**
   * Constrcut category service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('CategoryService constructed');
  }

  /**
   * Get list of all shop, which are accesable to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllCategories() {
    return this.http.get(this._shopUrl + '/all')
      .map(res => <CategoryVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Create category on the fly.
   * @param name name of category
   * @param parentId parent id
   * @returns {Observable<R>}
     */
  createCategory(name:string, parentId : number) {
    var cat = {'name' : name, 'parentId' : parentId };
    let body = JSON.stringify(cat);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._shopUrl, body, options)
      .map(res => <CategoryVO> res.json())
      .catch(this.handleError);
  }

  private handleError (error: Response) {
    // in a real world app, we may send the error to some remote logging infrastructure
    // instead of just logging it to the console
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }

}
