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
import {Component, OnInit, OnDestroy, OnChanges, Input, ViewChild} from '@angular/core';
import {NgIf, NgFor, CORE_DIRECTIVES } from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {PaginationComponent} from './../../shared/pagination/index';
import {ShopVO, ShopUrlVO, UrlVO} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-shop-url',
  moduleId: module.id,
  templateUrl: 'shop-url.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent]
})

export class ShopUrlComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;


  shopUrl:ShopUrlVO;
  private urlFilter:string;
  filteredShopUrl:Array<UrlVO>;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  changed:boolean = false;
  changedSingle:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  selectedRow:UrlVO;

  urlToEdit:UrlVO;

  shopUrlForm:any;
  shopUrlFormSub:any;

  /**
   * Construct shop url panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService,
              fb: FormBuilder) {
    console.debug('ShopUrlComponent constructed');

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

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.shopUrlForm.controls) {
      this.shopUrlForm.controls[key]['_pristine'] = true;
      this.shopUrlForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.shopUrlFormSub = this.shopUrlForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingle) {
        this.validForSave = this.shopUrlForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.shopUrlFormSub) {
      console.debug('ShopUrlComponent unbining form');
      this.shopUrlFormSub.unsubscribe();
    }
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
  public ngOnInit() {
    console.debug('ShopUrlComponent ngOnInit shop', this.shop);

    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterUrls();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopUrlComponent ngOnDestroy');
    this.formUnbind();
  }


  /**
   * Row delete handler.
   * @param row url to delete.
   */
  protected onRowDelete(row:UrlVO) {
    console.debug('ShopUrlComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:UrlVO) {
    console.debug('ShopUrlComponent onRowEdit handler', row);
    this.formReset();
    this.validForSave = false;
    this.urlToEdit = Util.clone(row);
    this.changedSingle = false;
    this.editModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onRowPrimary(row:UrlVO) {
    console.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row.primary === false) {
      for (var idx = 0; idx < this.shopUrl.urls.length; idx++) {
        var url:UrlVO = this.shopUrl.urls[idx];
        url.primary = (url.urlId === row.urlId || (url.urlId === 0 && url.url === row.url)); // Match by id and url
      }
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
    console.debug('ShopUrlComponent onRowPrimary handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onRowNew() {
    console.debug('ShopUrlComponent onRowNew handler');
    this.formReset();
    this.validForSave = false;
    this.urlToEdit = this.newUrlInstance();
    this.editModalDialog.show();
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  onDataChange(event:any) {
    console.debug('ShopUrlComponent data changed', event);
    this.changedSingle = true;
  }

  protected onSaveHandler() {
    console.debug('ShopUrlComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopUrl) {
      var _sub:any = this._shopService.saveShopUrls(this.shopUrl).subscribe(
          rez => {
            this.shopUrl = rez;
            this.changed = false;
            this._reload = false;
            this.selectedRow = null;
            this.filterUrls();
            console.debug('ShopUrlComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    console.debug('ShopUrlComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    console.debug('ShopUrlComponent refresh handler', this.shop);
    this.getShopUrls();
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopUrlComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let urlToDelete = this.selectedRow.url;
      let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.url === urlToDelete;} );
      console.debug('ShopUrlComponent onDeleteConfirmationResult index in array of urls ' + idx);
      this.shopUrl.urls.splice(idx, 1);
      this.filterUrls();
      this.selectedRow = null;
      this.changed = true;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    console.debug('ShopUrlComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.urlToEdit.urlId === 0) { // add new
        console.debug('ShopUrlComponent onEditModalResult add new url', this.shopUrl);
        this.shopUrl.urls.push(this.urlToEdit);
        this.totalItems++;
      } else { // edit existing
        console.debug('ShopUrlComponent onEditModalResult update existing', this.shopUrl);
        let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.urlId === this.urlToEdit.urlId;} );
        this.shopUrl.urls[idx] = this.urlToEdit;
      }
      this.selectedRow = this.urlToEdit;
      this.changed = true;
      this.filterUrls();
    }
  }

  /**
   * Read urls, that belong to shop.
   */
  private getShopUrls() {
    console.debug('ShopUrlComponent get urls', this.shop);
    if (this.shop.shopId > 0) {

      this._shopService.getShopUrls(this.shop.shopId).subscribe(shopUrl => {

        console.debug('ShopUrlComponent urls', this.shopUrl);
        this.shopUrl = shopUrl;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;

        this.filterUrls();
        console.debug('ShopUrlComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);

      });
    } else {
      this.shopUrl = null;
      this.filteredShopUrl = [];
      this.selectedRow = null;
    }
  }

  private filterUrls() {
    let _filter = this.urlFilter ? this.urlFilter.toLowerCase() : null;
    if (_filter) {
      this.filteredShopUrl = this.shopUrl.urls.filter(url =>
        url.url.toLowerCase().indexOf(_filter) !== -1 ||
        url.theme && url.theme.toLowerCase().indexOf(_filter) !== -1
      );
      console.debug('ShopUrlComponent filterUrls', _filter);
    } else {
      this.filteredShopUrl = this.shopUrl.urls;
      console.debug('ShopUrlComponent filterUrls no filter');
    }

    if (this.filteredShopUrl === null) {
      this.filteredShopUrl = [];
    }

    let _total = this.filteredShopUrl.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
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


}
