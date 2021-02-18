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
import { I18nEventBus } from "../../shared/services/index";
import { AttributeVO, PromotionVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';
import { UiUtil } from "../../shared/ui/uiutil";

@Component({
  selector: 'cw-promotions',
  templateUrl: 'promotions.component.html',
})

export class PromotionsComponent implements OnInit, OnDestroy {

  @Input() selectedShopCode:string;

  @Input() selectedPromotion:PromotionVO;

  @Output() dataSelected: EventEmitter<PromotionVO> = new EventEmitter<PromotionVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _promotions:SearchResultVO<PromotionVO> = null;

  private _types:any = {};
  private _actions:any = {};

  public filteredPromotions:Array<PromotionVO>;

  //sorting
  public sortColumn:string = null;
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;

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

  @Input()
  set promoTypes(actions:Array<Pair<AttributeVO, boolean>>) {
    actions.forEach(type => {
      this._types[type.first.val] = type.first;
    });
    LogUtil.debug('PromotionsComponent mapped types', this._types);
  }

  @Input()
  set promoActions(actions:Array<Pair<AttributeVO, boolean>>) {
    actions.forEach(action => {
      this._actions[action.first.val] = action.first;
    });
    LogUtil.debug('PromotionsComponent mapped actions', this._actions);
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

  onSelectRow(row:PromotionVO) {
    LogUtil.debug('PromotionsComponent onSelectRow handler', row);
    if (row == this.selectedPromotion) {
      this.selectedPromotion = null;
    } else {
      this.selectedPromotion = row;
    }
    this.dataSelected.emit(this.selectedPromotion);
  }

  isAvailableFromNow(row:PromotionVO) {
    return row.enabledFrom === null || (row.enabledFrom < new Date());
  }

  isAvailableToNow(row:PromotionVO) {
    return row.enabledTo === null || (row.enabledTo > new Date());
  }

  getTypeName(row:PromotionVO) {
    if (this._types.hasOwnProperty(row.promoType)) {
      let lang = I18nEventBus.getI18nEventBus().current();
      let attr:AttributeVO = this._types[row.promoType];
      let i18n = attr.displayNames;
      let def = attr.name != null ? attr.name : attr.code;

      return UiUtil.toI18nString(i18n, def, lang);
    }
    return row.promoType;
  }

  getActionName(row:PromotionVO) {
    if (this._actions.hasOwnProperty(row.promoAction)) {
      let lang = I18nEventBus.getI18nEventBus().current();
      let attr:AttributeVO = this._actions[row.promoAction];
      let i18n = attr.displayNames;
      let def = attr.name != null ? attr.name : attr.code;

      return UiUtil.toI18nString(i18n, def, lang);
    }
    return row.promoAction;
  }

  getPromoTypeIcon(row:PromotionVO) {
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

  getPromoActionIcon(row:PromotionVO) {
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
