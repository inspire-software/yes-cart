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
import {HTTP_PROVIDERS}    from '@angular/http';
import {PaymentGatewayInfoVO} from './../../../shared/model/index';
import {PaginationComponent} from './../../../shared/pagination/index';
import {Futures, Future} from './../../../shared/event/index';
import {Config} from './../../../shared/config/env.config';


@Component({
  selector: 'yc-gateways',
  moduleId: module.id,
  templateUrl: 'gateways.component.html',
  directives: [NgIf, PaginationComponent],
})

export class GatewaysComponent implements OnInit, OnDestroy {

  _gateways:Array<PaymentGatewayInfoVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  filteredGateways:Array<PaymentGatewayInfoVO>;

  @Input() selectedGateway:PaymentGatewayInfoVO;

  @Output() dataSelected: EventEmitter<PaymentGatewayInfoVO> = new EventEmitter<PaymentGatewayInfoVO>();

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
    console.debug('GatewaysComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterGateways();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('GatewaysComponent ngOnInit');
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

  private filterGateways() {
    if (this._filter) {
      this.filteredGateways = this._gateways.filter(country =>
          country.label.toLowerCase().indexOf(this._filter) !== -1 ||
          country.name.toLowerCase().indexOf(this._filter) !== -1
      );
      console.debug('GatewaysComponent filterGateways', this._filter);
    } else {
      this.filteredGateways = this._gateways;
      console.debug('GatewaysComponent filterGateways no filter');
    }

    if (this.filteredGateways === null) {
      this.filteredGateways = [];
    }

    let _total = this.filteredGateways.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('GatewaysComponent ngOnDestroy');
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
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:PaymentGatewayInfoVO) {
    console.debug('GatewaysComponent onSelectRow handler', row);
    if (row == this.selectedGateway) {
      this.selectedGateway = null;
    } else {
      this.selectedGateway = row;
    }
    this.dataSelected.emit(this.selectedGateway);
  }

}
