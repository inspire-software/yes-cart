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
import { PriceListVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-pricelist',
  moduleId: module.id,
  templateUrl: 'pricelist.component.html',
})

export class PriceListComponent implements OnInit, OnDestroy {

  @Input() selectedPricelist:PriceListVO;

  @Output() dataSelected: EventEmitter<PriceListVO> = new EventEmitter<PriceListVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _pricelist:SearchResultVO<PriceListVO> = null;

  private filteredPricelist:Array<PriceListVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('PricelistComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PricelistComponent ngOnInit');
  }

  @Input()
  set pricelist(pricelist:SearchResultVO<PriceListVO>) {
    this._pricelist = pricelist;
    this.filterPricelist();
  }

  ngOnDestroy() {
    LogUtil.debug('PricelistComponent ngOnDestroy');
    this.selectedPricelist = null;
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

  protected onSelectRow(row:PriceListVO) {
    LogUtil.debug('PricelistComponent onSelectRow handler', row);
    if (row == this.selectedPricelist) {
      this.selectedPricelist = null;
    } else {
      this.selectedPricelist = row;
    }
    this.dataSelected.emit(this.selectedPricelist);
  }

  protected isAvailableFromNow(row:PriceListVO) {
    return row.salefrom === null || (row.salefrom < new Date());
  }

  protected isAvailableToNow(row:PriceListVO) {
    return row.saleto === null || (row.saleto > new Date());
  }

  protected getPolicyLabels(row:PriceListVO) {
    let out = '';
    if (row.ref) {
      if (row.pricingPolicy) {
        out += ' <span class="label label-primary">' + row.pricingPolicy + ':' + row.ref + '</span> ';
      } else {
        out += ' <span class="label label-primary">*:' + row.ref + '</span> ';
      }
    } else if (row.pricingPolicy) {
      out += ' <span class="label label-primary">' + row.pricingPolicy + '</span> ';
    }
    if (row.supplier) {
      out += ' <span class="label label-info">' + row.supplier + '</span> ';
    }
    return out;
  }

  private filterPricelist() {

    LogUtil.debug('PricelistComponent filterPricelist', this.filteredPricelist);

    if (this._pricelist != null) {

      this.filteredPricelist = this._pricelist.items != null ? this._pricelist.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._pricelist.searchContext.size;
      this.totalItems = this._pricelist.total;
      this.currentPage = this._pricelist.searchContext.start + 1;
      this.sortColumn = this._pricelist.searchContext.sortBy;
      this.sortDesc = this._pricelist.searchContext.sortDesc;
    } else {
      this.filteredPricelist = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
