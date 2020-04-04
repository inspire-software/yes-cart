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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ProductVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-products',
  moduleId: module.id,
  templateUrl: 'products.component.html',
})

export class ProductsComponent implements OnInit, OnDestroy {

  @Input() selectedProduct:ProductVO;

  @Output() dataSelected: EventEmitter<ProductVO> = new EventEmitter<ProductVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _products:SearchResultVO<ProductVO> = null;

  private filteredProducts:Array<ProductVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('ProductsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ProductsComponent ngOnInit');
  }

  @Input()
  set products(products:SearchResultVO<ProductVO>) {
    this._products = products;
    this.filterProducts();
  }

  ngOnDestroy() {
    LogUtil.debug('ProductsComponent ngOnDestroy');
    this.selectedProduct = null;
    this.dataSelected.emit(null);
  }

  onPageChanged(event:any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortSelected.emit(null);
      } else {  // same column asc, change to desc
        this.sortSelected.emit({ first: event, second: true });
      }
    } else { // different column, start asc sort
      this.sortSelected.emit({ first: event, second: false });
    }
  }

  protected onSelectRow(row:ProductVO) {
    LogUtil.debug('ProductsComponent onSelectRow handler', row);
    if (row == this.selectedProduct) {
      this.selectedProduct = null;
    } else {
      this.selectedProduct = row;
    }
    this.dataSelected.emit(this.selectedProduct);
  }


  protected getUri(row:ProductVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }


  protected getFlags(row:ProductVO) {
    let flags = '';
    if (row.configurable) {
      flags += '<i class="fa fa-gears"></i>&nbsp;';
    }
    return flags;
  }


  private filterProducts() {

    LogUtil.debug('ProductsComponent filterProducts', this.filteredProducts);

    if (this._products != null) {

      this.filteredProducts = this._products.items != null ? this._products.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._products.searchContext.size;
      this.totalItems = this._products.total;
      this.currentPage = this._products.searchContext.start + 1;
      this.sortColumn = this._products.searchContext.sortBy;
      this.sortDesc = this._products.searchContext.sortDesc;
    } else {
      this.filteredProducts = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
