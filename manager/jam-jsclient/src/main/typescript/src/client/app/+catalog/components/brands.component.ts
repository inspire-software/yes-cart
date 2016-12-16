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
import { BrandVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-brands',
  moduleId: module.id,
  templateUrl: 'brands.component.html',
})

export class BrandsComponent implements OnInit, OnDestroy {

  @Input() selectedBrand:BrandVO;

  @Output() dataSelected: EventEmitter<BrandVO> = new EventEmitter<BrandVO>();

  private _brands:Array<BrandVO> = [];

  private filteredBrands:Array<BrandVO>;

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
    LogUtil.debug('BrandsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('BrandsComponent ngOnInit');
  }

  @Input()
  set brands(brands:Array<BrandVO>) {
    this._brands = brands;
    this.filterBrands();
  }

  ngOnDestroy() {
    LogUtil.debug('BrandsComponent ngOnDestroy');
    this.selectedBrand = null;
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

  protected onSelectRow(row:BrandVO) {
    LogUtil.debug('BrandsComponent onSelectRow handler', row);
    if (row == this.selectedBrand) {
      this.selectedBrand = null;
    } else {
      this.selectedBrand = row;
    }
    this.dataSelected.emit(this.selectedBrand);
  }

  private filterBrands() {

    this.filteredBrands = this._brands;
    LogUtil.debug('BrandsComponent filterBrands', this.filteredBrands);

    if (this.filteredBrands === null) {
      this.filteredBrands = [];
    }

    let _total = this.filteredBrands.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
