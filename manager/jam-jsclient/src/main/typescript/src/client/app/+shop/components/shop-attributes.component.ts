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
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {ShopVO, AttrValueShopVO, AttributeVO, Pair} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {YcValidators} from './../../shared/validation/validators';

@Component({
  selector: 'yc-shop-attributes',
  moduleId: module.id,
  templateUrl: 'shop-attributes.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, AttributeValuesComponent]
})

export class ShopAttributesComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  shopAttributes:Array<AttrValueShopVO> = null;
  attributeFilter:string;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  selectedRow:AttrValueShopVO;

  update:Array<Pair<AttrValueShopVO, boolean>>;

  /**
   * Construct shop attribute panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService) {
    console.debug('ShopAttributeComponent constructed');

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
    console.debug('ShopAttributeComponent ngOnInit shop', this.shop);
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
    console.debug('ShopAttributeComponent onSelectRow handler', row);
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
    console.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    console.debug('ShopAttributeComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.update) {

      console.debug('ShopAttributeComponent Save handler update', this.update);

      var _sub:any = this._shopService.saveShopAttributes(this.update).subscribe(
          rez => {
            console.debug('ShopAttributeComponent attributes', rez);
            this.shopAttributes = rez;
            this.changed = false;
            this._reload = false;
            this.selectedRow = null;
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    console.debug('ShopAttributeComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    console.debug('ShopAttributeComponent refresh handler', this.shop);
    this.getShopAttributes();
  }

  /**
   * Read attributes, that belong to shop.
   */
  private getShopAttributes() {
    console.debug('ShopAttributeComponent get attributes', this.shop);
    if (this.shop.shopId > 0) {

      var _sub:any = this._shopService.getShopAttributes(this.shop.shopId).subscribe(shopAttributes => {

        console.debug('ShopAttributeComponent attributes', shopAttributes);
        this.shopAttributes = shopAttributes;
        this.changed = false;
        this._reload = false;
        this.selectedRow = null;
        _sub.unsubscribe();

      });
    } else {
      this.shopAttributes = null;
      this.selectedRow = null;
    }
  }

}
