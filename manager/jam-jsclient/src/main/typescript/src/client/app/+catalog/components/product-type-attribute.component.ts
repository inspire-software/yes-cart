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
import {Component, OnInit, OnDestroy, OnChanges, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf, NgFor, CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {ProductAttributeSelectComponent} from './../../shared/attributes/index';
import {ProductTypeVO, ProductTypeAttrVO, AttributeVO, ProductTypeAttrNavigationRangeVO, Pair} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {I18nComponent} from './../../shared/i18n/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {YcValidators} from './../../shared/validation/validators';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-product-type-attribute',
  moduleId: module.id,
  templateUrl: 'product-type-attribute.component.html',
  directives: [PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, I18nComponent, ProductAttributeSelectComponent]
})

export class ProductTypeAttributeComponent implements OnInit, OnChanges {

  @Input() masterObject:ProductTypeVO;

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;


  _objectAttributes:Array<ProductTypeAttrVO>;
  objectAttributesRemove:Array<number>;
  _attributeFilter:string;
  filteredObjectAttributes:Array<ProductTypeAttrVO>;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  selectedRow:ProductTypeAttrVO;

  attributeToEdit:ProductTypeAttrVO;
  enableEditRanges:boolean = false;
  rangeNav:boolean = false;
  showRanges:boolean = false;
  editRanges:boolean = false;
  duplicateAttribute:boolean = false;

  @Output() dataSelected: EventEmitter<ProductTypeAttrVO> = new EventEmitter<ProductTypeAttrVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<ProductTypeAttrVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<ProductTypeAttrVO, boolean>>>>();

  /**
   * Construct attribute panel
   */
  constructor() {
    console.debug('ProductTypeAttributeComponent constructed');

    this.attributeToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set objectAttributes(objectAttributes:Array<ProductTypeAttrVO>) {
    this._objectAttributes = objectAttributes;
    this.loadData();
  }

  get objectAttributes():Array<ProductTypeAttrVO> {
    return this._objectAttributes;
  }

  @Input()
  set attributeFilter(attributeFilter:string) {
    this._attributeFilter = attributeFilter;
    this.delayedFiltering.delay();
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ProductTypeAttributeComponent ngOnInit type', this.masterObject);
  }

  ngOnChanges(changes:any) {
    console.debug('ProductTypeAttributeComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }

  private loadData() {
    if (this.masterObject && this._objectAttributes) {

      console.debug('ProductTypeAttributeComponent attributes', this._objectAttributes);
      this.objectAttributesRemove = [];
      this.filterAttributes();

    } else {

      this.objectAttributesRemove = null;
      this.filteredObjectAttributes = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }

  public onRowAdd() {
    console.debug('ProductTypeAttributeComponent onRowAdd handler');

    var _attr:ProductTypeAttrVO = {
      productTypeAttrId: 0, producttypeId: this.masterObject.producttypeId,
      attribute:null, rank:500,
      visible:true, similarity:false, store:false, search:false, primary:false, navigation:false,
      navigationType:null, rangeNavigation: { ranges: [] }
    };

    this.onRowEdit(_attr);

  }

  protected onNewAttributeSelected(event:AttributeVO) {
    let idx = this._objectAttributes.findIndex(attr =>
      attr.attribute.code === event.code
    );
    this.duplicateAttribute = idx != -1;
    if (!this.duplicateAttribute) {
      this.attributeToEdit.attribute = event;
      this.attributeToEdit.store = event.store;
      this.attributeToEdit.search = event.search;
      this.attributeToEdit.primary = event.primary;
      this.attributeToEdit.navigation = event.navigation;
      if (this.attributeToEdit.navigation) {
        this.attributeToEdit.navigationType = 'S'; // single by default
      }
      this.changed = true;
      this.validForSave = true;
    }
  }

  protected onRowDelete(row:ProductTypeAttrVO) {
    console.debug('ProductTypeAttributeComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:ProductTypeAttrVO) {
    console.debug('ProductTypeAttributeComponent onRowEdit handler', row);
    this.validForSave = false;
    this.enableEditRanges = false;
    this.attributeToEdit = Util.clone(row);
    this.rangeNav = this.attributeToEdit.navigation && this.attributeToEdit.navigationType === 'R';
    this.showRanges = this.rangeNav;
    this.editRanges = this.enableEditRanges;
    this.editModalDialog.show();
  }

  public onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onSelectRow(row:ProductTypeAttrVO) {
    console.debug('ProductTypeAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  onDataChange(event:any) {

    this.validForSave = false;

    let navRange = this.attributeToEdit.navigation && this.attributeToEdit.navigationType === 'R';
    if (navRange) {
      if (!this.rangeNav) { // just changed to nav range
        this.rangeNav = true;
        this.enableEditRanges = false;
        this.showRanges = true;
        this.editRanges = false;
      }

    } else {
      this.showRanges = false;
      this.editRanges = false;
      this.rangeNav = false;
    }

    let rank = this.attributeToEdit.rank;
    this.validForSave = !isNaN(rank);

    console.debug('ProductTypeAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  private processDataChangesEvent() {

    console.debug('ProductTypeAttributeComponent data changes', this.masterObject);
    if (this.masterObject && this._objectAttributes) {

      let _update = <Array<Pair<ProductTypeAttrVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
      });

      console.debug('ProductTypeAttributeComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ProductTypeAttributeComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.productTypeAttrId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.attribute.code === this.selectedRow.attribute.code;
        });
        this._objectAttributes.splice(idx, 1);
        this.objectAttributes = this._objectAttributes.slice(0, this._objectAttributes.length);  // reset to propagate changes
        console.debug('ProductTypeAttributeComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        console.debug('ProductTypeAttributeComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.objectAttributesRemove.push(attrToDelete);
      }
      this.filterAttributes();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    console.debug('ProductTypeAttributeComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.productTypeAttrId === 0) { // add new
        console.debug('ProductTypeAttributeComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.attributeToEdit.attribute.code;} );
        if (idx != -1) {
          this._objectAttributes[idx] = this.attributeToEdit;
        } else {
          this._objectAttributes.push(this.attributeToEdit);
        }
      } else { // edit existing
        console.debug('ProductTypeAttributeComponent onEditModalResult update existing', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.productTypeAttrId === this.attributeToEdit.productTypeAttrId;} );
        this._objectAttributes[idx] = this.attributeToEdit;
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    }
  }

  private filterAttributes() {
    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    let _si = this._attributeFilter === '#SI';
    let _vis = this._attributeFilter === '+V';
    let _inv = this._attributeFilter === '-V';
    if (_vis || _inv) {
      this.filteredObjectAttributes = this._objectAttributes.filter(val =>
        (_vis && val.visible) || (_inv && !val.visible)
      );
      console.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else if (_si) {
      this.filteredObjectAttributes = this._objectAttributes.filter(val =>
        val.store || val.search || val.primary || val.navigation
      );
      console.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else if (_filter) {
      this.filteredObjectAttributes = this._objectAttributes.filter(val =>
        val.attribute.code.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.name.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.description && val.attribute.description.toLowerCase().indexOf(_filter) !== -1
      );
      console.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else {
      this.filteredObjectAttributes = this._objectAttributes;
      console.debug('ProductTypeAttributeComponent filterAttributes no filter', this.filteredObjectAttributes);
    }

    if (this.filteredObjectAttributes === null) {
      this.filteredObjectAttributes = [];
    }

    let _total = this.filteredObjectAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  isRemovedAttribute(row:ProductTypeAttrVO):boolean {
    return this.objectAttributesRemove.indexOf(row.productTypeAttrId) !== -1;
  }

  isNewAttribute(row:ProductTypeAttrVO):boolean {
    return row.productTypeAttrId == 0;
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

  protected getSearchFlags(row:ProductTypeAttrVO) {
    let flags = '';
    if (row.store) {
      flags += '<i class="fa fa-save"></i>&nbsp;';
    }
    if (row.search) {
      if (row.primary) {
        flags += '<i class="fa fa-search-plus"></i>&nbsp;';
      } else {
        flags += '<i class="fa fa-search"></i>&nbsp;';
      }
    }
    if (row.navigation) {
      if (row.navigationType === 'R') {
        flags += '<i class="fa fa-sliders"></i>&nbsp;';
      } else {
        flags += '<i class="fa fa-list-alt"></i>&nbsp;';
      }
    }
    return flags;
  }

  protected getAttrFlags(row:ProductTypeAttrVO) {
    let flags = '';
    if (row.visible) {
      flags += '<i class="fa fa-eye"></i>&nbsp;';
    }
    if (row.similarity) {
      flags += '<i class="fa fa-copy"></i>&nbsp;';
    }
    return flags;
  }

  protected onToggleEditRanges() {
    this.enableEditRanges = !this.enableEditRanges;
    this.showRanges = !this.enableEditRanges;
    this.editRanges = this.enableEditRanges;
  }

  protected onNavRangeAdd() {
    if (this.attributeToEdit.rangeNavigation == null) {
      this.attributeToEdit.rangeNavigation = {ranges: []};
    }
    if (this.attributeToEdit.rangeNavigation.ranges == null) {
      this.attributeToEdit.rangeNavigation.ranges = [];
    }
    var _range:ProductTypeAttrNavigationRangeVO = { range: '0-99', displayVals: null };
    this.attributeToEdit.rangeNavigation.ranges.push(_range);
    this.onDataChange(_range);
  }

  protected onNavRangeDelete(range:ProductTypeAttrNavigationRangeVO) {
    let idx = this.attributeToEdit.rangeNavigation.ranges.indexOf(range);
    this.attributeToEdit.rangeNavigation.ranges.splice(idx, 1);
    this.attributeToEdit.rangeNavigation.ranges = this.attributeToEdit.rangeNavigation.ranges.slice(0, this.attributeToEdit.rangeNavigation.ranges.length);
    this.onDataChange(this.attributeToEdit.rangeNavigation.ranges);
  }

}
