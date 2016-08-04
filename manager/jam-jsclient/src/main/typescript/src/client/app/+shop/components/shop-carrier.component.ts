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
import {NgIf, NgFor} from '@angular/common';
import {ShopVO, ShopCarrierVO} from './../../shared/model/index';
import {ShippingService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';

@Component({
  selector: 'yc-shop-carrier',
  moduleId: module.id,
  templateUrl: './shop-carrier.component.html',
  directives: [ NgIf, NgFor, DataControlComponent],
})

export class ShopCarrierComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopCarriersVO:Array<ShopCarrierVO>;
  availableCarriers:Array<ShopCarrierVO>;
  selectedCarriers:Array<ShopCarrierVO>;

  changed:boolean = false;

  constructor(private _shippingService:ShippingService) {
    console.debug('ShopCarrierComponent constructor');
  }

  ngOnInit() {
    console.debug('ShopCarrierComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopCarrierComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }


  onDataChange() {
    console.debug('ShopCarrierComponent data changed');
    this.changed = true;
  }

  private remapCarriers() {

    var availableCarriers:Array<ShopCarrierVO> = [];
    var selectedCarriers:Array<ShopCarrierVO> = [];

    this.shopCarriersVO.forEach(carrier => {
      if (carrier.carrierShop.disabled) {
        availableCarriers.push(carrier);
      } else {
        selectedCarriers.push(carrier);
      }
    });

    var _sort = function(a:ShopCarrierVO, b:ShopCarrierVO):number {
      if (a.name < b.name)
        return -1;
      if (a.name > b.name)
        return 1;
      return 0;
    };

    availableCarriers.sort(_sort);
    selectedCarriers.sort(_sort);

    this.selectedCarriers = selectedCarriers;
    this.availableCarriers = availableCarriers;

  }

  onSaveHandler() {
    console.debug('ShopCarrierComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCarriersVO) {
      var _sub:any = this._shippingService.saveShopCarriers(this.shopCarriersVO).subscribe(shopLanguagesVo => {
        this.shopCarriersVO = Util.clone(shopLanguagesVo);
        this.remapCarriers();
        this.changed = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    console.debug('ShopCarrierComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    console.debug('ShopCarrierComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shippingService.getShopCarriers(this.shop.shopId).subscribe(shopCarriersVo => {
        console.debug('ShopCarrierComponent getShopCarriers', shopCarriersVo);
        this.shopCarriersVO  = Util.clone(shopCarriersVo);
        this.remapCarriers();
        this.changed = false;
        _sub.unsubscribe();
      });
    } else {
      this.shopCarriersVO = null;
    }
  }

  onAvailableCarrierClick(event:any) {
    console.debug('ShopCarrierComponent onAvailableCarrierClick', event);
    event.carrierShop.disabled = false;
    this.remapCarriers();
    this.changed = true;
  }

  onSupportedCarrierClick(event:any) {
    console.debug('ShopCarrierComponent onSupportedCarrierClick', event);
    event.carrierShop.disabled = true;
    this.remapCarriers();
    this.changed = true;
  }

}
