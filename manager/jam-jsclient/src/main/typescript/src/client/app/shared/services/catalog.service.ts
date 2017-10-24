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
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Config } from '../config/env.config';
import { BrandVO, AttrValueBrandVO, ProductTypeInfoVO, ProductTypeVO, ProductTypeAttrVO, BasicCategoryVO, CategoryVO, AttrValueCategoryVO, Pair } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class CatalogService {

  private _serviceBaseUrl = Config.API + 'service/catalog';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('CatalogService constructed');
  }

  /**
   * Get list of all brands, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredBrands(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/brand/filtered/' + max, body, options)
        .map(res => <BrandVO[]> this.json(res))
        .catch(this.handleError);
  }

  /**
   * Get brand, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getBrandById(brandId:number) {
    return this.http.get(this._serviceBaseUrl + '/brand/' + brandId)
      .map(res => <BrandVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update brand.
   * @param brand brand
   * @returns {Observable<R>}
   */
  saveBrand(brand:BrandVO) {

    let body = JSON.stringify(brand);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (brand.brandId > 0) {
      return this.http.post(this._serviceBaseUrl + '/brand', body, options)
        .map(res => <BrandVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/brand', body, options)
        .map(res => <BrandVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove brand.
   * @param brand brand
   * @returns {Observable<R>}
   */
  removeBrand(brand:BrandVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/brand/' + brand.brandId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given brand id.
   * @param id
   * @returns {Observable<R>}
   */
  getBrandAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/brand/attributes/' + id)
      .map(res => <AttrValueBrandVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveBrandAttributes(attrs:Array<Pair<AttrValueBrandVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/brand/attributes', body, options)
      .map(res => <AttrValueBrandVO[]> this.json(res))
      .catch(this.handleError);
  }




  /**
   * Get list of all product types, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredProductTypes(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/producttypes/filtered/' + max, body, options)
      .map(res => <ProductTypeInfoVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get product type, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getProductTypeById(productTypeId:number) {
    return this.http.get(this._serviceBaseUrl + '/producttype/' + productTypeId)
      .map(res => <ProductTypeVO> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Create/update product type.
   * @param productType product type
   * @returns {Observable<R>}
   */
  saveProductType(productType:ProductTypeVO) {

    let body = JSON.stringify(productType);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (productType.producttypeId > 0) {
      return this.http.post(this._serviceBaseUrl + '/producttype', body, options)
        .map(res => <ProductTypeVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/producttype', body, options)
        .map(res => <ProductTypeVO> this.json(res))
        .catch(this.handleError);
    }
  }


  /**
   * Remove product type.
   * @param productType product type
   * @returns {Observable<R>}
   */
  removeProductType(productType:ProductTypeInfoVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/producttype/' + productType.producttypeId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given product type id.
   * @param id
   * @returns {Observable<R>}
   */
  getProductTypeAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/producttype/attributes/' + id)
      .map(res => <ProductTypeAttrVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveProductTypeAttributes(attrs:Array<Pair<ProductTypeAttrVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/producttype/attributes', body, options)
      .map(res => <ProductTypeAttrVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllCategories() {
    return this.http.get(this._serviceBaseUrl + '/category/all')
      .map(res => <CategoryVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getBranchCategories(root:number, expand:string[]) {
    if (expand != null && expand.length > 0) {
      let param = '';
      expand.forEach(node => {
        param += node + '|';
      });
      return this.http.get(this._serviceBaseUrl + '/category/branch/' + root + '/?expand=' + encodeURIComponent(param))
        .map(res => <CategoryVO[]> this.json(res))
        .catch(this.handleError);
    }
    return this.http.get(this._serviceBaseUrl + '/category/branch/' + root + '/')
      .map(res => <CategoryVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all category ids leading to given category, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getBranchesCategoriesPaths(expand:number[]) {
    let param = '';
    expand.forEach(node => {
      param += node + '|';
    });
    return this.http.get(this._serviceBaseUrl + '/category/branchpaths/?expand=' + encodeURIComponent(param))
      .map(res => <number[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all filtered categories, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getFilteredCategories(filter:string, max:number) {

    let body = filter;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/category/filtered/' + max, body, options)
      .map(res => <CategoryVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get category, which are accessible to manage or view,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getCategoryById(categoryId:number) {
    return this.http.get(this._serviceBaseUrl + '/category/' + categoryId)
      .map(res => <CategoryVO> this.json(res))
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
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.put(this._serviceBaseUrl + '/category', body, options)
      .map(res => <CategoryVO> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Create/update category.
   * @param category category
   * @returns {Observable<R>}
   */
  saveCategory(category:CategoryVO) {

    let body = JSON.stringify(category);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    if (category.categoryId > 0) {
      return this.http.post(this._serviceBaseUrl + '/category', body, options)
        .map(res => <CategoryVO> this.json(res))
        .catch(this.handleError);
    } else {
      return this.http.put(this._serviceBaseUrl + '/category', body, options)
        .map(res => <CategoryVO> this.json(res))
        .catch(this.handleError);
    }
  }



  /**
   * Remove category.
   * @param category category
   * @returns {Observable<R>}
   */
  removeCategory(category:CategoryVO) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/category/' + category.categoryId, options)
      .catch(this.handleError);
  }


  /**
   * Get attributes for given category id.
   * @param id
   * @returns {Observable<R>}
   */
  getCategoryAttributes(id:number) {
    return this.http.get(this._serviceBaseUrl + '/category/attributes/' + id)
      .map(res => <AttrValueCategoryVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<R>}
   */
  saveCategoryAttributes(attrs:Array<Pair<AttrValueCategoryVO, boolean>>) {
    let body = JSON.stringify(attrs);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/category/attributes', body, options)
      .map(res => <AttrValueCategoryVO[]> this.json(res))
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

    LogUtil.error('CatalogService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
