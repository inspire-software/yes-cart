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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { PromotionCouponVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-promotion-coupons',
  templateUrl: 'promotion-coupons.component.html',
})

export class PromotionCouponsComponent implements OnInit, OnDestroy {

  @Input() selectedCoupon:PromotionCouponVO;

  @Output() dataSelected: EventEmitter<PromotionCouponVO> = new EventEmitter<PromotionCouponVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _coupons:SearchResultVO<PromotionCouponVO> = null;

  public filteredCoupons:Array<PromotionCouponVO>;

  //sorting
  public sortColumn:string = null;
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;

  constructor() {
    LogUtil.debug('PromotionCouponsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PromotionCouponsComponent ngOnInit');
  }

  @Input()
  set coupons(coupons:SearchResultVO<PromotionCouponVO>) {
    this._coupons = coupons;
    this.filterCoupons();
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionCouponsComponent ngOnDestroy');
    this.selectedCoupon = null;
    this.dataSelected.emit(null);
  }

  onPageChanged(event:any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortSelected.emit(null);
      } else {  // same column asc, change to desc
        this.sortSelected.emit({ first: event, second: true });
      }
    } else { // different column, start asc sort
      this.sortSelected.emit({ first: event, second: false });
    }
  }

  onSelectRow(row:PromotionCouponVO) {
    LogUtil.debug('PromotionCouponsComponent onSelectRow handler', row);
    if (row == this.selectedCoupon) {
      this.selectedCoupon = null;
    } else {
      this.selectedCoupon = row;
    }
    this.dataSelected.emit(this.selectedCoupon);
  }

  isExhausted(row:PromotionCouponVO) {
    return row.usageLimit > 0 && row.usageLimit <= row.usageCount;
  }


  private filterCoupons() {

    LogUtil.debug('PromotionCouponsComponent filterPromotions', this.filteredCoupons);

    if (this._coupons != null) {

      this.filteredCoupons = this._coupons.items != null ? this._coupons.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._coupons.searchContext.size;
      this.totalItems = this._coupons.total;
      this.currentPage = this._coupons.searchContext.start + 1;
      this.sortColumn = this._coupons.searchContext.sortBy;
      this.sortDesc = this._coupons.searchContext.sortDesc;
    } else {
      this.filteredCoupons = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
