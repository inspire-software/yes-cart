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
import { Component, OnInit, OnDestroy, Input, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, SubShopVO, ValidationRequestVO } from './../../shared/model/index';
import { ShopService } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-subs',
  moduleId: module.id,
  templateUrl: 'shop-subs.component.html',
})

export class ShopSubsComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;


  private shopSubs:ShopVO[];
  private subFilter:string;
  private filteredShopSub:Array<ShopVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  private selectedRow:ShopVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private subShopToEdit:SubShopVO;

  private shopSubForm:any;
  private shopSubFormSub:any; // tslint:disable-line:no-unused-variable

  private loading:boolean = false;

  /**
   * Construct shop sub panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopSubsComponent constructed');

    this.subShopToEdit = this.newSubInstance();

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || !that.shopSubForm) {
        return null;
      }

      let basic = YcValidators.requiredValidCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'shop', subjectId: 0, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.shopSubForm = fb.group({
      'code': ['', validCode],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'admin': ['', YcValidators.requiredValidEmail],
    });
  }

  newSubInstance():SubShopVO {
    if (this._shop != null) {
      return {'code': null, 'masterId': this._shop.shopId, 'masterCode' : this._shop.code, 'name': null, 'admin': null};
    } else {
      return {'code': null, 'masterId': 0, 'masterCode' : null, 'name': null, 'admin': null};
    }
  }

  formBind():void {
    UiUtil.formBind(this, 'shopSubForm', 'shopSubFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopSubFormSub');
  }


  formChange():void {
    LogUtil.debug('AttributeComponent formChange', this.shopSubForm.valid, this.subShopToEdit);
    this.validForSave = false; // this.shopSubForm.valid;
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.getShopSubs();
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._reload || this.shopSubs != null) {
      this.getShopSubs();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  /** {@inheritDoc} */
  ngOnInit() {
    LogUtil.debug('ShopSubsComponent ngOnInit shop', this.shop);

    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterSubs();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopSubsComponent ngOnDestroy');
    this.formUnbind();
  }

  protected onRowNew() {
    LogUtil.debug('ShopSubsComponent onRowNew handler');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'shopSubForm', 'subShopToEdit', this.newSubInstance());
    this.editModalDialog.show();
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ShopSubsComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      var _sub:any = this._shopService.saveSubShop(this.subShopToEdit).subscribe(
          rez => {
          this.changed = false;
          this._reload = false;
          this.selectedRow = null;
          _sub.unsubscribe();
          this.getShopSubs();
        }
      );
    }
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


  protected onClearFilter() {

    this.subFilter = '';
    this.delayedFiltering.delay();

  }


  /**
   * Read subs, that belong to shop.
   */
  private getShopSubs() {
    LogUtil.debug('ShopSubsComponent get subs', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      this._shopService.getSubShops(this.shop.shopId).subscribe(shopSubs => {

        LogUtil.debug('ShopSubsComponent subs', this.shopSubs);
        this.shopSubs = shopSubs;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;

        this.filterSubs();
        LogUtil.debug('ShopSubsComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);
        this.loading = false;
      });
    } else {
      this.shopSubs = null;
      this.filteredShopSub = [];
      this.selectedRow = null;
    }
  }

  private filterSubs() {
    let _filter = this.subFilter ? this.subFilter.toLowerCase() : null;
    let _subs:ShopVO[] = [];
    if (_filter) {
      _subs = this.shopSubs.filter(sub =>
        sub.code.toLowerCase().indexOf(_filter) !== -1 ||
        sub.name.toLowerCase().indexOf(_filter) !== -1
      );
      LogUtil.debug('ShopSubsComponent filterSubs', _filter);
    } else {
      _subs = this.shopSubs;
      LogUtil.debug('ShopSubsComponent filterSubs no filter');
    }

    if (_subs === null) {
      _subs = [];
    }

    _subs.sort(function(a:ShopVO, b:ShopVO) {

      return a.name > b.name ? 1 : -1;

    });

    this.filteredShopSub = _subs;

    let _total = this.filteredShopSub.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
