/*
 * Copyright 2009 Inspire-Software.com
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
import { ShopEventBus, UserEventBus, ShopService } from './../shared/services/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-sub-shop',
  templateUrl: 'sub-shop.component.html',
})

export class SubShopComponent implements OnInit, OnDestroy {

  private shopId:string = null;
  public shop:ShopVO;
  public addressShops:ShopVO[];
  public shopCustomer:CustomerInfoVO;

  private shopSub:any;
  private userSub:any;

  public reloadCatalogue:boolean = false;
  public reloadAttributes:boolean = false;
  public reloadSEO:boolean = false;
  public reloadUrls:boolean = false;
  public reloadAliases:boolean = false;
  public reloadCarriers:boolean = false;
  public reloadWarehouse:boolean = false;
  public reloadCurrency:boolean = false;
  public reloadLanguage:boolean = false;
  public reloadLocations:boolean = false;
  public reloadPGs:boolean = false;
  public reloadSummary:boolean = true;
  public reloadAddressbook:boolean = false;

  constructor(private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    LogUtil.debug('SubShopComponent constructed');
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
      if (this.shop != null) {
        this.addressShops = [ this.shop ];
        this.shopCustomer = null;
      } else {
        this.addressShops = [];
        this.shopCustomer = null;
      }
    });
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(userevt => {
      if (userevt != null) {
        LogUtil.debug('SubShopComponent new user event, retrieving shop by shopId', userevt, this.shopId);
        this.emitShopByIdIfNecessary();
      }
    });
  }

  ngOnInit() {
    this._route.params.subscribe(params => {
      this.shopId = params['shopId'];
      this.emitShopByIdIfNecessary();
    });
  }

  ngOnDestroy() {
    LogUtil.debug('SubShopComponent ngOnDestroy');
    this.shopSub.unsubscribe();
    this.userSub.unsubscribe();
  }

  tabSelected(tab:any) {
    LogUtil.debug('SubShopComponent tabSelected', tab);

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
    this.reloadAddressbook = tab === 'Addressbook';

  }


  private emitShopByIdIfNecessary():void {
    LogUtil.debug('SubShopComponent getShopByIdIfNecessary', this.shopId);

    if (this.shopId != null  && (this.shop == null || this.shop.shopId != +this.shopId)) {
      LogUtil.debug('SubShopComponent shopId from params is ' + this.shopId);
      this._shopService.getShop(+this.shopId).subscribe(shop => {
        LogUtil.debug('SubShopComponent Retrieving existing shop', shop);
        ShopEventBus.getShopEventBus().emit(shop);
      });
    }
  }

}
