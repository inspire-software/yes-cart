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
import {Component,  OnInit, OnDestroy} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {ShopVO} from '../model/index';
import {ShopEventBus, ShopService} from '../services/index';



@Component({
  selector: 'yc-shop-list',
  moduleId: module.id,
  templateUrl: 'shop-list.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class ShopListComponent implements OnInit, OnDestroy {

  private shops : ShopVO[];
  private selectedShop : ShopVO;

  private shopSub:any;

  constructor (private _shopService : ShopService) {
    console.debug('ShopListComponent constructed selectedShop ' + this.selectedShop);
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.reloadShopList(shopevt);
    });
  }

  getAllShops() {
    var _sub:any = this._shopService.getAllShops().subscribe( allshops => {
      console.debug('ShopListComponent getAllShops', allshops);
      this.shops = allshops;
      _sub.unsubscribe();
    });
  }

  ngOnDestroy() {
    console.debug('ShopListComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
  }

  ngOnInit() {
    console.debug('ShopListComponent ngOnInit');
    this.getAllShops();
  }

  onSelect(shop: ShopVO) {
    console.debug('ShopListComponent onSelect', shop);
    this.selectedShop = shop;
  }

  /**
   * Reload list of shops
   * @param shopVo shop that was changed or added
   */
  reloadShopList(shopVo : ShopVO) {
   this.getAllShops();
  }

  isShopDisabled(shop: ShopVO):boolean {
    return shop.disabled;
  }

}
