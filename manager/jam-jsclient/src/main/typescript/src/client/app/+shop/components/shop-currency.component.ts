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
import { Component, OnInit, Input } from '@angular/core';
import { ShopVO, ShopSupportedCurrenciesVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-currency',
  moduleId: module.id,
  templateUrl: 'shop-currency.component.html',
})

export class ShopCurrencyComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopSupportedCurrenciesVO:ShopSupportedCurrenciesVO;
  private curr:ShopSupportedCurrenciesVO;

  private changed:boolean = false;

  private loading:boolean = false;

  constructor(private _shopService:ShopService) {
    LogUtil.debug('ShopCurrencyComponent constructed');
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.onRefreshHandler();
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._reload || this.shopSupportedCurrenciesVO != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopCurrencyComponent ngOnInit shop', this.shop);
  }


  onDataChange() {
    LogUtil.debug('ShopCurrencyComponent data changed');
    this.changed = true;
  }

  onSaveHandler() {
    LogUtil.debug('ShopCurrencyComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.curr) {
      var _sub:any = this._shopService.saveShopCurrencies(this.curr).subscribe(shopSupportedCurrenciesVO => {
        LogUtil.debug('ShopCurrencyComponent Saved currencies', shopSupportedCurrenciesVO);
        this.shopSupportedCurrenciesVO = Util.clone(shopSupportedCurrenciesVO);
        this.curr = Util.clone(shopSupportedCurrenciesVO);
        Util.remove(this.curr.all, this.curr.supported);
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopCurrencyComponent discard handler', this.shop);
    if (this.shop.shopId > 0 && this.shopSupportedCurrenciesVO) {
      this.curr = Util.clone(this.shopSupportedCurrenciesVO);
      Util.remove(this.curr.all, this.curr.supported);
      this.changed = false;
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ShopCurrencyComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      var _sub:any = this._shopService.getShopCurrencies(this.shop.shopId).subscribe(shopSupportedCurrenciesVO => {
        this.shopSupportedCurrenciesVO = Util.clone(shopSupportedCurrenciesVO);
        this.curr = Util.clone(shopSupportedCurrenciesVO);
        Util.remove(this.curr.all, this.curr.supported);
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.curr = null;
    }
  }

  onAvailableCurrencyClick(event:any) {
    LogUtil.debug('ShopCurrencyComponent onAvailableCurrencyClick', event);
    this.curr.supported.push(event);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

  onSupportedCurrencyClick(event:any) {
    LogUtil.debug('ShopCurrencyComponent onSupportedCurrencyClick', event);
    this.curr.supported = this.curr.supported.filter( obj => {return obj !== event;});
    this.curr.all = Util.clone(this.shopSupportedCurrenciesVO.all);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

}
