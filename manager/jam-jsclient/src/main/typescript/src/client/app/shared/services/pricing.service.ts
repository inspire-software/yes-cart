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
import {ShopVO, PriceListVO} from '../model/index';
import {ErrorEventBus} from './error-event-bus.service';
import {Util} from './util';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class PricingService {

  private _serviceBaseUrl = Config.API + 'service/pricing';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    console.debug('PricingService constructed');
  }

  /**
   * Get list of all price lists, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredPriceLists(shop:ShopVO, currency:string, filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/price/shop/' + shop.shopId + '/currency/' + currency + '/filtered/' + max, body, options)
        .map(res => <PriceListVO[]> res.json())
        .catch(this.handleError);
  }

  /**
   * Get price list, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPriceListById(priceId:number) {
    return this.http.get(this._serviceBaseUrl + '/price/' + priceId)
      .map(res => <PriceListVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create/update price list.
   * @param price price list
   * @returns {Observable<R>}
   */
  savePriceList(price:PriceListVO) {

    let body = JSON.stringify(price);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    if (price.skuPriceId > 0) {
      return this.http.post(this._serviceBaseUrl + '/price', body, options)
        .map(res => <PriceListVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/price', body, options)
        .map(res => <PriceListVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Remove price list.
   * @param price list price list
   * @returns {Observable<R>}
   */
  removePriceList(price:PriceListVO) {
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/price/' + price.skuPriceId, options)
      .catch(this.handleError);
  }


  private handleError (error:any) {

    console.error('PricingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
