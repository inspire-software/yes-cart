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
import {CarrierVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';


@Component({
  selector: 'yc-carriers',
  moduleId: module.id,
  templateUrl: 'carriers.component.html',
  directives: [NgIf, PaginationComponent],
})

export class CarriersComponent implements OnInit, OnDestroy {

  _carriers:Array<CarrierVO> = [];
  _filter:string;

  filteredCarriers:Array<CarrierVO>;

  @Input() selectedCarrier:CarrierVO;

  @Output() dataSelected: EventEmitter<CarrierVO> = new EventEmitter<CarrierVO>();

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
    console.debug('CarriersComponent constructed');
  }

  ngOnInit() {
    console.debug('CarriersComponent ngOnInit');
  }

  @Input()
  set carriers(carriers:Array<CarrierVO>) {
    this._carriers = carriers;
    this.filterCarriers();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.filterCarriers();
  }

  private filterCarriers() {
    if (this._filter) {
      this.filteredCarriers = this._carriers.filter(carrier =>
          carrier.name.toLowerCase().indexOf(this._filter) !== -1 ||
          carrier.description && carrier.description.toLowerCase().indexOf(this._filter) !== -1
      );
      console.debug('CarriersComponent filterCarriers', this._filter);
    } else {
      this.filteredCarriers = this._carriers;
      console.debug('CarriersComponent filterCarriers no filter');
    }

    if (this.filteredCarriers === null) {
      this.filteredCarriers = [];
    }

    let _total = this.filteredCarriers.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('CarriersComponent ngOnDestroy');
    this.selectedCarrier = null;
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

  protected onSelectRow(row:CarrierVO) {
    console.debug('CarriersComponent onSelectRow handler', row);
    if (row == this.selectedCarrier) {
      this.selectedCarrier = null;
    } else {
      this.selectedCarrier = row;
    }
    this.dataSelected.emit(this.selectedCarrier);
  }

}
