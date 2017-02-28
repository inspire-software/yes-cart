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
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ShopVO, CustomerInfoVO } from './../shared/model/index';
import { Router, ActivatedRoute } from '@angular/router';
import { ShopEventBus, ShopService } from './../shared/services/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-shop',
  moduleId: module.id,
  templateUrl: 'shop.component.html',
})

export class ShopComponent implements OnInit, OnDestroy {

  private shop:ShopVO;
  private addressShops:ShopVO[];
  private shopCustomer:CustomerInfoVO;

  private shopIdSub:any;
  private shopSub:any;

  private reloadCatalogue:boolean = false;
  private reloadAttributes:boolean = false;
  private reloadSEO:boolean = false;
  private reloadUrls:boolean = false;
  private reloadAliases:boolean = false;
  private reloadCarriers:boolean = false;
  private reloadWarehouse:boolean = false;
  private reloadCurrency:boolean = false;
  private reloadLanguage:boolean = false;
  private reloadLocations:boolean = false;
  private reloadPGs:boolean = false;
  private reloadSummary:boolean = true;
  private reloadB2b:boolean = false;
  private reloadAddressbook:boolean = false;

  constructor(private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    LogUtil.debug('ShopComponent constructed');
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
      if (this.shop != null) {
        this.addressShops = [ this.shop ];
        this.shopCustomer = null; // YCE
      } else {
        this.addressShops = [];
        this.shopCustomer = null;
      }
    });
  }

  ngOnInit() {
    this.shopIdSub = this._route.params.subscribe(params => {
      let shopId = params['shopId'];
      LogUtil.debug('ShopComponent shopId from params is ' + shopId);
      if (shopId.indexOf('new_') != -1) {
        this._shopService.createShop().then(shop => {
          LogUtil.debug('ShopComponent Creating new shop', shop);
          ShopEventBus.getShopEventBus().emit(shop);
        });
      } else {
        var _sub:any = this._shopService.getShop(+shopId).subscribe(shop => {
          LogUtil.debug('ShopComponent Retrieving existing shop', shop);
          ShopEventBus.getShopEventBus().emit(shop);
          _sub.unsubscribe();
        });
      }
    });
  }

  ngOnDestroy() {
    LogUtil.debug('ShopComponent ngOnDestroy');
    if (this.shopIdSub) {
      this.shopIdSub.unsubscribe();
    }
    this.shopSub.unsubscribe();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ShopComponent tabSelected', tab);

    this.reloadCatalogue = tab === 'Catalogue';
    this.reloadAttributes = tab === 'Attributes';
    this.reloadSEO = tab === 'SEO';
    this.reloadUrls = tab === 'Urls';
    this.reloadAliases = tab === 'Aliases';
    this.reloadCarriers = tab === 'Carriers';
    this.reloadWarehouse = tab === 'Warehouse';
    this.reloadCurrency = tab === 'Currency';
    this.reloadLanguage = tab === 'Language';
    this.reloadLocations = tab === 'Locations';
    this.reloadPGs = tab === 'PGs';
    this.reloadSummary = tab === 'Summary';
    this.reloadB2b = tab === 'B2B';
    this.reloadAddressbook = tab === 'Addressbook';

  }

}
