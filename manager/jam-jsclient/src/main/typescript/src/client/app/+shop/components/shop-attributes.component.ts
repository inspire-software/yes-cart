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
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {YcValidators} from './../../shared/validation/validators';

@Component({
  selector: 'yc-shop-attributes',
  moduleId: module.id,
  templateUrl: 'shop-attributes.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent]
})

export class ShopAttributesComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  //paging
  maxSize:number = 5;
  itemsPerPage:number = 10;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;


  shopAttributes:Array<AttrValueShopVO>;
  shopAttributesRemove:Array<number>;
  private attributeFilter:string;
  filteredShopAttributes:Array<AttrValueShopVO>;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  selectedRow:AttrValueShopVO;

  attributeToEdit:AttrValueShopVO;

  /**
   * Construct shop attribute panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService) {
    console.debug('ShopAttributeComponent constructed');

    this.attributeToEdit = null;

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ShopAttributeComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopAttributeComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  /**
   * Row delete handler.
   * @param row attribute to delete.
   */
  protected onRowDelete(row:AttrValueShopVO) {
    console.debug('ShopAttributeComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:AttrValueShopVO) {
    console.debug('ShopAttributeComponent onRowEdit handler', row);
    this.validForSave = false;
    this.attributeToEdit = Util.clone(row) ;
    this.editModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onSelectRow(row:AttrValueShopVO) {
    console.debug('ShopAttributeComponent onRowPrimary handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onFilterChange(event:any) {
    this.filterAttributes();
  }

  onDataChange(event:any) {
    this.validForSave = true;
    console.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    console.debug('ShopAttributeComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopAttributes) {

      let _update = <Array<Pair<AttrValueShopVO, boolean>>>[];
      this.shopAttributes.forEach(attr => {
        if (attr.attrvalueId !== 0 || (attr.val !== null && /\S+.*\S+/.test(attr.val))) {
          _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
        }
      });

      console.debug('ShopAttributeComponent Save handler update', _update);

      var _sub:any = this._shopService.saveShopAttributes(_update).subscribe(
          rez => {
            this.shopAttributes = rez;
            this.shopAttributesRemove = [];
            this.changed = false;
            this.selectedRow = null;
            this.filterAttributes();
            console.debug('ShopAttributeComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);
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

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopAttributeComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.attrvalueId;
      if (attrToDelete === 0) {
        let idx = this.shopAttributes.findIndex(attrVo => {
          return attrVo.attribute.code === this.selectedRow.attribute.code;
        });
        this.shopAttributes[idx].val = null;
        console.debug('ShopAttributeComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        console.debug('ShopAttributeComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.shopAttributesRemove.push(attrToDelete);
      }
      this.filterAttributes();
      this.selectedRow = null;
      this.changed = true;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    console.debug('ShopAttributeComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.attrvalueId === 0) { // add new
        console.debug('ShopAttributeComponent onEditModalResult add new attribute', this.shopAttributes);
        let idx = this.shopAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.attributeToEdit.attribute.code;} );
        this.shopAttributes[idx] = this.attributeToEdit;
      } else { // edit existing
        console.debug('ShopAttributeComponent onEditModalResult update existing', this.shopAttributes);
        let idx = this.shopAttributes.findIndex(attrVo =>  {return attrVo.attrvalueId === this.attributeToEdit.attrvalueId;} );
        this.shopAttributes[idx] = this.attributeToEdit;
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
    }
  }

  /**
   * Read attributes, that belong to shop.
   */
  private getShopAttributes() {
    console.debug('ShopAttributeComponent get attributes', this.shop);
    if (this.shop.shopId > 0) {

      this._shopService.getShopAttributes(this.shop.shopId).subscribe(shopAttributes => {

        console.debug('ShopAttributeComponent attributes', this.shopAttributes);
        this.shopAttributes = shopAttributes;
        this.shopAttributesRemove = [];
        this.changed = false;
        this.selectedRow = null;

        this.filterAttributes();
        console.debug('ShopAttributeComponent totalItems:' + this.totalItems + ', itemsPerPage:' + this.itemsPerPage);

      });
    } else {
      this.shopAttributes = null;
      this.shopAttributesRemove = null;
      this.filteredShopAttributes = [];
      this.selectedRow = null;
    }
  }

  private filterAttributes() {
    let _filter = this.attributeFilter ? this.attributeFilter.toLowerCase() : null;
    if (_filter) {
      this.filteredShopAttributes = this.shopAttributes.filter(val =>
        val.attribute.code.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.name.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.description && val.attribute.description.toLowerCase().indexOf(_filter) !== -1 ||
        val.val && val.val.toLowerCase().indexOf(_filter) !== -1
      );
      console.debug('ShopAttributeComponent filterAttributes', _filter);
    } else {
      this.filteredShopAttributes = this.shopAttributes;
      console.debug('ShopAttributeComponent filterAttributes no filter');
    }

    if (this.filteredShopAttributes === null) {
      this.filteredShopAttributes = [];
    }

    console.debug('ShopAttributeComponent filterAttributes', this.filteredShopAttributes);

    let _total = this.filteredShopAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  isRemovedAttribute(row:AttrValueShopVO):boolean {
    return this.shopAttributesRemove.indexOf(row.attrvalueId) !== -1;
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
