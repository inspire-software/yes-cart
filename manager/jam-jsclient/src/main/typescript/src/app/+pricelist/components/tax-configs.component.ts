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
import { TaxConfigVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-tax-configs',
  templateUrl: 'tax-configs.component.html',
})

export class TaxConfigsComponent implements OnInit, OnDestroy {

  @Input() selectedShopCode: string;

  @Input() selectedTaxConfig:TaxConfigVO;

  @Output() dataSelected: EventEmitter<TaxConfigVO> = new EventEmitter<TaxConfigVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _taxconfigs:SearchResultVO<TaxConfigVO> = null;

  public filteredTaxconfigs:Array<TaxConfigVO>;

  //sorting
  public sortColumn: string = null;
  public sortDesc: boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;

  constructor() {
    LogUtil.debug('TaxConfigsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('TaxConfigsComponent ngOnInit');
  }

  @Input()
  set taxconfigs(taxconfigs:SearchResultVO<TaxConfigVO>) {
    this._taxconfigs = taxconfigs;
    this.filterTaxconfigs();
  }

  ngOnDestroy() {
    LogUtil.debug('TaxConfigsComponent ngOnDestroy');
    this.selectedTaxConfig = null;
    this.dataSelected.emit(null);
  }

  onPageChanged(event: any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
  }

  onSortClick(event: any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortSelected.emit(null);
      } else {  // same column asc, change to desc
        this.sortSelected.emit({first: event, second: true});
      }
    } else { // different column, start asc sort
      this.sortSelected.emit({first: event, second: false});
    }
  }

  onSelectRow(row:TaxConfigVO) {
    LogUtil.debug('TaxConfigsComponent onSelectRow handler', row);
    if (row == this.selectedTaxConfig) {
      this.selectedTaxConfig = null;
    } else {
      this.selectedTaxConfig = row;
    }
    this.dataSelected.emit(this.selectedTaxConfig);
  }

  private filterTaxconfigs() {

    LogUtil.debug('TaxConfigsComponent filterTaxconfigs', this.filteredTaxconfigs);

    if (this._taxconfigs != null) {

      this.filteredTaxconfigs = this._taxconfigs.items != null ? this._taxconfigs.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._taxconfigs.searchContext.size;
      this.totalItems = this._taxconfigs.total;
      this.currentPage = this._taxconfigs.searchContext.start + 1;
      this.sortColumn = this._taxconfigs.searchContext.sortBy;
      this.sortDesc = this._taxconfigs.searchContext.sortDesc;
    } else {
      this.filteredTaxconfigs = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }
}
