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
import {ShopSupportedCurrenciesVO} from './../../model/shop';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {DataControl} from '../common/data_control';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {ShopEventBus} from '../../service/shop_event_bus';
import {AppCmp} from '../../app/components/app';

@Component({
  selector: 'shop-currency',
  moduleId: module.id,
  templateUrl: './shop_currency.html',
  styleUrls: ['./shop_currency.css'],
  directives: [DataControl],
  providers: [HTTP_PROVIDERS, ShopService, ShopEventBus]
})

export class ShopCurrency implements OnInit {

  shopSupportedCurrenciesVO:ShopSupportedCurrenciesVO;
  curr:ShopSupportedCurrenciesVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop currencies');
  }

  ngOnInit() {
    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);

    this._shopService.getShopCurrencies(+shopId).subscribe(shopSupportedCurrenciesVO => {
      this.shopSupportedCurrenciesVO = shopSupportedCurrenciesVO;
      this.changed = false;
      this.curr  = shopSupportedCurrenciesVO;
      //TODO create copy of object to filer out supported from available
    });
  }

  onDataChange() {
    console.debug('data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('Save handler for shop id ');

  }

  onDiscardEvent() {
    console.debug('Discard hander for shop id ' );

  }

  onRefreshHandler() {
    console.debug('Refresh handler');
    this.onDiscardEvent();
  }

}
