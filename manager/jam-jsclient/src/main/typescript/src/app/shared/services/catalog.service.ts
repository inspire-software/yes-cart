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
  BrandVO, AttrValueBrandVO,
  ProductTypeInfoVO, ProductTypeVO, ProductTypeAttrVO,
  BasicCategoryVO, CategoryVO, AttrValueCategoryVO,
  Pair, SearchContextVO, SearchResultVO,
  ProductSupplierCatalogVO
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
export class CatalogService {

  private _serviceBaseUrl = Config.API + 'service/catalog';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('CatalogService constructed');
  }

  /**
   * Get list of all brands, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredBrands(filter:SearchContextVO):Observable<SearchResultVO<BrandVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<BrandVO>>(this._serviceBaseUrl + '/brands/search', body,
            { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get brand, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getBrandById(brandId:number):Observable<BrandVO> {
    return this.http.get<BrandVO>(this._serviceBaseUrl + '/brands/' + brandId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update brand.
   * @param brand brand
   * @returns {Observable<T>}
   */
  saveBrand(brand:BrandVO):Observable<BrandVO> {

    let body = JSON.stringify(brand);

    if (brand.brandId > 0) {
      return this.http.put<BrandVO>(this._serviceBaseUrl + '/brands', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<BrandVO>(this._serviceBaseUrl + '/brands', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove brand.
   * @param brand brand
   * @returns {Observable<T>}
   */
  removeBrand(brand:BrandVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/brands/' + brand.brandId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given brand id.
   * @param id
   * @returns {Observable<T>}
   */
  getBrandAttributes(id:number):Observable<AttrValueBrandVO[]> {
    return this.http.get<AttrValueBrandVO[]>(this._serviceBaseUrl + '/brands/' + id + '/attributes', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveBrandAttributes(attrs:Array<Pair<AttrValueBrandVO, boolean>>):Observable<AttrValueBrandVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueBrandVO[]>(this._serviceBaseUrl + '/brands/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }




  /**
   * Get list of all product types, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredProductTypes(filter:SearchContextVO):Observable<SearchResultVO<ProductTypeInfoVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<ProductTypeInfoVO>>(this._serviceBaseUrl + '/producttypes/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get product type, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getProductTypeById(productTypeId:number):Observable<ProductTypeVO> {
    return this.http.get<ProductTypeVO>(this._serviceBaseUrl + '/producttypes/' + productTypeId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create/update product type.
   * @param productType product type
   * @returns {Observable<T>}
   */
  saveProductType(productType:ProductTypeVO):Observable<ProductTypeVO> {

    let body = JSON.stringify(productType);

    if (productType.producttypeId > 0) {
      return this.http.put<ProductTypeVO>(this._serviceBaseUrl + '/producttypes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<ProductTypeVO>(this._serviceBaseUrl + '/producttypes', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Create copy of product type.
   * @param productType product type
   * @returns {Observable<T>}
   */
  copyProductType(productType:ProductTypeInfoVO, copy:ProductTypeInfoVO):Observable<ProductTypeVO> {

    let body = JSON.stringify(copy);

    return this.http.post<ProductTypeVO>(this._serviceBaseUrl + '/producttypes/' + productType.producttypeId + '/copy', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Remove product type.
   * @param productType product type
   * @returns {Observable<T>}
   */
  removeProductType(productType:ProductTypeInfoVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/producttypes/' + productType.producttypeId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given product type id.
   * @param id
   * @returns {Observable<T>}
   */
  getProductTypeAttributes(id:number):Observable<ProductTypeAttrVO[]> {
    return this.http.get<ProductTypeAttrVO[]>(this._serviceBaseUrl + '/producttypes/' + id + '/attributes',
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveProductTypeAttributes(attrs:Array<Pair<ProductTypeAttrVO, boolean>>):Observable<ProductTypeAttrVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<ProductTypeAttrVO[]>(this._serviceBaseUrl + '/producttypes/attributes', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all product supplier catalogs, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getAllProductSuppliersCatalogs():Observable<ProductSupplierCatalogVO[]> {
    return this.http.get<ProductSupplierCatalogVO[]>(this._serviceBaseUrl + '/productsuppliercatalogs', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getAllCategories():Observable<CategoryVO[]> {
    return this.http.get<CategoryVO[]>(this._serviceBaseUrl + '/categories', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all categories, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getBranchCategories(root:number, expand:string[] | number[]):Observable<CategoryVO[]> {
    if (expand != null && expand.length > 0) {
      let param = '';
      expand.forEach(node => {
        param += node + '|';
      });
      return this.http.get<CategoryVO[]>(this._serviceBaseUrl + '/categories/' + root + '/branches?expand=' + encodeURIComponent(param),
          { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
    return this.http.get<CategoryVO[]>(this._serviceBaseUrl + '/categories/' + root + '/branches', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get list of all category ids leading to given category, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getBranchesCategoriesPaths(expand:string[] | number[]):Observable<number[]> {
    let param = '';
    expand.forEach(node => {
      param += node + '|';
    });
    return this.http.get<number[]>(this._serviceBaseUrl + '/categories/branchpaths/?expand=' + encodeURIComponent(param),
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get list of all filtered categories, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getFilteredCategories(filter:SearchContextVO):Observable<SearchResultVO<CategoryVO>> {

    let body = JSON.stringify(filter);

    return this.http.post<SearchResultVO<CategoryVO>>(this._serviceBaseUrl + '/categories/search', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get category, which are accessible to manage or view,
   * @returns {Observable<T>}
   */
  getCategoryById(categoryId:number):Observable<CategoryVO> {
    return this.http.get<CategoryVO>(this._serviceBaseUrl + '/categories/' + categoryId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create category on the fly.
   * @param newCat name of category
   * @param parentId parent id
   * @returns {Observable<T>}
   */
  createCategory(newCat:BasicCategoryVO, parentId : number):Observable<CategoryVO> {
    let cat = newCat.guid ? {'guid' : newCat.guid, 'name' : newCat.name, 'parentId' : parentId } : {'name' : newCat.name, 'parentId' : parentId };
    let body = JSON.stringify(cat);

    return this.http.post<CategoryVO>(this._serviceBaseUrl + '/categories', body, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Create/update category.
   * @param category category
   * @returns {Observable<T>}
   */
  saveCategory(category:CategoryVO):Observable<CategoryVO> {

    let body = JSON.stringify(category);

    if (category.categoryId > 0) {
      return this.http.put<CategoryVO>(this._serviceBaseUrl + '/categories', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<CategoryVO>(this._serviceBaseUrl + '/categories', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }



  /**
   * Remove category.
   * @param category category
   * @returns {Observable<T>}
   */
  removeCategory(category:CategoryVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/categories/' + category.categoryId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get attributes for given category id.
   * @param id
   * @returns {Observable<T>}
   */
  getCategoryAttributes(id:number):Observable<AttrValueCategoryVO[]> {
    return this.http.get<AttrValueCategoryVO[]>(this._serviceBaseUrl + '/categories/' + id + '/attributes',
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Update supported attributes.
   * @param attrs
   * @returns {Observable<T>}
   */
  saveCategoryAttributes(attrs:Array<Pair<AttrValueCategoryVO, boolean>>):Observable<AttrValueCategoryVO[]> {
    let body = JSON.stringify(attrs);
    return this.http.post<AttrValueCategoryVO[]>(this._serviceBaseUrl + '/categories/attributes', body,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  private handleError (error:any) {

    LogUtil.error('CatalogService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}
