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
import { TaxConfigVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-tax-configs',
  moduleId: module.id,
  templateUrl: 'tax-configs.component.html',
})

export class TaxConfigsComponent implements OnInit, OnDestroy {

  @Input() selectedTaxConfig:TaxConfigVO;

  @Output() dataSelected: EventEmitter<TaxConfigVO> = new EventEmitter<TaxConfigVO>();

  private _taxconfigs:Array<TaxConfigVO> = [];

  private filteredTaxconfigs:Array<TaxConfigVO>;

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
    LogUtil.debug('TaxConfigsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('TaxConfigsComponent ngOnInit');
  }

  @Input()
  set taxconfigs(taxconfigs:Array<TaxConfigVO>) {
    this._taxconfigs = taxconfigs;
    this.filterTaxconfigs();
  }

  ngOnDestroy() {
    LogUtil.debug('TaxConfigsComponent ngOnDestroy');
    this.selectedTaxConfig = null;
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

  protected onSelectRow(row:TaxConfigVO) {
    LogUtil.debug('TaxConfigsComponent onSelectRow handler', row);
    if (row == this.selectedTaxConfig) {
      this.selectedTaxConfig = null;
    } else {
      this.selectedTaxConfig = row;
    }
    this.dataSelected.emit(this.selectedTaxConfig);
  }

  private filterTaxconfigs() {

    this.filteredTaxconfigs = this._taxconfigs;
    LogUtil.debug('TaxConfigsComponent filterTaxconfigs', this.filteredTaxconfigs);

    if (this.filteredTaxconfigs === null) {
      this.filteredTaxconfigs = [];
    }

    let _total = this.filteredTaxconfigs.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
