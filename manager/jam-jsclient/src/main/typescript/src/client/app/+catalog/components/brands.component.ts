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
import { BrandVO, Pair, SearchResultVO } from './../../shared/model/index';
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

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _brands:SearchResultVO<BrandVO> = null;

  private filteredBrands:Array<BrandVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('BrandsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('BrandsComponent ngOnInit');
  }

  @Input()
  set brands(brands:SearchResultVO<BrandVO>) {
    this._brands = brands;
    this.filterBrands();
  }

  ngOnDestroy() {
    LogUtil.debug('BrandsComponent ngOnDestroy');
    this.selectedBrand = null;
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

    LogUtil.debug('BrandsComponent filterBrands', this.filteredBrands);

    if (this._brands != null) {

      this.filteredBrands = this._brands.items != null ? this._brands.items : [];
      this.totalItems = 0;
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._brands.searchContext.size;
      this.totalItems = this._brands.total;
      this.currentPage = this._brands.searchContext.start + 1;
      this.sortColumn = this._brands.searchContext.sortBy;
      this.sortDesc = this._brands.searchContext.sortDesc;
    } else {
      this.filteredBrands = [];
      this.totalItems = 0;
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
