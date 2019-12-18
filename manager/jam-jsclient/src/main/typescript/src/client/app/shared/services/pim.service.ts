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
  AssociationVO,
  AttrValueProductVO, AttrValueProductSkuVO, ProductSkuVO, ProductVO, ProductWithLinksVO,
  Pair, SearchContextVO, SearchResultVO,
} from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class PIMService {

  private _serviceBaseUrl = Config.API + 'service/pim';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('PIMService constructed');
  }


  /**
   * Get list of all product association types, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllAssociations() {

    return this.http.get(this._serviceBaseUrl + '/associations/all', Util.requestOptions())
      .map(res => <AssociationVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get list of all products, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredProducts(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/product/filtered', body,
          Util.requestOptions())
      .map(res => <ProductVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get product, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductById(productId:number) {
    return this.http.get(this._serviceBaseUrl + '/product/' + productId, Util.requestOptions())
      .map(res => <ProductWithLinksVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update product.
   * @param product product
   * @returns {Observable<R>}
   */
  saveProduct(product:ProductWithLinksVO) {

    let body = JSON.stringify(product);

    if (product.productId > 0) {
      return this.http.post(this._serviceBaseUrl + '/product', body, Util.requestOptions())
        .map(res => <ProductWithLinksVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/product', body, Util.requestOptions())
        .map(res => <ProductWithLinksVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove product.
   * @param product product
   * @returns {Observable<R>}
   */
  removeProduct(product:ProductVO) {

    return this.http.delete(this._serviceBaseUrl + '/product/' + product.productId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get attributes for given product id.
   * @param id
   * @returns {Observable<R>}
   */
  getProductAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/product/attributes/' + id, Util.requestOptions())
      .map(res => <AttrValueProductVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveProductAttributes(attrs:Array<Pair<AttrValueProductVO, boolean>>) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + '/product/attributes', body, Util.requestOptions())
      .map(res => <AttrValueProductVO[]> this.json(res))
      .catch(this.handleError);
  }




  /**
   * Get list of all product SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductSkuAll(product:ProductVO) {

    return this.http.get(this._serviceBaseUrl + '/product/sku/' + product.productId, Util.requestOptions())
      .map(res => <ProductSkuVO[]> this.json(res))
      .catch(this.handleError);
  }



  /**
   * Get list of all products SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredProductSkus(filter:SearchContextVO) {

    let body = JSON.stringify(filter);

    return this.http.post(this._serviceBaseUrl + '/product/sku/filtered', body,
          Util.requestOptions())
      .map(res => <ProductSkuVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getSkuById(skuId:number) {
    return this.http.get(this._serviceBaseUrl + '/sku/' + skuId, Util.requestOptions())
      .map(res => <ProductSkuVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update SKU.
   * @param sku SKU
   * @returns {Observable<R>}
   */
  saveSKU(sku:ProductSkuVO) {

    let body = JSON.stringify(sku);

    if (sku.skuId > 0) {
      return this.http.post(this._serviceBaseUrl + '/sku', body, Util.requestOptions())
        .map(res => <ProductSkuVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/sku', body, Util.requestOptions())
        .map(res => <ProductSkuVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove SKU.
   * @param sku SKU
   * @returns {Observable<R>}
   */
  removeSKU(sku:ProductSkuVO) {

    return this.http.delete(this._serviceBaseUrl + '/sku/' + sku.skuId, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Get attributes for given SKU id.
   * @param id
   * @returns {Observable<R>}
   */
  getSKUAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/sku/attributes/' + id, Util.requestOptions())
      .map(res => <AttrValueProductSkuVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveSKUAttributes(attrs:Array<Pair<AttrValueProductSkuVO, boolean>>) {
    let body = JSON.stringify(attrs);
    return this.http.post(this._serviceBaseUrl + '/sku/attributes', body, Util.requestOptions())
      .map(res => <AttrValueProductSkuVO[]> this.json(res))
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

    LogUtil.error('PIMService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
