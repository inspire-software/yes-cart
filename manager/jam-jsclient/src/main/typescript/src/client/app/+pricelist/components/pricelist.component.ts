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
import { PriceListVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-pricelist',
  moduleId: module.id,
  templateUrl: 'pricelist.component.html',
})

export class PriceListComponent implements OnInit, OnDestroy {

  @Input() selectedPricelist:PriceListVO;

  @Output() dataSelected: EventEmitter<PriceListVO> = new EventEmitter<PriceListVO>();

  private _pricelist:Array<PriceListVO> = [];

  private filteredPricelist:Array<PriceListVO>;

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
    LogUtil.debug('PricelistComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PricelistComponent ngOnInit');
  }

  @Input()
  set pricelist(pricelist:Array<PriceListVO>) {
    this._pricelist = pricelist;
    this.filterPricelist();
  }

  ngOnDestroy() {
    LogUtil.debug('PricelistComponent ngOnDestroy');
    this.selectedPricelist = null;
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

  protected onSelectRow(row:PriceListVO) {
    LogUtil.debug('PricelistComponent onSelectRow handler', row);
    if (row == this.selectedPricelist) {
      this.selectedPricelist = null;
    } else {
      this.selectedPricelist = row;
    }
    this.dataSelected.emit(this.selectedPricelist);
  }

  private filterPricelist() {

    this.filteredPricelist = this._pricelist;
    LogUtil.debug('PricelistComponent filterPricelist', this.filteredPricelist);

    if (this.filteredPricelist === null) {
      this.filteredPricelist = [];
    }

    let _total = this.filteredPricelist.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
