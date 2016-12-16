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
import { ShopVO } from './../../shared/model/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-payment-gateways',
  moduleId: module.id,
  templateUrl: 'shop-payment-gateways.component.html',
})

export class ShopPaymentGatewaysComponent implements OnInit {

  private _shop:ShopVO;
  private shopCode:string = null;
  private _reload:boolean = false;

  /**
   * Construct shop url panel
   *
   * @param _shopService shop service
   */
  constructor() {
    LogUtil.debug('ShopPaymentGatewaysComponent constructed');
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      if (this._shop != null && this._shop.shopId > 0 && (this._reload || this.shopCode != null)) {
        this.shopCode = this._shop.code;
        this._reload = false;
      } else {
        this.shopCode = null;
        this._reload = false;
      }
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._shop != null && this._shop.shopId > 0 && (this._reload || this.shopCode != null)) {
      this.shopCode = this._shop.code;
    } else {
      this.shopCode = null;
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ShopPaymentGatewaysComponent ngOnInit shop', this.shop);
  }

}
