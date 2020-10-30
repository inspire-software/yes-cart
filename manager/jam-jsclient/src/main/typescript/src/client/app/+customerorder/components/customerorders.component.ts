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
import { CustomerOrderInfoVO, Pair, SearchResultVO } from './../../shared/model/index';
import { CookieUtil } from './../../shared/cookies/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-customerorders',
  moduleId: module.id,
  templateUrl: 'customerorders.component.html',
})

export class CustomerOrdersComponent implements OnInit, OnDestroy {

  @Input() selectedCustomerorder:CustomerOrderInfoVO;

  @Output() dataSelected: EventEmitter<CustomerOrderInfoVO> = new EventEmitter<CustomerOrderInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _customerorders:SearchResultVO<CustomerOrderInfoVO> = null;

  private filteredCustomerorders:Array<CustomerOrderInfoVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('CustomerOrdersComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CustomerOrdersComponent ngOnInit');
  }

  @Input()
  set customerorders(customerorders:SearchResultVO<CustomerOrderInfoVO>) {
    this._customerorders = customerorders;
    this.filterCustomerorders();
  }

  get showGrossTotal():boolean {
    return Config.UI_ORDER_TOTALS === 'gross';
  }

  set showGrossTotal(showGrossTotal:boolean) {
    Config.UI_ORDER_TOTALS = showGrossTotal ? 'gross' : 'net';
    let cookieName = 'ADM_UI_ORDER_TOTALS';
    CookieUtil.createCookie(cookieName, Config.UI_ORDER_TOTALS, 360);
  }

  onShowGrossTotalClick() {
    let _gross = this.showGrossTotal;
    this.showGrossTotal = !_gross;
  }

  ngOnDestroy() {
    LogUtil.debug('CustomerOrdersComponent ngOnDestroy');
    this.selectedCustomerorder = null;
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

  protected onSelectRow(row:CustomerOrderInfoVO) {
    LogUtil.debug('CustomerOrdersComponent onSelectRow handler', row);
    if (row == this.selectedCustomerorder) {
      this.selectedCustomerorder = null;
    } else {
      this.selectedCustomerorder = row;
    }
    this.dataSelected.emit(this.selectedCustomerorder);
  }

  protected getUserIcon(row:CustomerOrderInfoVO) {
    if (row.customerId > 0) {
      if (row.managedOrder) {
        return '<i class="fa fa-user-plus" title="' + row.managerName + ' / ' + row.managerEmail  + '"></i>';
      }
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }


  private filterCustomerorders() {

    LogUtil.debug('CustomerOrdersComponent filterCustomerorders', this._customerorders, this.filteredCustomerorders);

    if (this._customerorders != null) {
      this.filteredCustomerorders = this._customerorders.items != null ? this._customerorders.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._customerorders.searchContext.size;
      this.totalItems = this._customerorders.total;
      this.currentPage = this._customerorders.searchContext.start + 1;
      this.sortColumn = this._customerorders.searchContext.sortBy;
      this.sortDesc = this._customerorders.searchContext.sortDesc;
    } else {
      this.filteredCustomerorders = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
