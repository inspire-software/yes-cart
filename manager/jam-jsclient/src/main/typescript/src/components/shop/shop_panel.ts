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
import {Component} from 'angular2/core';
import {ShopVO} from './../../model/shop';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {Util} from '../../service/util';
import {DataControl} from '../common/data_control';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {ShopEventBus} from '../../service/shop_event_bus';
import {AppCmp} from '../../app/components/app';

@Component({
  selector: 'shop-panel',
  moduleId: module.id,
  templateUrl: './shop_panel.html',
  styleUrls: ['./shop_panel.css'],
  directives: [DataControl],
  providers: [HTTP_PROVIDERS, ShopService, ShopEventBus]
})

export class ShopPanel implements OnInit {

  shop:ShopVO;
  shopDisabled:boolean = false;

  changed:boolean = false;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop panel constructed');
    AppCmp.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = Util.clone(shopevt);
      this.changed = false;
      this.shopDisabled = shopevt.disabled;
    });
  }

  ngOnInit() {
    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);

    this.onDiscardEvent();

  }

  onDataChange() {
    console.debug('data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('Save handler for shop id', this.shop);
    this._shopService.saveShop(this.shop).subscribe(shop => {
      console.debug('Shop service save', shop);
      AppCmp.getShopEventBus().emit(shop);
    });
  }

  onPowerOff() {
    console.debug('Power off handler for shop', this.shop);
    this._shopService.updateDisabledFlag(this.shop, !this.shop.disabled).subscribe(shop => {
      console.debug('Shop service power off', shop);
      AppCmp.getShopEventBus().emit(shop);
    });
  }

  onDiscardEvent() {
    console.debug('Discard handler for shop', this.shop);
    this.shop = Util.clone(AppCmp.getShopEventBus().current());
    this.shopDisabled = this.shop.disabled;
    this.changed = false;
  }

  onRefreshHandler() {
    console.debug('Refresh handler', this.shop);
    this._shopService.getShop(this.shop.shopId).subscribe(shop => {
      AppCmp.getShopEventBus().emit(shop);
    });
  }

}
