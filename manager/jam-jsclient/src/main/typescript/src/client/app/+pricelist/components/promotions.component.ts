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
import { PromotionVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-promotions',
  moduleId: module.id,
  templateUrl: 'promotions.component.html',
})

export class PromotionsComponent implements OnInit, OnDestroy {

  @Input() selectedShopCode:string;

  @Input() selectedPromotion:PromotionVO;

  @Output() dataSelected: EventEmitter<PromotionVO> = new EventEmitter<PromotionVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _promotions:SearchResultVO<PromotionVO> = null;

  private filteredPromotions:Array<PromotionVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('PromotionsComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('PromotionsComponent ngOnInit');
  }

  @Input()
  set promotions(promotions:SearchResultVO<PromotionVO>) {
    this._promotions = promotions;
    this.filterPromotions();
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionsComponent ngOnDestroy');
    this.selectedPromotion = null;
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

  protected onSelectRow(row:PromotionVO) {
    LogUtil.debug('PromotionsComponent onSelectRow handler', row);
    if (row == this.selectedPromotion) {
      this.selectedPromotion = null;
    } else {
      this.selectedPromotion = row;
    }
    this.dataSelected.emit(this.selectedPromotion);
  }

  protected isAvailableFromNow(row:PromotionVO) {
    return row.enabledFrom === null || (row.enabledFrom < new Date());
  }

  protected isAvailableToNow(row:PromotionVO) {
    return row.enabledTo === null || (row.enabledTo > new Date());
  }

  protected getPromoTypeIcon(row:PromotionVO) {
    switch (row.promoType) {
      case 'O':
        return 'fa-shopping-cart';
      case 'S':
        return 'fa-truck';
      case 'I':
        return 'fa-dropbox';
      case 'C':
        return 'fa-user-circle';
      default:
        return 'fa-question';
    }
  }

  protected getPromoActionIcon(row:PromotionVO) {
    switch (row.promoAction) {
      case 'F':
        return 'fa-money';
      case 'P':
      case 'S':
        return 'fa-percent';
      case 'G':
        return 'fa-gift';
      case 'T':
        return 'fa-tag';
      default:
        return 'fa-question';
    }
  }

  private filterPromotions() {

    LogUtil.debug('PromotionsComponent filterPromotions', this.filteredPromotions);

    if (this._promotions != null) {

      this.filteredPromotions = this._promotions.items != null ? this._promotions.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._promotions.searchContext.size;
      this.totalItems = this._promotions.total;
      this.currentPage = this._promotions.searchContext.start + 1;
      this.sortColumn = this._promotions.searchContext.sortBy;
      this.sortDesc = this._promotions.searchContext.sortDesc;
    } else {
      this.filteredPromotions = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
