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
import { ProductSkuVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-skus',
  moduleId: module.id,
  templateUrl: 'skus.component.html',
})

export class SKUsComponent implements OnInit, OnDestroy {

  @Input() selectedSku:ProductSkuVO;

  @Output() dataSelected: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  private _skus:Array<ProductSkuVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredSkus:Array<ProductSkuVO>;

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
    LogUtil.debug('SKUsComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterSkus();
    }, this.delayedFilteringMs);
  }

  ngOnInit() {
    LogUtil.debug('SKUsComponent ngOnInit');
  }

  @Input()
  set skus(skus:Array<ProductSkuVO>) {
    this._skus = skus;
    this.filterSkus();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('SKUsComponent ngOnDestroy');
    this.selectedSku = null;
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

  protected onSelectRow(row:ProductSkuVO) {
    LogUtil.debug('SKUsComponent onSelectRow handler', row);
    if (row == this.selectedSku) {
      this.selectedSku = null;
    } else {
      this.selectedSku = row;
    }
    this.dataSelected.emit(this.selectedSku);
  }


  protected getUri(row:ProductSkuVO) {
    if (row.uri) {
      return '<i  title="' + row.uri + '" class="fa fa-globe"></i>';
    }
    return '';
  }


  private filterSkus() {

    if (this._filter) {
      this.filteredSkus = this._skus.filter(sku =>
        sku.guid.toLowerCase().indexOf(this._filter) !== -1 ||
        sku.code.toLowerCase().indexOf(this._filter) !== -1 ||
        sku.name.toLowerCase().indexOf(this._filter) !== -1 ||
        sku.manufacturerCode && sku.manufacturerCode.toLowerCase().indexOf(this._filter) !== -1 ||
        sku.barCode && sku.barCode.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('SKUsComponent filterSkus', this._filter);
    } else {
      this.filteredSkus = this._skus;
      LogUtil.debug('SKUsComponent filterSkus no filter');
    }

    if (this.filteredSkus === null) {
      this.filteredSkus = [];
    }

    var _sort = function(a:ProductSkuVO, b:ProductSkuVO):number {
      return a.rank - b.rank;
    };

    this.filteredSkus.sort(_sort);

    let _total = this.filteredSkus.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
