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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ProductTypeInfoVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-product-types',
  moduleId: module.id,
  templateUrl: 'product-types.component.html',
})

export class ProductTypesComponent implements OnInit, OnDestroy {

  @Input() selectedProductType:ProductTypeInfoVO;

  @Output() dataSelected: EventEmitter<ProductTypeInfoVO> = new EventEmitter<ProductTypeInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _productTypes:SearchResultVO<ProductTypeInfoVO> = null;

  private filteredProductTypes:Array<ProductTypeInfoVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('ProductTypesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ProductTypesComponent ngOnInit');
  }

  @Input()
  set productTypes(productTypes:SearchResultVO<ProductTypeInfoVO>) {
    this._productTypes = productTypes;
    this.filterProductTypes();
  }

  ngOnDestroy() {
    LogUtil.debug('ProductTypesComponent ngOnDestroy');
    this.selectedProductType = null;
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

  protected onSelectRow(row:ProductTypeInfoVO) {
    LogUtil.debug('ProductTypesComponent onSelectRow handler', row);
    if (row == this.selectedProductType) {
      this.selectedProductType = null;
    } else {
      this.selectedProductType = row;
    }
    this.dataSelected.emit(this.selectedProductType);
  }


  protected getFlags(row:ProductTypeInfoVO) {
    let flags = '';
    if (row.service) {
      flags += '<i class="fa fa-users"></i>&nbsp;';
    }
    if (row.shippable) {
      flags += '<i class="fa fa-truck"></i>&nbsp;';
    }
    if (row.downloadable) {
      flags += '<i class="fa fa-download"></i>&nbsp;';
    }
    if (row.digital) {
      flags += '<i class="fa fa-database"></i>&nbsp;';
    }
    return flags;
  }

  private filterProductTypes() {

    LogUtil.debug('ProductTypesComponent filterProductTypes', this.filteredProductTypes);

    if (this._productTypes != null) {

      this.filteredProductTypes = this._productTypes.items != null ? this._productTypes.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._productTypes.searchContext.size;
      this.totalItems = this._productTypes.total;
      this.currentPage = this._productTypes.searchContext.start + 1;
      this.sortColumn = this._productTypes.searchContext.sortBy;
      this.sortDesc = this._productTypes.searchContext.sortDesc;
    } else {
      this.filteredProductTypes = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
