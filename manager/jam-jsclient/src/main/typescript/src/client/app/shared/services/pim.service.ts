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
import { AssociationVO, AttrValueProductVO, AttrValueProductSkuVO, ProductSkuVO, ProductVO, ProductWithLinksVO, Pair } from '../model/index';
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

    return this.http.get(this._serviceBaseUrl + '/associations/all')
      .map(res => <AssociationVO[]> res.json())
      .catch(this.handleError);
  }



  /**
   * Get list of all products, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredProducts(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/product/filtered/' + max, body, options)
      .map(res => <ProductVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get product, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductById(productId:number) {
    return this.http.get(this._serviceBaseUrl + '/product/' + productId)
      .map(res => <ProductWithLinksVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create/update product.
   * @param product product
   * @returns {Observable<R>}
   */
  saveProduct(product:ProductWithLinksVO) {

    let body = JSON.stringify(product);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (product.productId > 0) {
      return this.http.post(this._serviceBaseUrl + '/product', body, options)
        .map(res => <ProductWithLinksVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/product', body, options)
        .map(res => <ProductWithLinksVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Remove product.
   * @param product product
   * @returns {Observable<R>}
   */
  removeProduct(product:ProductVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/product/' + product.productId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given product id.
   * @param id
   * @returns {Observable<R>}
   */
  getProductAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/product/attributes/' + id)
      .map(res => <AttrValueProductVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveProductAttributes(attrs:Array<Pair<AttrValueProductVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/product/attributes', body, options)
      .map(res => <AttrValueProductVO[]> res.json())
      .catch(this.handleError);
  }




  /**
   * Get list of all product SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductSkuAll(product:ProductVO) {

    return this.http.get(this._serviceBaseUrl + '/product/sku/' + product.productId)
      .map(res => <ProductSkuVO[]> res.json())
      .catch(this.handleError);
  }



  /**
   * Get list of all products SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredProductSkus(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/product/sku/filtered/' + max, body, options)
      .map(res => <ProductSkuVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Get SKU, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getSkuById(skuId:number) {
    return this.http.get(this._serviceBaseUrl + '/sku/' + skuId)
      .map(res => <ProductSkuVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Create/update SKU.
   * @param sku SKU
   * @returns {Observable<R>}
   */
  saveSKU(sku:ProductSkuVO) {

    let body = JSON.stringify(sku);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (sku.skuId > 0) {
      return this.http.post(this._serviceBaseUrl + '/sku', body, options)
        .map(res => <ProductSkuVO> res.json())
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/sku', body, options)
        .map(res => <ProductSkuVO> res.json())
        .catch(this.handleError);
    }
  }


  /**
   * Remove SKU.
   * @param sku SKU
   * @returns {Observable<R>}
   */
  removeSKU(sku:ProductSkuVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/sku/' + sku.skuId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given SKU id.
   * @param id
   * @returns {Observable<R>}
   */
  getSKUAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/sku/attributes/' + id)
      .map(res => <AttrValueProductSkuVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveSKUAttributes(attrs:Array<Pair<AttrValueProductSkuVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/sku/attributes', body, options)
      .map(res => <AttrValueProductSkuVO[]> res.json())
      .catch(this.handleError);
  }






  private handleError (error:any) {

    LogUtil.error('PIMService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
