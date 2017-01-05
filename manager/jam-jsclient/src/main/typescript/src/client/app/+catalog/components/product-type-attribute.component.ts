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
import { Component, OnInit, OnChanges, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { ProductTypeVO, ProductTypeAttrVO, AttributeVO, ProductTypeAttrNavigationRangeVO, Pair } from './../../shared/model/index';
import { Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-product-type-attribute',
  moduleId: module.id,
  templateUrl: 'product-type-attribute.component.html',
})

export class ProductTypeAttributeComponent implements OnInit, OnChanges {

  @Input() masterObject:ProductTypeVO;

  @Output() dataSelected: EventEmitter<ProductTypeAttrVO> = new EventEmitter<ProductTypeAttrVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<ProductTypeAttrVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<ProductTypeAttrVO, boolean>>>>();

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;


  private _objectAttributes:Array<ProductTypeAttrVO>;
  private objectAttributesRemove:Array<number>;
  private objectAttributesEdit:Array<number>;
  private _attributeFilter:string;
  private filteredObjectAttributes:Array<ProductTypeAttrVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  private selectedRow:ProductTypeAttrVO;

  private attributeToEdit:ProductTypeAttrVO;
  private enableEditRanges:boolean = false;
  private rangeNav:boolean = false;
  private showRanges:boolean = false;
  private editRanges:boolean = false;
  private duplicateAttribute:boolean = false;

  /**
   * Construct attribute panel
   */
  constructor() {
    LogUtil.debug('ProductTypeAttributeComponent constructed');

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
    LogUtil.debug('ProductTypeAttributeComponent ngOnInit type', this.masterObject);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ProductTypeAttributeComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }

  public onRowAdd() {
    LogUtil.debug('ProductTypeAttributeComponent onRowAdd handler');

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
    LogUtil.debug('ProductTypeAttributeComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:ProductTypeAttrVO) {
    LogUtil.debug('ProductTypeAttributeComponent onRowEdit handler', row);
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
    LogUtil.debug('ProductTypeAttributeComponent onSelectRow handler', row);
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

    LogUtil.debug('ProductTypeAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeAttributeComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.productTypeAttrId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.attribute.code === this.selectedRow.attribute.code;
        });
        this._objectAttributes.splice(idx, 1);
        this.objectAttributes = this._objectAttributes.slice(0, this._objectAttributes.length);  // reset to propagate changes
        LogUtil.debug('ProductTypeAttributeComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        LogUtil.debug('ProductTypeAttributeComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.objectAttributesRemove.push(attrToDelete);
        this.objectAttributesEdit.push(attrToDelete);
      }
      this.filterAttributes();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeAttributeComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.productTypeAttrId === 0) { // add new
        LogUtil.debug('ProductTypeAttributeComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.attributeToEdit.attribute.code;} );
        if (idx != -1) {
          this._objectAttributes[idx] = this.attributeToEdit;
        } else {
          this._objectAttributes.push(this.attributeToEdit);
        }
      } else { // edit existing
        LogUtil.debug('ProductTypeAttributeComponent onEditModalResult update existing', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.productTypeAttrId === this.attributeToEdit.productTypeAttrId;} );
        this._objectAttributes[idx] = this.attributeToEdit;
        this.objectAttributesEdit.push(this.attributeToEdit.productTypeAttrId);
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  isRemovedAttribute(row:ProductTypeAttrVO):boolean {
    return this.objectAttributesRemove.indexOf(row.productTypeAttrId) !== -1;
  }

  isEditedAttribute(row:ProductTypeAttrVO):boolean {
    return this.objectAttributesEdit.indexOf(row.productTypeAttrId) !== -1;
  }

  isNewAttribute(row:ProductTypeAttrVO):boolean {
    return row.productTypeAttrId == 0;
  }


  getAttributeColor(row:ProductTypeAttrVO, removed:string, edited:string, added:string, prestine:string) {

    if (this.isRemovedAttribute(row)) {
      return removed;
    }

    if (this.isEditedAttribute(row)) {
      return edited;
    }

    if (this.isNewAttribute(row)) {
      return added;
    }

    return prestine;
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

  private loadData() {
    if (this.masterObject && this._objectAttributes) {

      LogUtil.debug('ProductTypeAttributeComponent attributes', this._objectAttributes);
      this.objectAttributesRemove = [];
      this.objectAttributesEdit = [];
      this.filterAttributes();

    } else {

      this.objectAttributesRemove = null;
      this.objectAttributesEdit = null;
      this.filteredObjectAttributes = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }

  private processDataChangesEvent() {

    LogUtil.debug('ProductTypeAttributeComponent data changes', this.masterObject);
    if (this.masterObject && this._objectAttributes) {

      let _update = <Array<Pair<ProductTypeAttrVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
      });

      LogUtil.debug('ProductTypeAttributeComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }


  private filterAttributes() {
    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    let _si = this._attributeFilter === '#SI';
    let _vis = this._attributeFilter === '+V';
    let _inv = this._attributeFilter === '-V';
    let _delta = this._attributeFilter === '###';
    let _filteredObjectAttributes:Array<ProductTypeAttrVO> = [];
    if (_vis || _inv) {
      _filteredObjectAttributes = this._objectAttributes.filter(val =>
        (_vis && val.visible) || (_inv && !val.visible)
      );
      LogUtil.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, _filteredObjectAttributes);
    } else if (_si) {
      _filteredObjectAttributes = this._objectAttributes.filter(val =>
        val.store || val.search || val.primary || val.navigation
      );
      LogUtil.debug('ProductTypeAttributeComponent filterAttributes ' + _filter, _filteredObjectAttributes);
    } else if (_delta) {
      _filteredObjectAttributes = this._objectAttributes.filter(val =>
        this.isEditedAttribute(val) || this.isRemovedAttribute(val) || this.isNewAttribute(val)
      );
      LogUtil.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, _filteredObjectAttributes);
    } else if (_filter) {
      _filteredObjectAttributes = this._objectAttributes.filter(val =>
        val.attribute.code.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.name.toLowerCase().indexOf(_filter) !== -1 ||
        val.attribute.description && val.attribute.description.toLowerCase().indexOf(_filter) !== -1
      );
      LogUtil.debug('ProductTypeAttributeComponent filterAttributes ' +  _filter, _filteredObjectAttributes);
    } else {
      _filteredObjectAttributes = this._objectAttributes;
      LogUtil.debug('ProductTypeAttributeComponent filterAttributes no filter', _filteredObjectAttributes);
    }

    if (_filteredObjectAttributes === null) {
      _filteredObjectAttributes = [];
    }

    _filteredObjectAttributes.sort(function(a, b) {
      var rank:number = a.rank - b.rank;
      if (rank == 0) {
        return a.attribute.name > b.attribute.name ? 1 : -1;
      }
      return rank;
    });

    this.filteredObjectAttributes = _filteredObjectAttributes;

    let _total = this.filteredObjectAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
