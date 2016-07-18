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
import {Component, OnInit, AfterContentInit} from 'angular2/core';
import {ShopVO} from './../../model/shop';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {Tab, TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ShopPanel} from './shop_panel';
import {ShopUrlPanel} from './shop_url_panel';
import {ShopCurrency} from './shop_currency';
import {ShopLanguage} from './shop_language';
import {ShopCatalogue} from './shop_catalogue';
import {ShopLocalizationPanel} from './shop_localization_panel';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {ShopEventBus} from '../../service/shop_event_bus';
import {AppCmp} from '../../app/components/app';


@Component({
  selector: 'shop',
  moduleId: module.id,
  templateUrl: './shop_page.html',
  styleUrls: ['./shop_page.css'],
  directives: [TAB_DIRECTIVES, ShopPanel, ShopLocalizationPanel, ShopUrlPanel, ShopCurrency, ShopLanguage, ShopCatalogue],
  providers: [HTTP_PROVIDERS, ShopService, ShopEventBus]
})

export class ShopPage implements OnInit, AfterContentInit {
  shop:ShopVO;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop page constructed');
    AppCmp.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
    });
  }

  ngOnInit() {
    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);
    if (shopId === 'new') {
      this._shopService.createShop().then(shop => {
        console.debug('Creating new shop', shop);
        AppCmp.getShopEventBus().emit(shop);
      });
    } else {
      this._shopService.getShop(+shopId).subscribe(shop => {
        console.debug('Retrieving existing shop', shop);
        AppCmp.getShopEventBus().emit(shop);
      });
    }
  }

  ngAfterContentInit() {
    console.debug('ngAfterContentInit');
  }

  tabSelected(tab:Tab) {
    console.debug('tabSelected ' + tab);
  }

}
