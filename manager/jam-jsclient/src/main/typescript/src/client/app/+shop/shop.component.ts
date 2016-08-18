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
import {Component, OnInit, OnDestroy, AfterContentInit} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ShopVO} from './../shared/model/index';
import {Router, ActivatedRoute} from '@angular/router';
import {ShopEventBus, ShopService} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import {
  ShopMainComponent,
  ShopI18nComponent,
  ShopCurrencyComponent,
  ShopLanguageComponent,
  ShopUrlComponent,
  ShopCatalogComponent,
  ShopLocationComponent,
  ShopCarrierComponent,
  ShopAttributesComponent,
  ShopPaymentGatewaysComponent
} from './components/index';

@Component({
  selector: 'yc-shop',
  moduleId: module.id,
  templateUrl: 'shop.component.html',
  directives: [
    TAB_DIRECTIVES, NgIf,
    ShopMainComponent,
    ShopI18nComponent,
    ShopCurrencyComponent,
    ShopLanguageComponent,
    ShopUrlComponent,
    ShopCatalogComponent,
    ShopLocationComponent,
    ShopCarrierComponent,
    ShopAttributesComponent,
    ShopPaymentGatewaysComponent
  ],
})

export class ShopComponent implements OnInit, OnDestroy, AfterContentInit {

  private shop:ShopVO;

  private shopIdSub:any;
  private shopSub:any;

  private reloadCatalogue:boolean = false;
  private reloadAttributes:boolean = false;
  private reloadI18n:boolean = false;
  private reloadUrls:boolean = false;
  private reloadCarriers:boolean = false;
  private reloadCurrency:boolean = false;
  private reloadLanguage:boolean = false;
  private reloadLocations:boolean = false;
  private reloadPGs:boolean = false;

  constructor(private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    console.debug('ShopComponent constructed');
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
    });
  }

  ngOnInit() {
    this.shopIdSub = this._route.params.subscribe(params => {
      let shopId = params['shopId'];
      console.debug('ShopComponent shopId from params is ' + shopId);
      if (shopId.indexOf('new_') != -1) {
        this._shopService.createShop().then(shop => {
          console.debug('ShopComponent Creating new shop', shop);
          ShopEventBus.getShopEventBus().emit(shop);
        });
      } else {
        var _sub:any = this._shopService.getShop(+shopId).subscribe(shop => {
          console.debug('ShopComponent Retrieving existing shop', shop);
          ShopEventBus.getShopEventBus().emit(shop);
          _sub.unsubscribe();
        });
      }
    });

  }

  ngOnDestroy() {
    console.debug('ShopComponent ngOnDestroy');
    if (this.shopIdSub) {
      this.shopIdSub.unsubscribe();
    }
    this.shopSub.unsubscribe();
  }

  ngAfterContentInit() {
    console.debug('ShopComponent ngAfterContentInit');
  }

  tabSelected(tab:any) {
    console.debug('ShopComponent tabSelected', tab);

    this.reloadCatalogue = tab === 'Catalogue';
    this.reloadAttributes = tab === 'Attributes';
    this.reloadI18n = tab === 'I18n';
    this.reloadUrls = tab === 'Urls';
    this.reloadCarriers = tab === 'Carriers';
    this.reloadCurrency = tab === 'Currency';
    this.reloadLanguage = tab === 'Language';
    this.reloadLocations = tab === 'Locations';
    this.reloadPGs = tab === 'PGs';

  }

}
