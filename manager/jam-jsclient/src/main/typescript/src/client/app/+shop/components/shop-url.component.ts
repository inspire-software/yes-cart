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
import { ShopVO, ShopUrlVO, UrlVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-url',
  moduleId: module.id,
  templateUrl: 'shop-url.component.html',
})

export class ShopUrlComponent implements OnInit, OnDestroy {

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


  private shopUrl:ShopUrlVO;
  private urlFilter:string;
  private filteredShopUrl:Array<UrlVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  private selectedRow:UrlVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private urlToEdit:UrlVO;

  private shopUrlForm:any;
  private shopUrlFormSub:any; // tslint:disable-line:no-unused-variable

  private loading:boolean = false;

  /**
   * Construct shop url panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopUrlComponent constructed');

    this.urlToEdit = this.newUrlInstance();

    this.shopUrlForm = fb.group({
      'url': ['', YcValidators.requiredValidDomainName],
      'theme': [''],
      'primary': [''],
    });
  }

  newUrlInstance():UrlVO {
    return {'urlId': 0, 'url': '', 'theme' : '', 'primary': false};
  }

  formBind():void {
    UiUtil.formBind(this, 'shopUrlForm', 'shopUrlFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopUrlFormSub');
  }


  formChange():void {
    LogUtil.debug('AttributeComponent formChange', this.shopUrlForm.valid, this.urlToEdit);
    this.validForSave = this.shopUrlForm.valid;
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
    if (this._reload || this.shopUrl != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  /** {@inheritDoc} */
  ngOnInit() {
    LogUtil.debug('ShopUrlComponent ngOnInit shop', this.shop);

    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterUrls();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopUrlComponent ngOnDestroy');
    this.formUnbind();
  }


  /**
   * Row delete handler.
   * @param row url to delete.
   */
  protected onRowDelete(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowEdit handler', row);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'shopUrlForm', 'urlToEdit', Util.clone(row));
    this.editModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onRowPrimary(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row.primary === false) {
      this.resetPrimary(row);
    } else {
      // do not allow unsetting because we need at least one primary for emails to reference shop url
    }
    this.changed = true;
  }

  protected onRowPrimarySelected() {
    if (this.selectedRow != null) {
      this.onRowPrimary(this.selectedRow);
    }
  }

  protected onSelectRow(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onRowNew() {
    LogUtil.debug('ShopUrlComponent onRowNew handler');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'shopUrlForm', 'urlToEdit', this.newUrlInstance());
    this.editModalDialog.show();
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onSaveHandler() {
    LogUtil.debug('ShopUrlComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopUrl) {
      var _sub:any = this._shopService.saveShopUrls(this.shopUrl).subscribe(
          rez => {
            this.shopUrl = rez;
            this.changed = false;
            this._reload = false;
            this.selectedRow = null;
            this.filterUrls();
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopUrlComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopUrlComponent refresh handler', this.shop);
    this.getShopUrls();
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopUrlComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let urlToDelete = this.selectedRow.url;
      let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.url === urlToDelete;} );
      LogUtil.debug('ShopUrlComponent onDeleteConfirmationResult index in array of urls ' + idx);
      this.shopUrl.urls.splice(idx, 1);
      this.filterUrls();
      this.selectedRow = null;
      this.changed = true;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ShopUrlComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.urlToEdit.urlId === 0) { // add new
        LogUtil.debug('ShopUrlComponent onEditModalResult add new url', this.shopUrl);
        this.shopUrl.urls.push(this.urlToEdit);
        this.totalItems++;
      } else { // edit existing
        LogUtil.debug('ShopUrlComponent onEditModalResult update existing', this.shopUrl);
        let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.urlId === this.urlToEdit.urlId;} );
        this.shopUrl.urls[idx] = this.urlToEdit;
      }
      this.selectedRow = this.urlToEdit;
      if (this.urlToEdit.primary) {
        this.resetPrimary(this.urlToEdit);
      }
      this.changed = true;
      this.filterUrls();
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

    this.urlFilter = '';
    this.delayedFiltering.delay();

  }


  private resetPrimary(row:UrlVO) {
    this.shopUrl.urls.forEach((url:UrlVO) => {
      url.primary = (url.urlId === row.urlId || (url.urlId === 0 && url.url === row.url)); // Match by id and url
    });
  }


  /**
   * Read urls, that belong to shop.
   */
  private getShopUrls() {
    LogUtil.debug('ShopUrlComponent get urls', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      this._shopService.getShopUrls(this.shop.shopId).subscribe(shopUrl => {

        LogUtil.debug('ShopUrlComponent urls', this.shopUrl);
        this.shopUrl = shopUrl;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;

        this.filterUrls();
        LogUtil.debug('ShopUrlComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);
        this.loading = false;
      });
    } else {
      this.shopUrl = null;
      this.filteredShopUrl = [];
      this.selectedRow = null;
    }
  }

  private filterUrls() {
    let _filter = this.urlFilter ? this.urlFilter.toLowerCase() : null;
    let _urls:UrlVO[] = [];
    if (_filter) {
      _urls = this.shopUrl.urls.filter(url =>
        url.url.toLowerCase().indexOf(_filter) !== -1 ||
        url.theme && url.theme.toLowerCase().indexOf(_filter) !== -1
      );
      LogUtil.debug('ShopUrlComponent filterUrls', _filter);
    } else {
      _urls = this.shopUrl.urls;
      LogUtil.debug('ShopUrlComponent filterUrls no filter');
    }

    if (_urls === null) {
      _urls = [];
    }

    _urls.sort(function(a:UrlVO, b:UrlVO) {

      return a.url > b.url ? 1 : -1;

    });

    this.filteredShopUrl = _urls;

    let _total = this.filteredShopUrl.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
