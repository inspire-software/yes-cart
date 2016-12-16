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
import { FulfilmentCentreVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-fulfilment-centres',
  moduleId: module.id,
  templateUrl: 'fulfilment-centres.component.html',
})

export class FulfilmentCentresComponent implements OnInit, OnDestroy {

  @Input() selectedCentre:FulfilmentCentreVO;

  @Output() dataSelected: EventEmitter<FulfilmentCentreVO> = new EventEmitter<FulfilmentCentreVO>();

  private _centres:Array<FulfilmentCentreVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredCentres:Array<FulfilmentCentreVO>;

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
    LogUtil.debug('FulfilmentCentresComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCentres();
    }, this.delayedFilteringMs);

  }

  ngOnInit() {
    LogUtil.debug('FulfilmentCentresComponent ngOnInit');
  }

  @Input()
  set centres(centres:Array<FulfilmentCentreVO>) {
    this._centres = centres;
    this.filterCentres();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentCentresComponent ngOnDestroy');
    this.selectedCentre = null;
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

  protected onSelectRow(row:FulfilmentCentreVO) {
    LogUtil.debug('FulfilmentCentresComponent onSelectRow handler', row);
    if (row == this.selectedCentre) {
      this.selectedCentre = null;
    } else {
      this.selectedCentre = row;
    }
    this.dataSelected.emit(this.selectedCentre);
  }


  private filterCentres() {
    if (this._filter) {
      this.filteredCentres = this._centres.filter(centre =>
        centre.code.toLowerCase().indexOf(this._filter) !== -1 ||
        centre.name.toLowerCase().indexOf(this._filter) !== -1 ||
        centre.description && centre.description.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('FulfilmentCentresComponent filterCentres', this._filter);
    } else {
      this.filteredCentres = this._centres;
      LogUtil.debug('FulfilmentCentresComponent filterCentres no filter');
    }

    if (this.filteredCentres === null) {
      this.filteredCentres = [];
    }

    let _total = this.filteredCentres.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


}
