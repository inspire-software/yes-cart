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
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { ShopVO, AttrValueShopVO, Pair } from './../../shared/model/index';
import { ShopService } from './../../shared/services/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-attributes',
  moduleId: module.id,
  templateUrl: 'shop-attributes.component.html',
})

export class ShopAttributesComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopAttributes:Array<AttrValueShopVO> = null;
  private attributeFilter:string;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  private selectedRow:AttrValueShopVO;

  private update:Array<Pair<AttrValueShopVO, boolean>>;

  private searchHelpShow:boolean = false;

  private loading:boolean = false;

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
  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ShopAttributeComponent ngOnInit shop', this.shop);
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  protected onSelectRow(row:AttrValueShopVO) {
    LogUtil.debug('ShopAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onDataChange(event:any) {
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    LogUtil.debug('ShopAttributeComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.update) {

      LogUtil.debug('ShopAttributeComponent Save handler update', this.update);

      var _sub:any = this._shopService.saveShopAttributes(this.update).subscribe(
          rez => {
            LogUtil.debug('ShopAttributeComponent attributes', rez);
            this.shopAttributes = rez;
            this.changed = false;
            this.validForSave = false;
            this._reload = false;
            this.selectedRow = null;
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopAttributeComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopAttributeComponent refresh handler', this.shop);
    this.getShopAttributes();
  }

  protected onClearFilter() {

    this.attributeFilter = '';

  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  protected onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  protected onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  protected onSearchValuesChanges() {
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
      var _sub:any = this._shopService.getShopAttributes(this.shop.shopId).subscribe(shopAttributes => {

        LogUtil.debug('ShopAttributeComponent attributes', shopAttributes);
        this.shopAttributes = shopAttributes;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;
        _sub.unsubscribe();
        this.loading = false;

      });
    } else {
      this.shopAttributes = null;
      this.selectedRow = null;
    }
  }


}
