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
import { CarrierVO, ShopVO } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-carriers',
  moduleId: module.id,
  templateUrl: 'carriers.component.html',
})

export class CarriersComponent implements OnInit, OnDestroy {

  @Input() selectedCarrier:CarrierVO;

  @Output() dataSelected: EventEmitter<CarrierVO> = new EventEmitter<CarrierVO>();

  private _carriers:Array<CarrierVO> = [];
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private _shops:any = {};

  private filteredCarriers:Array<CarrierVO>;

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
    LogUtil.debug('CarriersComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCarriers();
    }, this.delayedFilteringMs);

  }

  ngOnInit() {
    LogUtil.debug('CarriersComponent ngOnInit');
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    shops.forEach(shop => {
      this._shops['S' + shop.shopId] = shop;
    });
    LogUtil.debug('CarrierComponent mapped shops', this._shops);
  }

  @Input()
  set carriers(carriers:Array<CarrierVO>) {
    this._carriers = carriers;
    this.filterCarriers();
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnDestroy() {
    LogUtil.debug('CarriersComponent ngOnDestroy');
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
    LogUtil.debug('CarriersComponent onSelectRow handler', row);
    if (row == this.selectedCarrier) {
      this.selectedCarrier = null;
    } else {
      this.selectedCarrier = row;
    }
    this.dataSelected.emit(this.selectedCarrier);
  }

  protected getShopNames(row:CarrierVO):CarrierShop[] {
    let shops:CarrierShop[] = [];
    if (row.carrierShops != null && row.carrierShops.length > 0) {
      row.carrierShops.forEach( carrierShop => {
        let key = 'S' + carrierShop.shopId;
        if (this._shops.hasOwnProperty(key)) {
          let shop:ShopVO = this._shops[key];
          shops.push({ code: shop.code, name: shop.name, active: !shop.disabled && !carrierShop.disabled });
        }
      });
    } else {
      shops.push({ code: '-', name: '-', active: false });
    }
    return shops;
  }

  private filterCarriers() {
    if (this._filter) {
      this.filteredCarriers = this._carriers.filter(carrier =>
        carrier.name.toLowerCase().indexOf(this._filter) !== -1 ||
        carrier.description && carrier.description.toLowerCase().indexOf(this._filter) !== -1
      );
      LogUtil.debug('CarriersComponent filterCarriers', this._filter);
    } else {
      this.filteredCarriers = this._carriers;
      LogUtil.debug('CarriersComponent filterCarriers no filter');
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

}

interface CarrierShop {

  code: string;
  name: string;
  active: boolean;

}
