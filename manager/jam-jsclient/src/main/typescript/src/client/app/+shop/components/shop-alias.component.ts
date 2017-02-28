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
import { ShopVO, ShopAliasVO, AliasVO, ValidationRequestVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-alias',
  moduleId: module.id,
  templateUrl: 'shop-alias.component.html',
})

export class ShopAliasComponent implements OnInit, OnDestroy {

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


  private shopAlias:ShopAliasVO;
  private aliasFilter:string;
  private filteredShopAlias:Array<AliasVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  private selectedRow:AliasVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private aliasToEdit:AliasVO;

  private shopAliasForm:any;
  private shopAliasFormSub:any; // tslint:disable-line:no-unused-variable

  private loading:boolean = false;

  /**
   * Construct shop alias panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopAliasComponent constructed');

    this.aliasToEdit = this.newAliasInstance();

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.shopAliasForm) {
        return null;
      }

      let basic = YcValidators.requiredNonBlankTrimmed(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'shop', subjectId: 0, field: 'alias', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.shopAliasForm = fb.group({
      'alias': ['', validCode]
    });
  }

  newAliasInstance():AliasVO {
    return {'aliasId': 0, 'alias': ''};
  }

  formBind():void {
    UiUtil.formBind(this, 'shopAliasForm', 'shopAliasFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopAliasFormSub');
  }


  formChange():void {
    LogUtil.debug('AttributeComponent formChange', this.shopAliasForm.valid, this.aliasToEdit);
    this.validForSave = this.shopAliasForm.valid;
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
    if (this._reload || this.shopAlias != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  /** {@inheritDoc} */
  ngOnInit() {
    LogUtil.debug('ShopAliasComponent ngOnInit shop', this.shop);

    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAliases();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopAliasComponent ngOnDestroy');
    this.formUnbind();
  }


  /**
   * Row delete handler.
   * @param row alias to delete.
   */
  protected onRowDelete(row:AliasVO) {
    LogUtil.debug('ShopAliasComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:AliasVO) {
    LogUtil.debug('ShopAliasComponent onRowEdit handler', row);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'shopAliasForm', 'aliasToEdit', Util.clone(row));
    this.editModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onSelectRow(row:AliasVO) {
    LogUtil.debug('ShopAliasComponent onRowPrimary handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onRowNew() {
    LogUtil.debug('ShopAliasComponent onRowNew handler');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'shopAliasForm', 'aliasToEdit', this.newAliasInstance());
    this.editModalDialog.show();
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onSaveHandler() {
    LogUtil.debug('ShopAliasComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopAlias) {
      var _sub:any = this._shopService.saveShopAliases(this.shopAlias).subscribe(
          rez => {
            this.shopAlias = rez;
            this.changed = false;
            this._reload = false;
            this.selectedRow = null;
            this.filterAliases();
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopAliasComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopAliasComponent refresh handler', this.shop);
    this.getShopAliases();
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopAliasComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let aliasToDelete = this.selectedRow.alias;
      let idx = this.shopAlias.aliases.findIndex(aliasVo =>  {return aliasVo.alias === aliasToDelete;} );
      LogUtil.debug('ShopAliasComponent onDeleteConfirmationResult index in array of aliases ' + idx);
      this.shopAlias.aliases.splice(idx, 1);
      this.filterAliases();
      this.selectedRow = null;
      this.changed = true;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ShopAliasComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.aliasToEdit.aliasId === 0) { // add new
        LogUtil.debug('ShopAliasComponent onEditModalResult add new alias', this.shopAlias);
        this.shopAlias.aliases.push(this.aliasToEdit);
        this.totalItems++;
      } else { // edit existing
        LogUtil.debug('ShopAliasComponent onEditModalResult update existing', this.shopAlias);
        let idx = this.shopAlias.aliases.findIndex(aliasVo =>  {return aliasVo.aliasId === this.aliasToEdit.aliasId;} );
        this.shopAlias.aliases[idx] = this.aliasToEdit;
      }
      this.selectedRow = this.aliasToEdit;
      this.changed = true;
      this.filterAliases();
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

    this.aliasFilter = '';
    this.delayedFiltering.delay();

  }


  /**
   * Read aliases, that belong to shop.
   */
  private getShopAliases() {
    LogUtil.debug('ShopAliasComponent get aliases', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      this._shopService.getShopAliases(this.shop.shopId).subscribe(shopAlias => {

        LogUtil.debug('ShopAliasComponent aliases', this.shopAlias);
        this.shopAlias = shopAlias;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;

        this.filterAliases();
        LogUtil.debug('ShopAliasComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);
        this.loading = false;
      });
    } else {
      this.shopAlias = null;
      this.filteredShopAlias = [];
      this.selectedRow = null;
    }
  }

  private filterAliases() {
    let _filter = this.aliasFilter ? this.aliasFilter.toLowerCase() : null;
    let _aliases:AliasVO[] = [];
    if (_filter) {
      _aliases = this.shopAlias.aliases.filter(alias =>
        alias.alias.toLowerCase().indexOf(_filter) !== -1
      );
      LogUtil.debug('ShopAliasComponent filterAliases', _filter);
    } else {
      _aliases = this.shopAlias.aliases;
      LogUtil.debug('ShopAliasComponent filterAliases no filter');
    }

    if (_aliases === null) {
      _aliases = [];
    }

    _aliases.sort(function(a:AliasVO, b:AliasVO) {

      return a.alias > b.alias ? 1 : -1;

    });

    this.filteredShopAlias = _aliases;

    let _total = this.filteredShopAlias.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
