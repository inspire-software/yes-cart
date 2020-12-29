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
import { Component, OnInit, Input } from '@angular/core';
import { ShopVO, ShopLocationsVO, LocationVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-shop-location',
  templateUrl: './shop-location.component.html',
})

export class ShopLocationComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  public shopLocationsVO:ShopLocationsVO;
  public locs:ShopLocationsVO;
  public availableBilling:Array<LocationVO> = [];
  public selectedBilling:Array<LocationVO> = [];
  public availableShipping:Array<LocationVO> = [];
  public selectedShipping:Array<LocationVO> = [];

  public changed:boolean = false;

  public loading:boolean = false;

  constructor(private _shopService:ShopService) {
    LogUtil.debug('ShopLocationComponent constructor');
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
    if (this._reload || this.shopLocationsVO != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopLocationComponent ngOnInit shop', this.shop);
  }


  onDataChange() {
    LogUtil.debug('ShopLocationComponent data changed');
    this.changed = true;
  }

  remapSelections() {
    let availableBilling:Array<LocationVO> = [];
    let selectedBilling:Array<LocationVO> = [];
    let availableShipping:Array<LocationVO> = [];
    let selectedShipping:Array<LocationVO> = [];

    this.locs.all.forEach(loc => {
      if (this.locs.supportedBilling.indexOf(loc.code) === -1) {
        availableBilling.push(loc);
      } else {
        selectedBilling.push(loc);
      }
      if (this.locs.supportedShipping.indexOf(loc.code) === -1) {
        availableShipping.push(loc);
      } else {
        selectedShipping.push(loc);
      }
    });

    let _sort = function(a:LocationVO, b:LocationVO):number {
      return (a.name < b.name) ? -1 : 1;
    };

    availableBilling.sort(_sort);
    selectedBilling.sort(_sort);
    availableShipping.sort(_sort);
    selectedShipping.sort(_sort);

    this.availableBilling = availableBilling;
    this.selectedBilling = selectedBilling;
    this.availableShipping = availableShipping;
    this.selectedShipping = selectedShipping;
  }

  onSaveHandler() {
    LogUtil.debug('ShopLocationComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.locs) {
      this.loading = true;
      this._shopService.saveShopLocations(this.locs).subscribe(shopLocationsVo => {
        this.shopLocationsVO = Util.clone(shopLocationsVo);
        this.locs = Util.clone(shopLocationsVo);
        this.remapSelections();
        this.changed = false;
        this._reload = false;
        this.loading = false;
      });
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopLocationComponent discard handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLocationsVO) {
      this.locs = Util.clone(this.shopLocationsVO);
      this.remapSelections();
      this.changed = false;
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ShopLocationComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      this._shopService.getShopLocations(this.shop.shopId).subscribe(shopLocationsVo => {
        this.shopLocationsVO  = Util.clone(shopLocationsVo);
        this.locs  = Util.clone(shopLocationsVo);
        this.remapSelections();
        this.changed = false;
        this._reload = false;
        this.loading = false;
      });
    } else {
      this.locs = null;
    }
  }

  onAvailableBillingClick(event:LocationVO) {
    LogUtil.debug('ShopLocationComponent onAvailableBillingClick', event);
    this.locs.supportedBilling.push(event.code);
    this.remapSelections();
    this.changed = true;
  }

  onAvailableShippingClick(event:LocationVO) {
    LogUtil.debug('ShopLocationComponent onAvailableShippingClick', event);
    this.locs.supportedShipping.push(event.code);
    this.remapSelections();
    this.changed = true;
  }

  onSupportedBillingClick(event:LocationVO) {
    LogUtil.debug('ShopLocationComponent onSupportedBillingClick', event);
    let idx = this.locs.supportedBilling.indexOf(event.code);
    if (idx != -1) {
      this.locs.supportedBilling.splice(idx, 1);
      this.remapSelections();
      this.changed = true;
    }
  }

  onSupportedShippingClick(event:LocationVO) {
    LogUtil.debug('ShopLocationComponent onSupportedBillingClick', event);
    let idx = this.locs.supportedShipping.indexOf(event.code);
    if (idx != -1) {
      this.locs.supportedShipping.splice(idx, 1);
      this.remapSelections();
      this.changed = true;
    }
  }

}
