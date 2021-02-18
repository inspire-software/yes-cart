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
  PriceListVO,
  TaxVO, TaxConfigVO,
  PromotionVO, PromotionCouponVO,
  CartVO, PromotionTestVO,
  SearchContextVO, SearchResultVO,
  AttributeVO, Pair
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
export class PricingService {

  private _serviceBaseUrl = Config.API + 'service/pricing';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('PricingService constructed');
  }

  /**
   * Get list of all price lists, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredPriceLists(filter:SearchContextVO):Observable<SearchResultVO<PriceListVO>> {

    let body = JSON.stringify(filter);
    return this.http.post<SearchResultVO<PriceListVO>>(this._serviceBaseUrl + '/prices/search', body,
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }

  /**
   * Get price list, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getPriceListById(priceId:number):Observable<PriceListVO> {
    return this.http.get<PriceListVO>(this._serviceBaseUrl + '/prices/' + priceId)
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update price list.
   * @param price price list
   * @returns {Observable<T>}
   */
  savePriceList(price:PriceListVO):Observable<PriceListVO> {

    let body = JSON.stringify(price);

    if (price.skuPriceId > 0) {
      return this.http.put<PriceListVO>(this._serviceBaseUrl + '/prices', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<PriceListVO>(this._serviceBaseUrl + '/prices', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove price list.
   * @param price list price list
   * @returns {Observable<T>}
   */
  removePriceList(price:PriceListVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/prices/' + price.skuPriceId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }



  /**
   * Get list of all taxes, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredTax(filter:SearchContextVO):Observable<SearchResultVO<TaxVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<TaxVO>>(this._serviceBaseUrl + '/taxes/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get tax, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getTaxById(taxId:number):Observable<TaxVO> {
    return this.http.get<TaxVO>(this._serviceBaseUrl + '/taxes/' + taxId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update tax.
   * @param tax tax
   * @returns {Observable<T>}
   */
  saveTax(tax:TaxVO):Observable<TaxVO> {

    let body = JSON.stringify(tax);

    if (tax.taxId > 0) {
      return this.http.put<TaxVO>(this._serviceBaseUrl + '/taxes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<TaxVO>(this._serviceBaseUrl + '/taxes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove tax.
   * @param tax tax
   * @returns {Observable<T>}
   */
  removeTax(tax:TaxVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/taxes/' + tax.taxId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }



  /**
   * Get list of all tax configs, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredTaxConfig(filter:SearchContextVO):Observable<SearchResultVO<TaxConfigVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<TaxConfigVO>>(this._serviceBaseUrl + '/taxconfigs/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get tax config, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getTaxConfigById(taxConfigId:number):Observable<TaxConfigVO> {
    return this.http.get<TaxConfigVO>(this._serviceBaseUrl + '/taxconfigs/' + taxConfigId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create tax config.
   * @param taxConfig config
   * @returns {Observable<T>}
   */
  createTaxConfig(taxConfig:TaxConfigVO):Observable<TaxConfigVO> {

    let body = JSON.stringify(taxConfig);

    return this.http.post<TaxConfigVO>(this._serviceBaseUrl + '/taxconfigs', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Remove tax config.
   * @param taxConfig tax config
   * @returns {Observable<T>}
   */
  removeTaxConfig(taxConfig:TaxConfigVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/taxconfigs/' + taxConfig.taxConfigId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }



  /**
   * Test rule for given shop in specified currency,
   * @returns {Observable<T>}
   */
  testPromotions(test:PromotionTestVO):Observable<CartVO> {

    let body = JSON.stringify(test);

    return this.http.post<CartVO>(this._serviceBaseUrl + '/promotions/test', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get promotions options, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getPromotionOptions():Observable<Pair<AttributeVO, AttributeVO[]>[]> {
    return this.http.get<Pair<AttributeVO, AttributeVO[]>[]>(this._serviceBaseUrl + '/promotions/options', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all promotions, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredPromotions(filter:SearchContextVO):Observable<SearchResultVO<PromotionVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<PromotionVO>>(this._serviceBaseUrl + '/promotions/search', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get promotion, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getPromotionById(promotionId:number):Observable<PromotionVO> {
    return this.http.get<PromotionVO>(this._serviceBaseUrl + '/promotions/' + promotionId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update promotion.
   * @param promotion promotion
   * @returns {Observable<T>}
   */
  savePromotion(promotion:PromotionVO):Observable<PromotionVO> {

    let body = JSON.stringify(promotion);

    if (promotion.promotionId > 0) {
      return this.http.put<PromotionVO>(this._serviceBaseUrl + '/promotions', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<PromotionVO>(this._serviceBaseUrl + '/promotions', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove promotion.
   * @param promotion promotion
   * @returns {Observable<T>}
   */
  removePromotion(promotion:PromotionVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/promotions/' + promotion.promotionId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Update promotion state.
   * @param promotion promotion
   * @param state enabled or disabled
   * @returns {Observable<T>}
   */
  updatePromotionDisabledFlag(promotion:PromotionVO, state:boolean):Observable<boolean> {
    LogUtil.debug('PricingService change state promotion ' + promotion.promotionId + ' to ' + state ? 'online' : 'offline');

    let body = JSON.stringify({ disabled: state });

    return this.http.post(this._serviceBaseUrl + '/promotions/' + promotion.promotionId + '/status', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get list of all promotion coupons, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredPromotionCoupons(filter:SearchContextVO):Observable<SearchResultVO<PromotionCouponVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<PromotionCouponVO>>(this._serviceBaseUrl + '/promotioncoupons/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create promotion coupons.
   * @param coupons coupons
   * @returns {Observable<T>}
   */
  createPromotionCoupons(coupons:PromotionCouponVO):Observable<boolean> {

    let body = JSON.stringify(coupons);

    return this.http.post(this._serviceBaseUrl + '/promotioncoupons', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Remove promotion coupon.
   * @param coupon coupon
   * @returns {Observable<T>}
   */
  removePromotionCoupon(coupon:PromotionCouponVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/promotioncoupons/' + coupon.promotioncouponId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }



  private handleError (error:any) {

    LogUtil.error('PricingService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
