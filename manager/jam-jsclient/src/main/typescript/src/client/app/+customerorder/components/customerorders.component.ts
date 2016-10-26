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
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {CustomerOrderInfoVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-customerorders',
  moduleId: module.id,
  templateUrl: 'customerorders.component.html',
  directives: [NgIf, PaginationComponent],
})

export class CustomerOrdersComponent implements OnInit, OnDestroy {

  _customerorders:Array<CustomerOrderInfoVO> = [];

  filteredCustomerorders:Array<CustomerOrderInfoVO>;

  @Input() selectedCustomerorder:CustomerOrderInfoVO;

  @Output() dataSelected: EventEmitter<CustomerOrderInfoVO> = new EventEmitter<CustomerOrderInfoVO>();

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  constructor() {
    console.debug('CustomerOrdersComponent constructed');
  }

  ngOnInit() {
    console.debug('CustomerOrdersComponent ngOnInit');
  }

  @Input()
  set customerorders(customerorders:Array<CustomerOrderInfoVO>) {
    this._customerorders = customerorders;
    this.filterCustomerorders();
  }

  private filterCustomerorders() {

    this.filteredCustomerorders = this._customerorders;
    console.debug('CustomerOrdersComponent filterCustomerorders', this.filteredCustomerorders);

    if (this.filteredCustomerorders === null) {
      this.filteredCustomerorders = [];
    }

    let _total = this.filteredCustomerorders.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('CustomerOrdersComponent ngOnDestroy');
    this.selectedCustomerorder = null;
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

  protected onSelectRow(row:CustomerOrderInfoVO) {
    console.debug('CustomerOrdersComponent onSelectRow handler', row);
    if (row == this.selectedCustomerorder) {
      this.selectedCustomerorder = null;
    } else {
      this.selectedCustomerorder = row;
    }
    this.dataSelected.emit(this.selectedCustomerorder);
  }

  protected getUserIcon(row:CustomerOrderInfoVO) {
    if (row.customerId > 0) {
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }

}
