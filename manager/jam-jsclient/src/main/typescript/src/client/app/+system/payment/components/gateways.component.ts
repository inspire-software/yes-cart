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
import { PaymentGatewayInfoVO } from './../../../shared/model/index';
import { Futures, Future } from './../../../shared/event/index';
import { Config } from './../../../shared/config/env.config';
import { LogUtil } from './../../../shared/log/index';


@Component({
  selector: 'yc-gateways',
  moduleId: module.id,
  templateUrl: 'gateways.component.html',
})

export class GatewaysComponent implements OnInit, OnDestroy {

  @Input() selectedGateway:PaymentGatewayInfoVO;

  @Output() dataSelected: EventEmitter<PaymentGatewayInfoVO> = new EventEmitter<PaymentGatewayInfoVO>();

  private _gateways:Array<PaymentGatewayInfoVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredGateways:Array<PaymentGatewayInfoVO>;

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
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:PaymentGatewayInfoVO) {
    LogUtil.debug('GatewaysComponent onSelectRow handler', row);
    if (row == this.selectedGateway) {
      this.selectedGateway = null;
    } else {
      this.selectedGateway = row;
    }
    this.dataSelected.emit(this.selectedGateway);
  }

  private filterGateways() {
    if (this._filter) {
      this.filteredGateways = this._gateways.filter(country =>
        country.label.toLowerCase().indexOf(this._filter) !== -1 ||
        country.name.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('GatewaysComponent filterGateways', this._filter);
    } else {
      this.filteredGateways = this._gateways;
      LogUtil.debug('GatewaysComponent filterGateways no filter');
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

}
