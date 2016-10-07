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
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ProductVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-products',
  moduleId: module.id,
  templateUrl: 'products.component.html',
  directives: [NgIf, PaginationComponent],
})

export class ProductsComponent implements OnInit, OnDestroy {

  _products:Array<ProductVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  filteredProducts:Array<ProductVO>;

  @Input() selectedProduct:ProductVO;

  @Output() dataSelected: EventEmitter<ProductVO> = new EventEmitter<ProductVO>();

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  constructor() {
    console.debug('ProductsComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterProducts();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('ProductsComponent ngOnInit');
  }

  @Input()
  set products(products:Array<ProductVO>) {
    this._products = products;
    this.filterProducts();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  private filterProducts() {

    this.filteredProducts = this._products;
    console.debug('ProductsComponent filterProducts', this.filteredProducts);

    if (this.filteredProducts === null) {
      this.filteredProducts = [];
    }

    let _total = this.filteredProducts.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('ProductsComponent ngOnDestroy');
    this.selectedProduct = null;
    this.dataSelected.emit(null);
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:ProductVO) {
    console.debug('ProductsComponent onSelectRow handler', row);
    if (row == this.selectedProduct) {
      this.selectedProduct = null;
    } else {
      this.selectedProduct = row;
    }
    this.dataSelected.emit(this.selectedProduct);
  }


  protected isAvailableFromNow(row:ProductVO) {
    // Preorder (2) ignores the from date as we want to show them before they are available
    return row.availability == 2 || row.availablefrom === null || (row.availablefrom < new Date());
  }

  protected isAvailableToNow(row:ProductVO) {
    return row.availableto === null || (row.availableto > new Date());
  }


  protected getUri(row:ProductVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }

}
