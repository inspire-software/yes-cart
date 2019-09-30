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
import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { CartVO, CartItemVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { CookieUtil } from './../../shared/cookies/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-promotion-test-result',
  moduleId: module.id,
  templateUrl: 'promotion-test-result.component.html',
})

export class PromotionTestResultComponent implements OnInit, OnDestroy {

  @Input() cart:CartVO;

  constructor() {
    LogUtil.debug('PromotionTestResultComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PromotionTestResultComponent ngOnInit');
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionTestResultComponent ngOnDestroy');
  }


  get showGrossTotal():boolean {
    return Config.UI_ORDER_TOTALS === 'gross';
  }

  set showGrossTotal(showGrossTotal:boolean) {
    Config.UI_ORDER_TOTALS = showGrossTotal ? 'gross' : 'net';
    let cookieName = 'ADM_UI_ORDER_TOTALS';
    CookieUtil.createCookie(cookieName, Config.UI_ORDER_TOTALS, 360);
  }

  onShowGrossTotalClick() {
    let _gross = this.showGrossTotal;
    this.showGrossTotal = !_gross;
  }

  protected getUserIcon(cart:CartVO) {
    if (cart.logonState == 2) {
      return '<i class="fa fa-user"></i>';
    }
    return '';
  }


  getLinePriceFlags(row:CartItemVO):string {

    let flags = '';
    if (row.price < row.salePrice) {
      // promotion
      flags += '<i title="' + row.salePrice.toFixed(2) + '" class="fa fa-cart-arrow-down"></i>&nbsp;';
    }

    if (row.salePrice < row.listPrice) {
      // sale
      flags += '<i title="' + row.listPrice.toFixed(2) + '" class="fa fa-clock-o"></i>&nbsp;';
    }

    return flags;
  }


  getOrderListPriceFlags(cart:CartVO):string {

    let flags = '';
    if (cart.total.priceSubTotal < cart.total.listSubTotal) {
      // sale
      flags += '<i title="' + cart.total.listSubTotal.toFixed(2) + '" class="fa fa-cart-arrow-down"></i>&nbsp;';
    }

    return flags;
  }


  getPromotions(codes:string):string[] {
    if (codes == null) {
      return [];
    }
    return codes.split(',');
  }

}
