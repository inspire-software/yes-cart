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
import {NgIf, NgClass} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {CarrierSlaVO, PaymentGatewayInfoVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-slas',
  moduleId: module.id,
  templateUrl: 'slas.component.html',
  directives: [NgIf, NgClass, PaginationComponent],
})

export class SlasComponent implements OnInit, OnDestroy {

  _slas:Array<CarrierSlaVO> = [];
  _filter:string;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  _pgs:any = {};

  filteredSlas:Array<CarrierSlaVO>;

  @Input() selectedSla:CarrierSlaVO;

  @Output() dataSelected: EventEmitter<CarrierSlaVO> = new EventEmitter<CarrierSlaVO>();

  //paging
  maxSize:number = 5;
  itemsPerPage:number = 10;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  constructor() {
    console.debug('SlasComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterSlas();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    console.debug('SlasComponent ngOnInit');
  }

  @Input()
  set paymentGateways(pgs:Array<PaymentGatewayInfoVO>) {
    pgs.forEach(pg => {
      this._pgs[pg.label] = pg;
    })
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

  private getPGNames(row:CarrierSlaVO):Array<PaymentGatewayInfoVO> {

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

  ngOnDestroy() {
    console.debug('SlasComponent ngOnDestroy');
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
    console.debug('SlasComponent onSelectRow handler', row);
    if (row == this.selectedSla) {
      this.selectedSla = null;
    } else {
      this.selectedSla = row;
    }
    this.dataSelected.emit(this.selectedSla);
  }

}
