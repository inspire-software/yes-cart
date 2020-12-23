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
  AssociationVO,
  AttrValueProductVO, AttrValueProductSkuVO, ProductSkuVO, ProductVO, ProductWithLinksVO,
  Pair, SearchContextVO, SearchResultVO,
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
export class PIMService {

  private _serviceBaseUrl = Config.API + 'service/pim';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('PIMService constructed');
  }


  /**
   * Get list of all product association types, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getAllAssociations():Observable<AssociationVO[]> {

    return this.http.get<AssociationVO[]>(this._serviceBaseUrl + '/associations', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of all products, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredProducts(filter:SearchContextVO):Observable<SearchResultVO<ProductVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<ProductVO>>(this._serviceBaseUrl + '/products/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get product, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getProductById(productId:number):Observable<ProductWithLinksVO> {
    return this.http.get<ProductWithLinksVO>(this._serviceBaseUrl + '/products/' + productId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create copy of product.
   * @param product product
   * @returns {Observable<T>}
   */
  copyProduct(product:ProductVO, copy:ProductVO):Observable<ProductVO> {

    let body = JSON.stringify(copy);

    return this.http.post<ProductVO>(this._serviceBaseUrl + '/products/' + product.productId + '/copy', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create/update product.
   * @param product product
   * @returns {Observable<T>}
   */
  saveProduct(product:ProductWithLinksVO):Observable<ProductWithLinksVO> {

    let body = JSON.stringify(product);

    if (product.productId > 0) {
      return this.http.put<ProductWithLinksVO>(this._serviceBaseUrl + '/products', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<ProductWithLinksVO>(this._serviceBaseUrl + '/products', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove product.
   * @param product product
   * @returns {Observable<T>}
   */
  removeProduct(product:ProductVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/products/' + product.productId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given product id.
   * @param id
   * @returns {Observable<T>}
   */
  getProductAttributes(id:number):Observable<AttrValueProductVO[]> {
    return this.http.get<AttrValueProductVO[]>(this._serviceBaseUrl + '/products/' + id + '/attributes/', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveProductAttributes(attrs:Array<Pair<AttrValueProductVO, boolean>>):Observable<AttrValueProductVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueProductVO[]>(this._serviceBaseUrl + '/products/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }




  /**
   * Get list of all product SKU, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getProductSkuAll(product:ProductVO):Observable<ProductSkuVO[]> {

    return this.http.get<ProductSkuVO[]>(this._serviceBaseUrl + '/products/' + product.productId + '/skus',
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of all products SKU, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredProductSkus(filter:SearchContextVO):Observable<SearchResultVO<ProductSkuVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<ProductSkuVO>>(this._serviceBaseUrl + '/skus/search', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get SKU, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getSkuById(skuId:number):Observable<ProductSkuVO> {
    return this.http.get<ProductSkuVO>(this._serviceBaseUrl + '/skus/' + skuId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create copy of sku.
   * @param sku product
   * @returns {Observable<T>}
   */
  copySKU(sku:ProductSkuVO, copy:ProductSkuVO):Observable<ProductSkuVO> {

    let body = JSON.stringify(copy);

    return this.http.post<ProductSkuVO>(this._serviceBaseUrl + '/skus/' + sku.skuId + '/copy', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update SKU.
   * @param sku SKU
   * @returns {Observable<T>}
   */
  saveSKU(sku:ProductSkuVO):Observable<ProductSkuVO> {

    let body = JSON.stringify(sku);

    if (sku.skuId > 0) {
      return this.http.put<ProductSkuVO>(this._serviceBaseUrl + '/skus', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<ProductSkuVO>(this._serviceBaseUrl + '/skus', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove SKU.
   * @param sku SKU
   * @returns {Observable<T>}
   */
  removeSKU(sku:ProductSkuVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/skus/' + sku.skuId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given SKU id.
   * @param id
   * @returns {Observable<T>}
   */
  getSKUAttributes(id:number):Observable<AttrValueProductSkuVO[]> {
    return this.http.get<AttrValueProductSkuVO[]>(this._serviceBaseUrl + '/skus/' + id + '/attributes', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveSKUAttributes(attrs:Array<Pair<AttrValueProductSkuVO, boolean>>):Observable<AttrValueProductSkuVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueProductSkuVO[]>(this._serviceBaseUrl + '/skus/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  private handleError (error:any) {

    LogUtil.error('PIMService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
