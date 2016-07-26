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
import {Component, OnInit, OnDestroy, OnChanges, Input} from '@angular/core';
import {NgIf, NgFor} from '@angular/common';
import {ShopVO, ShopSupportedCurrenciesVO} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';

@Component({
  selector: 'yc-shop-currency',
  moduleId: module.id,
  templateUrl: 'shop-currency.component.html',
  directives: [ NgIf, NgFor, DataControlComponent],
})

export class ShopCurrencyComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopSupportedCurrenciesVO:ShopSupportedCurrenciesVO;
  curr:ShopSupportedCurrenciesVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService) {
    console.debug('ShopCurrencyComponent constructed');
  }

  ngOnInit() {
    console.debug('ShopCurrencyComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopCurrencyComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }


  onDataChange() {
    console.debug('ShopCurrencyComponent data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('ShopCurrencyComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.curr) {
      var _sub:any = this._shopService.saveShopCurrencies(this.curr).subscribe(shopSupportedCurrenciesVO => {
        console.debug('ShopCurrencyComponent Saved currencies', shopSupportedCurrenciesVO);
        this.shopSupportedCurrenciesVO = Util.clone(shopSupportedCurrenciesVO);
        this.curr = Util.clone(shopSupportedCurrenciesVO);
        Util.remove(this.curr.all, this.curr.supported);
        this.changed = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    console.debug('ShopCurrencyComponent discard handler', this.shop);
    if (this.shop.shopId > 0 && this.shopSupportedCurrenciesVO) {
      this.curr = Util.clone(this.shopSupportedCurrenciesVO);
      Util.remove(this.curr.all, this.curr.supported);
      this.changed = false;
    }
  }

  onRefreshHandler() {
    console.debug('ShopCurrencyComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShopCurrencies(this.shop.shopId).subscribe(shopSupportedCurrenciesVO => {
        this.shopSupportedCurrenciesVO = Util.clone(shopSupportedCurrenciesVO);
        this.curr = Util.clone(shopSupportedCurrenciesVO);
        Util.remove(this.curr.all, this.curr.supported);
        this.changed = false;
        _sub.unsubscribe();
      });
    } else {
      this.curr = null;
    }
  }

  onAvailableCurrencyClick(event:any) {
    console.debug('ShopCurrencyComponent onAvailableCurrencyClick', event);
    this.curr.supported.push(event);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

  onSupportedCurrencyClick(event:any) {
    console.debug('ShopCurrencyComponent onSupportedCurrencyClick', event);
    this.curr.supported = this.curr.supported.filter( obj => {return obj !== event;});
    this.curr.all = Util.clone(this.shopSupportedCurrenciesVO.all);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

}
