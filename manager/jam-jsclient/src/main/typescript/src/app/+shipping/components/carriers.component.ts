/*
 * Copyright 2009 Inspire-Software.com
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
import { CarrierInfoVO, ShopVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-carriers',
  templateUrl: 'carriers.component.html',
})

export class CarriersComponent implements OnInit, OnDestroy {

  @Input() selectedCarrier:CarrierInfoVO;

  @Output() dataSelected: EventEmitter<CarrierInfoVO> = new EventEmitter<CarrierInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _carriers:SearchResultVO<CarrierInfoVO> = null;

  private _shops:any = {};

  public filteredCarriers:Array<CarrierInfoVO>;

  //sorting
  public sortColumn:string = null;
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;

  constructor() {
    LogUtil.debug('CarriersComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CarriersComponent ngOnInit');
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('CarrierComponent mapped shops', this._shops);
  }

  @Input()
  set carriers(carriers:SearchResultVO<CarrierInfoVO>) {
    this._carriers = carriers;
    this.filterCarriers();
  }

  ngOnDestroy() {
    LogUtil.debug('CarriersComponent ngOnDestroy');
    this.selectedCarrier = null;
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

  onSelectRow(row:CarrierInfoVO) {
    LogUtil.debug('CarriersComponent onSelectRow handler', row);
    if (row == this.selectedCarrier) {
      this.selectedCarrier = null;
    } else {
      this.selectedCarrier = row;
    }
    this.dataSelected.emit(this.selectedCarrier);
  }


  getShopNames(row:CarrierInfoVO):CarrierShop[] {
    let shops:CarrierShop[] = [];
    if (row.carrierShops != null && row.carrierShops.length > 0) {
      row.carrierShops.forEach( carrierShop => {
        let key = 'S' + carrierShop.shopId;
        if (this._shops.hasOwnProperty(key)) {
          let shop:ShopVO = this._shops[key];
          shops.push({ code: (shop.masterCode ? shop.masterCode + ': ' : '') + shop.code, name: shop.name, active: !shop.disabled && !carrierShop.disabled });
        }
      });
    }

    shops.sort((a, b) => {
      return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
    });

    return shops;
  }

  private filterCarriers() {

    LogUtil.debug('CarriersComponent filterCarriers', this.filteredCarriers);

    if (this._carriers != null) {

      this.filteredCarriers = this._carriers.items != null ? this._carriers.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._carriers.searchContext.size;
      this.totalItems = this._carriers.total;
      this.currentPage = this._carriers.searchContext.start + 1;
      this.sortColumn = this._carriers.searchContext.sortBy;
      this.sortDesc = this._carriers.searchContext.sortDesc;
    } else {
      this.filteredCarriers = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}

interface CarrierShop {

  code: string;
  name: string;
  active: boolean;

}
