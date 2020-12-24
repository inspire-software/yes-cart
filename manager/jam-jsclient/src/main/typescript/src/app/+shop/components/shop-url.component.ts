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
import { ShopVO, ShopUrlVO, UrlVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-shop-url',
  templateUrl: 'shop-url.component.html',
})

export class ShopUrlComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  //sorting
  public sortColumn:string = 'url';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;


  public shopUrl:ShopUrlVO;
  public urlFilter:string;
  public filteredShopUrl:Array<UrlVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  public selectedRow:UrlVO;

  public urlToEdit:UrlVO;

  public shopUrlForm:any;

  public loading:boolean = false;

  /**
   * Construct shop url panel
   *
   * @param _shopService shop service
   * @param fb form builder
   */
  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    LogUtil.debug('ShopUrlComponent constructed');

    this.urlToEdit = this.newUrlInstance();

    this.shopUrlForm = fb.group({
      'url': ['', CustomValidators.requiredValidDomainName],
      'theme': [''],
      'primary': [''],
    });
  }

  newUrlInstance():UrlVO {
    return {'urlId': 0, 'url': '', 'theme' : '', 'primary': false};
  }

  formBind():void {
    UiUtil.formBind(this, 'shopUrlForm', 'formChange', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'shopUrlForm');
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
  onRowDelete(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  onRowEdit(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowEdit handler', row);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'shopUrlForm', 'urlToEdit', Util.clone(row));
    this.editModalDialog.show();
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  onRowPrimary(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row.primary === false) {
      this.resetPrimary(row);
    } else {
      // do not allow unsetting because we need at least one primary for emails to reference shop url
    }
    this.changed = true;
  }

  onRowPrimarySelected() {
    if (this.selectedRow != null) {
      this.onRowPrimary(this.selectedRow);
    }
  }

  onSelectRow(row:UrlVO) {
    LogUtil.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onRowNew() {
    LogUtil.debug('ShopUrlComponent onRowNew handler');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'shopUrlForm', 'urlToEdit', this.newUrlInstance());
    this.editModalDialog.show();
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  onSaveHandler() {
    LogUtil.debug('ShopUrlComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopUrl) {
      this.loading = true;
      this._shopService.saveShopUrls(this.shopUrl).subscribe(
          rez => {
            this.shopUrl = rez;
            this.changed = false;
            this._reload = false;
            this.selectedRow = null;
            this.loading = false;
            this.filterUrls();
        }
      );
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopUrlComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopUrlComponent refresh handler', this.shop);
    this.getShopUrls();
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
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

  onEditModalResult(modalresult: ModalResult) {
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

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'url';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterUrls();
  }

  onClearFilter() {

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

    if (this.shopUrl.urls) {
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
    }

    if (_urls === null) {
      _urls = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function (a: any, b: any): number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    _urls.sort(_sort);

    this.filteredShopUrl = _urls;

    let _total = this.filteredShopUrl.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
