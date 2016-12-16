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
import { Component,  OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ShopVO } from './../model/index';
import { ShopEventBus, ShopService } from './../services/index';
import { Futures, Future } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-shop-select',
  moduleId: module.id,
  templateUrl: 'shop-select.component.html',
})

export class ShopSelectComponent implements OnInit, OnDestroy {

  @Input() showNewLink: boolean = true;

  @Output() dataNew: EventEmitter<ShopVO> = new EventEmitter<ShopVO>();
  @Output() dataSelected: EventEmitter<ShopVO> = new EventEmitter<ShopVO>();

  private shops : ShopVO[] = null;
  private filteredShops : ShopVO[] = [];
  private shopFilter : string;

  private selectedShop : ShopVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private shopSub:any;
  private shopAllSub:any;

  constructor (private _shopService : ShopService) {
    LogUtil.debug('ShopListComponent constructed selectedShop ', this.selectedShop);
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.reloadShopList(shopevt);
    });
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
      this.reloadShopList(null);
    });
  }

  ngOnDestroy() {
    LogUtil.debug('ShopListComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }

  ngOnInit() {
    LogUtil.debug('ShopListComponent ngOnInit');
    if (this.shops == null) {
      this.getAllShops();
    }
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.reloadShopList(null);
    }, this.delayedFilteringMs);

  }

  onNewClick() {
    LogUtil.debug('ShopListComponent onNewClick');
    this.dataNew.emit(null);
  }

  onSelectClick(shop: ShopVO) {
    LogUtil.debug('ShopListComponent onSelectClick', shop);
    this.selectedShop = shop;
    this.dataSelected.emit(this.selectedShop);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  /**
   * Reload list of shops
   * @param shopVo shop that was changed or added
   */
  private reloadShopList(shopVo : ShopVO) {

    if (this.shops != null) {

      if (shopVo != null) {
        let idx = this.shops.findIndex(shop => shop.shopId == shopVo.shopId);
        if (idx != -1) {
          this.shops[idx] = shopVo;
        } else {
          this.shops.push(shopVo);
        }
        ShopEventBus.getShopEventBus().emitAll(this.shops);
      }

      this.shops.sort((a, b) => {
        return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
      });

      if (this.shopFilter) {
        let _filter = this.shopFilter.toLowerCase();
        this.filteredShops = this.shops.filter(shop =>
          shop.code.toLowerCase().indexOf(_filter) != -1 ||
          shop.name && shop.name.toLowerCase().indexOf(_filter) != -1
        );
        LogUtil.debug('ShopListComponent reloadShopList filter: ' + _filter, this.filteredShops);
      } else {
        this.filteredShops = this.shops;
        LogUtil.debug('ShopListComponent reloadShopList no filter', this.filteredShops);
      }
    }

    // this.getAllShops(); - no need to REST, we keep full list in the event bus
  }

  private getAllShops() {
    var _sub:any = this._shopService.getAllShops().subscribe( allshops => {
      LogUtil.debug('ShopListComponent getAllShops', allshops);
      this.shops = allshops;
      ShopEventBus.getShopEventBus().emitAll(this.shops);
      this.reloadShopList(null);
      _sub.unsubscribe();
    });
  }

}
