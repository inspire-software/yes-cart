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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { PromotionCouponVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-promotion-coupons',
  moduleId: module.id,
  templateUrl: 'promotion-coupons.component.html',
})

export class PromotionCouponsComponent implements OnInit, OnDestroy {

  @Input() selectedCoupon:PromotionCouponVO;

  @Output() dataSelected: EventEmitter<PromotionCouponVO> = new EventEmitter<PromotionCouponVO>();

  private _coupons:Array<PromotionCouponVO> = [];

  private filteredCoupons:Array<PromotionCouponVO>;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('PromotionCouponsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PromotionCouponsComponent ngOnInit');
  }

  @Input()
  set coupons(coupons:Array<PromotionCouponVO>) {
    this._coupons = coupons;
    this.filterCoupons();
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionCouponsComponent ngOnDestroy');
    this.selectedCoupon = null;
    this.dataSelected.emit(null);
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:PromotionCouponVO) {
    LogUtil.debug('PromotionCouponsComponent onSelectRow handler', row);
    if (row == this.selectedCoupon) {
      this.selectedCoupon = null;
    } else {
      this.selectedCoupon = row;
    }
    this.dataSelected.emit(this.selectedCoupon);
  }

  protected isExhausted(row:PromotionCouponVO) {
    return row.usageLimit > 0 && row.usageLimit <= row.usageCount;
  }


  private filterCoupons() {

    this.filteredCoupons = this._coupons;
    LogUtil.debug('PromotionCouponsComponent filterPromotions', this.filteredCoupons);

    if (this.filteredCoupons === null) {
      this.filteredCoupons = [];
    }

    let _total = this.filteredCoupons.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
