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
import { PaymentVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-payments',
  moduleId: module.id,
  templateUrl: 'payments.component.html',
})

export class PaymentsComponent implements OnInit, OnDestroy {

  @Input() selectedPayment:PaymentVO;

  @Output() dataSelected: EventEmitter<PaymentVO> = new EventEmitter<PaymentVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _payments:SearchResultVO<PaymentVO> = null;

  private filteredPayments:Array<PaymentVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('PaymentsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PaymentsComponent ngOnInit');
  }

  @Input()
  set payments(payments:SearchResultVO<PaymentVO>) {
    this._payments = payments;
    this.filterPayments();
  }

  ngOnDestroy() {
    LogUtil.debug('PaymentsComponent ngOnDestroy');
    this.selectedPayment = null;
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

  protected onSelectRow(row:PaymentVO) {
    LogUtil.debug('PaymentsComponent onSelectRow handler', row);
    if (row == this.selectedPayment) {
      this.selectedPayment = null;
    } else {
      this.selectedPayment = row;
    }
    this.dataSelected.emit(this.selectedPayment);
  }

  private filterPayments() {

    LogUtil.debug('PaymentsComponent filterPayments', this._payments, this.filteredPayments);

    if (this._payments) {
      this.filteredPayments = this._payments != null ? this._payments.items : [];
      this.totalItems = 0;
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._payments.searchContext.size;
      this.totalItems = this._payments.total;
      this.currentPage = this._payments.searchContext.start + 1;
      this.sortColumn = this._payments.searchContext.sortBy;
      this.sortDesc = this._payments.searchContext.sortDesc;
    } else {
      this.filteredPayments = [];
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
