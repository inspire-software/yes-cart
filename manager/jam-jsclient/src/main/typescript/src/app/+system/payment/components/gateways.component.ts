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
import { PaymentGatewayInfoVO, Pair } from './../../../shared/model/index';
import { Futures, Future } from './../../../shared/event/index';
import { Config } from './../../../../environments/environment';
import { LogUtil } from './../../../shared/log/index';


@Component({
  selector: 'cw-gateways',
  templateUrl: 'gateways.component.html',
})

export class GatewaysComponent implements OnInit, OnDestroy {

  @Input() selectedGateway:PaymentGatewayInfoVO;

  @Output() dataSelected: EventEmitter<PaymentGatewayInfoVO> = new EventEmitter<PaymentGatewayInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _gateways:Array<PaymentGatewayInfoVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public filteredGateways:Array<PaymentGatewayInfoVO>;

  //sorting
  public sortColumn:string = 'name';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('GatewaysComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterGateways();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('GatewaysComponent ngOnInit');
  }

  @Input()
  set gateways(gateways:Array<PaymentGatewayInfoVO>) {
    this._gateways = gateways;
    this.filterGateways();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  @Input()
  set sortorder(sort:Pair<string, boolean>) {
    if (sort != null && (sort.first !== this.sortColumn || sort.second !== this.sortDesc)) {
      this.sortColumn = sort.first;
      this.sortDesc = sort.second;
      this.delayedFiltering.delay();
    }
  }

  ngOnDestroy() {
    LogUtil.debug('GatewaysComponent ngOnDestroy');
    this.selectedGateway = null;
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
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'name';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterGateways();
    this.sortSelected.emit({ first: this.sortColumn, second: this.sortDesc });
  }

  onSelectRow(row:PaymentGatewayInfoVO) {
    LogUtil.debug('GatewaysComponent onSelectRow handler', row);
    if (row == this.selectedGateway) {
      this.selectedGateway = null;
    } else {
      this.selectedGateway = row;
    }
    this.dataSelected.emit(this.selectedGateway);
  }

  private filterGateways() {

    if (this._gateways) {
      if (this._filter) {
        this.filteredGateways = this._gateways.filter(country =>
          country.label.toLowerCase().indexOf(this._filter) !== -1 ||
          country.name.toLowerCase().indexOf(this._filter) !== -1
        );
        LogUtil.debug('GatewaysComponent filterGateways', this._filter);
      } else {
        this.filteredGateways = this._gateways.slice(0, this._gateways.length);
        LogUtil.debug('GatewaysComponent filterGateways no filter');
      }
    }

    if (this.filteredGateways === null) {
      this.filteredGateways = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    this.filteredGateways.sort((a, b) => {
      let _a1:any = a;
      let _b1:any = b;
      return (_a1[_sortProp] > _b1[_sortProp] ? 1 : -1) * _sortOrder;
    });

    let _total = this.filteredGateways.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
