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
import { FulfilmentCentreVO, ShopVO, Pair, SearchResultVO } from './../../shared/model/index';
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

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _centres:SearchResultVO<FulfilmentCentreVO> = null;

  private _shops:any = {};

  private filteredCentres:Array<FulfilmentCentreVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('FulfilmentCentresComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('FulfilmentCentresComponent ngOnInit');
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('FulfilmentCentresComponent mapped shops', this._shops);
  }

  @Input()
  set centres(centres:SearchResultVO<FulfilmentCentreVO>) {
    this._centres = centres;
    this.filterCentres();
  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentCentresComponent ngOnDestroy');
    this.selectedCentre = null;
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

  protected onSelectRow(row:FulfilmentCentreVO) {
    LogUtil.debug('FulfilmentCentresComponent onSelectRow handler', row);
    if (row == this.selectedCentre) {
      this.selectedCentre = null;
    } else {
      this.selectedCentre = row;
    }
    this.dataSelected.emit(this.selectedCentre);
  }

  protected getShopNames(row:FulfilmentCentreVO):CentreShop[] {
    let shops:CentreShop[] = [];
    if (row.fulfilmentShops != null && row.fulfilmentShops.length > 0) {
      row.fulfilmentShops.forEach( fulfilmentShop => {
        let key = 'S' + fulfilmentShop.shopId;
        if (this._shops.hasOwnProperty(key)) {
          let shop:ShopVO = this._shops[key];
          shops.push({ code: (shop.masterCode ? shop.masterCode + ': ' : '') + shop.code, name: shop.name, active: !shop.disabled && !fulfilmentShop.disabled });
        }
      });
    }

    shops.sort((a, b) => {
      return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
    });

    return shops;
  }


  protected getFfFlags(row:FulfilmentCentreVO) {

    let flags = '';
    flags += '<i class="fa fa-truck"></i>&nbsp;' + row.defaultStandardStockLeadTime + '/' + row.defaultBackorderStockLeadTime + '&nbsp;';
    if (row.multipleShippingSupported) {
      flags += '<i class="fa fa-object-ungroup"></i>&nbsp;';
    } else {
      flags += '<i class="fa fa-object-group"></i>&nbsp;';
    }
    return flags;
  }

  private filterCentres() {

    LogUtil.debug('FulfilmentCentresComponent filterBrands', this.filteredCentres);

    if (this._centres != null) {

      this.filteredCentres = this._centres.items != null ? this._centres.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._centres.searchContext.size;
      this.totalItems = this._centres.total;
      this.currentPage = this._centres.searchContext.start + 1;
      this.sortColumn = this._centres.searchContext.sortBy;
      this.sortDesc = this._centres.searchContext.sortDesc;
    } else {
      this.filteredCentres = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}

interface CentreShop {

  code: string;
  name: string;
  active: boolean;

}

