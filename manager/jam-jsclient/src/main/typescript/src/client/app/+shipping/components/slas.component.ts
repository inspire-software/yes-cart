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
import { CarrierSlaVO, PaymentGatewayInfoVO, FulfilmentCentreInfoVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-slas',
  moduleId: module.id,
  templateUrl: 'slas.component.html',
})

export class SlasComponent implements OnInit, OnDestroy {

  @Input() selectedSla:CarrierSlaVO;

  @Output() dataSelected: EventEmitter<CarrierSlaVO> = new EventEmitter<CarrierSlaVO>();

  private _slas:Array<CarrierSlaVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private _pgs:any = {};
  private _fcs:any = {};

  private filteredSlas:Array<CarrierSlaVO>;

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
    LogUtil.debug('SlasComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterSlas();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('SlasComponent ngOnInit');
  }

  @Input()
  set paymentGateways(pgs:Array<PaymentGatewayInfoVO>) {
    pgs.forEach(pg => {
      this._pgs[pg.label] = pg;
    });
    LogUtil.debug('SlasComponent mapped PGs', this._pgs);
  }

  @Input()
  set fulfilmentCentres(fcs:Array<FulfilmentCentreInfoVO>) {
    fcs.forEach(fc => {
      this._fcs[fc.code] = fc;
    });
    LogUtil.debug('SlasComponent mapped FCs', this._fcs);
  }

  @Input()
  set slas(slas:Array<CarrierSlaVO>) {
    this._slas = slas;
    this.filterSlas();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('SlasComponent ngOnDestroy');
    this.selectedSla = null;
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

  protected onSelectRow(row:CarrierSlaVO) {
    LogUtil.debug('SlasComponent onSelectRow handler', row);
    if (row == this.selectedSla) {
      this.selectedSla = null;
    } else {
      this.selectedSla = row;
    }
    this.dataSelected.emit(this.selectedSla);
  }


  protected getPGNames(row:CarrierSlaVO):Array<PaymentGatewayInfoVO> {

    let supported = row.supportedPaymentGateways;
    if (!supported) {
      return [ { name: '-', label: '-', active: true } ];
    }

    let labels = <Array<PaymentGatewayInfoVO>>[];
    supported.forEach(label => {
      if (this._pgs.hasOwnProperty(label)) {
        labels.push(this._pgs[label]);
      } else {
        labels.push({ name: label, label: label, active: false });
      }
    });
    return labels;
  }


  protected getFCNames(row:CarrierSlaVO):Array<FulfilmentCentreInfoVO> {

    let supported = row.supportedFulfilmentCentres;
    if (!supported) {
      return [ {
        warehouseId: 0, code: '-', name: '-', description: null,
        countryCode: null, stateCode: null, city: null, postcode: null,
        defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0,
        multipleShippingSupported: false,
        displayNames: []
      } ];
    }

    let labels = <Array<FulfilmentCentreInfoVO>>[];
    supported.forEach(code => {
      if (this._fcs.hasOwnProperty(code)) {
        labels.push(this._fcs[code]);
      } else {
        labels.push({
          warehouseId: 0, code: code, name: code, description: null,
          countryCode: null, stateCode: null, city: null, postcode: null,
          defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0,
          multipleShippingSupported: false,
          displayNames: []
        });
      }
    });
    return labels;
  }

  private filterSlas() {
    if (this._filter) {
      this.filteredSlas = this._slas.filter(sla =>
        sla.code.toLowerCase().indexOf(this._filter) !== -1 ||
        sla.name.toLowerCase().indexOf(this._filter) !== -1 ||
        sla.description && sla.description.toLowerCase().indexOf(this._filter) !== -1
      );
    } else {
      this.filteredSlas = this._slas;
    }

    if (this.filteredSlas === null) {
      this.filteredSlas = [];
    }

    let _total = this.filteredSlas.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
