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
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { ShopVO, AttrValueShopVO, Pair } from './../../shared/model/index';
import { ShopService } from './../../shared/services/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-shop-attributes',
  templateUrl: 'shop-attributes.component.html',
})

export class ShopAttributesComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  public shopAttributes:Array<AttrValueShopVO> = null;
  public attributeFilter:string;
  public attributeSort:Pair<string, boolean> = { first: 'name', second: false };

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  public selectedRow:AttrValueShopVO;

  public imageOnlyMode:boolean = false;

  private update:Array<Pair<AttrValueShopVO, boolean>>;

  public searchHelpShow:boolean = false;

  public loading:boolean = false;

  public includeSecure:boolean = false;
  public changeIncludeSecure:boolean = false;

  /**
   * Construct shop attribute panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService) {
    LogUtil.debug('ShopAttributeComponent constructed');

    this.update = [];

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
    if (this._reload || this.shopAttributes != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  public ngOnInit() {
    LogUtil.debug('ShopAttributeComponent ngOnInit shop', this.shop);
  }

  onIncludeSecure() {
    this.changeIncludeSecure = !this.includeSecure;
    this.getShopAttributes();
  }

  onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null && !this.selectedRow.attribute.mandatory) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('ShopAttributeComponent onPageSelected', page);
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('ShopAttributeComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  onSelectRow(row:AttrValueShopVO) {
    LogUtil.debug('ShopAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onDataChange(event:any) {
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onSaveHandler() {
    LogUtil.debug('ShopAttributeComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.update) {

      LogUtil.debug('ShopAttributeComponent Save handler update', this.update);

      this.loading = true;
      this._shopService.saveShopAttributes(this.update, this.includeSecure).subscribe(
          rez => {
            LogUtil.debug('ShopAttributeComponent attributes', rez);
            this.shopAttributes = rez;
            this.changed = false;
            this.validForSave = false;
            this._reload = false;
            this.selectedRow = null;
            this.loading = false;
        }
      );
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopAttributeComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopAttributeComponent refresh handler', this.shop);
    this.changeIncludeSecure = this.includeSecure;
    this.getShopAttributes();
  }

  onClearFilter() {

    this.attributeFilter = '';

  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }

  /**
   * Read attributes, that belong to shop.
   */
  private getShopAttributes() {
    LogUtil.debug('ShopAttributeComponent get attributes', this.shop);
    if (this.shop.shopId > 0) {

      this.loading = true;
      this._shopService.getShopAttributes(this.shop.shopId, this.changeIncludeSecure).subscribe(shopAttributes => {

        LogUtil.debug('ShopAttributeComponent attributes', shopAttributes);
        this.shopAttributes = shopAttributes;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;
        this.loading = false;
        this.includeSecure = this.changeIncludeSecure; // change only if we get successful result

      });
    } else {
      this.shopAttributes = null;
      this.selectedRow = null;
    }
  }


}
