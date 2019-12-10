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
import { CustomerInfoVO, Pair, SearchResultVO, ShopVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-customers',
  moduleId: module.id,
  templateUrl: 'customers.component.html',
})

export class CustomersComponent implements OnInit, OnDestroy {

  @Input() selectedCustomer:CustomerInfoVO;

  @Output() dataSelected: EventEmitter<CustomerInfoVO> = new EventEmitter<CustomerInfoVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _customers:SearchResultVO<CustomerInfoVO> = null;

  private _shops:any = {};

  private filteredCustomers:Array<CustomerInfoVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('CustomersComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CustomersComponent ngOnInit');
  }

  @Input()
  set customers(customers:SearchResultVO<CustomerInfoVO>) {
    this._customers = customers;
    this.filterCustomers();
  }

  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('CustomersComponent mapped shops', this._shops);
  }

  ngOnDestroy() {
    LogUtil.debug('CustomersComponent ngOnDestroy');
    this.selectedCustomer = null;
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

  protected onSelectRow(row:CustomerInfoVO) {
    LogUtil.debug('CustomersComponent onSelectRow handler', row);
    if (row == this.selectedCustomer) {
      this.selectedCustomer = null;
    } else {
      this.selectedCustomer = row;
    }
    this.dataSelected.emit(this.selectedCustomer);
  }


  protected getPolicyLabels(row:CustomerInfoVO) {
    let out = '';
    if (row.customerType) {
      if (row.pricingPolicy) {
        out += ' <span class="label label-primary">' + row.customerType + ':' + row.pricingPolicy + '</span> ';
      } else {
        out += ' <span class="label label-primary">' + row.customerType + ':*</span> ';
      }
    } else if (row.pricingPolicy) {
      out += ' <span class="label label-primary">B2C:' + row.pricingPolicy + '</span> ';
    }
    return out;
  }

  protected getShopNames(row:CustomerInfoVO):CustomerShop[] {
    let shops:CustomerShop[] = [];
    if (row.customerShops != null && row.customerShops.length > 0) {
      row.customerShops.forEach( customerShop => {
        let key = 'S' + customerShop.shopId;
        if (this._shops.hasOwnProperty(key)) {
          let shop:ShopVO = this._shops[key];
          shops.push({ code: shop.code, name: shop.name, active: !shop.disabled && !customerShop.disabled });
        }
      });
    }

    shops.sort((a, b) => {
      return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
    });

    return shops;
  }

  private filterCustomers() {

    LogUtil.debug('CustomersComponent filterCustomers', this._customers, this.filteredCustomers);

    if (this._customers != null) {

      this.filteredCustomers = this._customers.items != null ? this._customers.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._customers.searchContext.size;
      this.totalItems = this._customers.total;
      this.currentPage = this._customers.searchContext.start + 1;
      this.sortColumn = this._customers.searchContext.sortBy;
      this.sortDesc = this._customers.searchContext.sortDesc;
    } else {
      this.filteredCustomers = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}


interface CustomerShop {

  code: string;
  name: string;
  active: boolean;

}

