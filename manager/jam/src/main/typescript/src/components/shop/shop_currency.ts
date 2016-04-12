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
import {ActiveLabel} from '../common/active_label';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {Util} from '../../service/util';

@Component({
  selector: 'shop-currency',
  moduleId: module.id,
  templateUrl: './shop_currency.html',
  styleUrls: ['./shop_currency.css'],
  directives: [DataControl, ActiveLabel],
  providers: [HTTP_PROVIDERS, ShopService]
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
    console.debug('ngOnInit shopId from params is ' + shopId);
    this.onRefreshHandler();
  }


  onDataChange() {
    console.debug('data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('Save handler for shop id ');
    this._shopService.saveShopCurrencies(this.curr).subscribe(shopSupportedCurrenciesVO => {
      this.curr  = Util.clone(shopSupportedCurrenciesVO);
      Util.remove(this.curr.all, this.curr.supported);
      this.changed = false;
    });

  }

  onDiscardEventHandler() {
    console.debug('Discard handler for shop id ' );
    this.curr  = Util.clone(this.shopSupportedCurrenciesVO);
    this.changed = false;
  }

  onRefreshHandler() {
    console.debug('Refresh handler');
    let shopId = this._routeParams.get('shopId');
    this._shopService.getShopCurrencies(+shopId).subscribe(shopSupportedCurrenciesVO => {
      this.shopSupportedCurrenciesVO  = Util.clone(shopSupportedCurrenciesVO);
      this.curr  = Util.clone(shopSupportedCurrenciesVO);
      Util.remove(this.curr.all, this.curr.supported);
      this.changed = false;
    });
  }

  onAvailableCurrencyClick(event) {
    console.debug('onAvailableCurrencyClick evt ' + event);
    this.curr.supported.push(event);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

  onSupportedCurrencyClick(event) {
    console.debug('onSupportedCurrencyClick evt ' + event);
    this.curr.supported = this.curr.supported.filter( obj => {return obj !== event;});
    this.curr.all = Util.clone(this.shopSupportedCurrenciesVO.all);
    Util.remove(this.curr.all, this.curr.supported);
    this.changed = true;
  }

}
