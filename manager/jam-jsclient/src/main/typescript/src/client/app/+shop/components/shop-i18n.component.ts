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
import {NgIf} from '@angular/common';
import {ShopVO, ShopLocaleVO} from './../../shared/model/index';
import {ShopService, ShopEventBus} from './../../shared/services/index';
import {I18nComponent} from './../../shared/i18n/index';
import {DataControlComponent} from './../../shared/sidebar/index';

@Component({
  selector: 'yc-shop-i18n',
  moduleId: module.id,
  templateUrl: 'shop-i18n.component.html',
  directives: [ I18nComponent, NgIf, DataControlComponent],
})

export class ShopI18nComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopLocalization:ShopLocaleVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService) {
    console.debug('ShopI18nComponent constructed');
  }

  ngOnInit() {
    console.debug('ShopI18nComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopI18nComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  onDataChanged() {
    console.debug('ShopI18nComponent data change');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('ShopI18nComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLocalization) {
      var _sub:any = this._shopService.saveShopLocalization(this.shopLocalization).subscribe(shopLocalization => {
        console.debug('ShopI18nComponent Saved i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEvent() {
    console.debug('ShopI18nComponent Discard handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShopLocalization(this.shop.shopId).subscribe(shopLocalization => {
        console.debug('ShopI18nComponent Refreshed i18n', shopLocalization);
        this.shopLocalization = shopLocalization;
        this.changed = false;
        _sub.unsubscribe();
      });
    } else {
      this.shopLocalization = null;
    }
  }

  onRefreshHandler() {
    console.debug('ShopI18nComponent Refresh handler');
    this.onDiscardEvent();
  }

}
