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
import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ShopVO, ShopSummaryVO, Pair } from './../../shared/model/index';
import { ShopService, I18nEventBus } from './../../shared/services/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-summary',
  moduleId: module.id,
  templateUrl: 'shop-summary.component.html',
})

export class ShopSummaryComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopSummary:ShopSummaryVO;

  private loading:boolean = false;

  private misconfigured:boolean = false;

  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopSummaryComponent constructed');
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
    if (this._reload || this.shopSummary != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopSummaryComponent ngOnInit shop', this.shop);
  }

  ngOnDestroy() {
    LogUtil.debug('ShopSummaryComponent ngOnDestroy');
  }

  onSaveHandler() {
    LogUtil.debug('ShopSummaryComponent Save handler', this.shop);
  }

  onDiscardEvent() {
    LogUtil.debug('ShopSummaryComponent Discard handler for shop', this.shop);
    if (this.shop && this.shop.shopId > 0) {
      this.loading = true;
      let lang = I18nEventBus.getI18nEventBus().current();
      var _sub:any = this._shopService.getShopSummary(this.shop.shopId, lang).subscribe(shopSummary => {
        this.shopSummary = shopSummary;
        this._reload = false;
        _sub.unsubscribe();
        this.loading = false;

        this.misconfigured =
          shopSummary.primaryUrlAndThemeChain == null ||
          shopSummary.currencies.length == 0 ||
          shopSummary.locales.length == 0 ||
          shopSummary.billingLocations.length == 0 ||
          shopSummary.shippingLocations.length == 0 ||
          shopSummary.carriers.length == 0 ||
          shopSummary.fulfilmentCentres.length == 0 ||
          shopSummary.paymentGateways.length == 0 ||
          shopSummary.customerTypes.length == 0;

        LogUtil.debug('ShopSummaryComponent Refreshed', shopSummary, this.misconfigured);
      });
    } else {
      this.shopSummary = null;
      this.misconfigured = false;
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ShopSummaryComponent Refresh handler');
    this.onDiscardEvent();
  }

  isTypeEnabled(customerType:string, types:string[]):boolean {
    let enabled = types.includes(customerType);
    return enabled;
  }

  getEmailConfigString(emailTemplate:string, strConfig:Pair<string, string>[]):string {
    let _out:string = null;
    strConfig.forEach(_str => {
       if (_str.first == emailTemplate) {
         _out = _str.second;
       }
    });
    return _out;
  }

  isEmailConfigOn(emailTemplate:string, boolConfig:Pair<string, boolean>[]):boolean {
    let _out:boolean = false;
    boolConfig.forEach(_bool => {
      if (_bool.first == emailTemplate) {
        _out = _bool.second;
      }
    });
    return _out;
  }

  isI18nConfigOn(lang:string, overrides:Pair<string, boolean>[]):boolean {
    let _out:boolean = false;
    overrides.forEach(_bool => {
      if (_bool.first == lang) {
        _out = _bool.second;
      }
    });
    return _out;
  }

}
