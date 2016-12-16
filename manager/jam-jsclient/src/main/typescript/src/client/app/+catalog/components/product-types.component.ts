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
import { ProductTypeInfoVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-product-types',
  moduleId: module.id,
  templateUrl: 'product-types.component.html',
})

export class ProductTypesComponent implements OnInit, OnDestroy {

  @Input() selectedProductType:ProductTypeInfoVO;

  @Output() dataSelected: EventEmitter<ProductTypeInfoVO> = new EventEmitter<ProductTypeInfoVO>();

  private _productTypes:Array<ProductTypeInfoVO> = [];

  private filteredProductTypes:Array<ProductTypeInfoVO>;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('ProductTypesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ProductTypesComponent ngOnInit');
  }

  @Input()
  set productTypes(productTypes:Array<ProductTypeInfoVO>) {
    this._productTypes = productTypes;
    this.filterProductTypes();
  }

  ngOnDestroy() {
    LogUtil.debug('ProductTypesComponent ngOnDestroy');
    this.selectedProductType = null;
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
    if (row.ensemble) {
      flags += '<i class="fa fa-gears"></i>&nbsp;';
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

    this.filteredProductTypes = this._productTypes;
    LogUtil.debug('ProductTypesComponent filterProductTypes', this.filteredProductTypes);

    if (this.filteredProductTypes === null) {
      this.filteredProductTypes = [];
    }

    let _total = this.filteredProductTypes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


}
