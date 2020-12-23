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
import { TaxVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-taxes',
  templateUrl: 'taxes.component.html',
})

export class TaxesComponent implements OnInit, OnDestroy {

  @Input() selectedShopCode: string;

  @Input() selectedTax: TaxVO;

  @Output() dataSelected: EventEmitter<TaxVO> = new EventEmitter<TaxVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _taxes: SearchResultVO<TaxVO> = null;

  public filteredTaxes: Array<TaxVO>;

  //sorting
  public sortColumn: string = null;
  public sortDesc: boolean = false;

  //paging
  public maxSize: number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage: number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems: number = 0;
  public currentPage: number = 1;

  constructor() {
    LogUtil.debug('TaxesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('TaxesComponent ngOnInit');
  }

  @Input()
  set taxes(taxes: SearchResultVO<TaxVO>) {
    this._taxes = taxes;
    this.filterTaxes();
  }

  ngOnDestroy() {
    LogUtil.debug('TaxesComponent ngOnDestroy');
    this.selectedTax = null;
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

  onSelectRow(row: TaxVO) {
    LogUtil.debug('TaxesComponent onSelectRow handler', row);
    if (row == this.selectedTax) {
      this.selectedTax = null;
    } else {
      this.selectedTax = row;
    }
    this.dataSelected.emit(this.selectedTax);
  }

  private filterTaxes() {

    LogUtil.debug('TaxesComponent filterTaxes', this.filteredTaxes);

    if (this._taxes != null) {

      this.filteredTaxes = this._taxes.items != null ? this._taxes.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._taxes.searchContext.size;
      this.totalItems = this._taxes.total;
      this.currentPage = this._taxes.searchContext.start + 1;
      this.sortColumn = this._taxes.searchContext.sortBy;
      this.sortDesc = this._taxes.searchContext.sortDesc;
    } else {
      this.filteredTaxes = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }
}
