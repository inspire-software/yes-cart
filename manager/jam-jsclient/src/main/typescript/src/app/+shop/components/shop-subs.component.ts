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
import { Component, OnInit, OnDestroy, Input, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { ShopVO, SubShopVO, ValidationRequestVO } from './../../shared/model/index';
import { ShopEventBus, ShopService } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-shop-subs',
  templateUrl: 'shop-subs.component.html',
})

export class ShopSubsComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  //sorting
  public sortColumn:string = 'name';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;


  public shopSubs:ShopVO[];
  public subFilter:string;
  public filteredShopSub:Array<ShopVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  public selectedRow:ShopVO;

  public subShopToEdit:SubShopVO;

  public shopSubForm:any;

  public loading:boolean = false;

  /**
   * Construct shop sub panel
   *
   * @param _shopService shop service
   * @param fb form builder
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

      let basic = CustomValidators.requiredValidCode(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'shop', subjectId: 0, field: 'code', value: code };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.shopSubForm = fb.group({
      'code': ['', validCode],
      'name': ['', CustomValidators.requiredNonBlankTrimmed],
      'admin': ['', CustomValidators.requiredValidEmail],
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
    UiUtil.formBind(this, 'shopSubForm', 'formChange', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopSubForm');
  }


  formChange():void {
    LogUtil.debug('AttributeComponent formChange', this.shopSubForm.valid, this.subShopToEdit);
    this.validForSave = this.shopSubForm.valid;
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

  onRowNew() {
    LogUtil.debug('ShopSubsComponent onRowNew handler');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'shopSubForm', 'subShopToEdit', this.newSubInstance());
    this.editModalDialog.show();
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ShopSubsComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.loading = true;
      this._shopService.saveSubShop(this.subShopToEdit).subscribe(
        rez => {
          this.changed = false;
          this._reload = false;
          this.selectedRow = null;
          this.loading = false;
          this.getAllShops();
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

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'name';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterSubs();
  }

  onClearFilter() {

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

    if (this.shopSubs) {
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
    }

    if (_subs === null) {
      _subs = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function (a: any, b: any): number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    _subs.sort(_sort);

    this.filteredShopSub = _subs;

    let _total = this.filteredShopSub.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


  private getAllShops() {
    LogUtil.debug('ShopSubsComponent getAllShops');
    this._shopService.getAllShops().subscribe( allshops => {
      LogUtil.debug('ShopSubsComponent getAllShops', allshops);
      ShopEventBus.getShopEventBus().emitAll(allshops);
    });
  }


}
