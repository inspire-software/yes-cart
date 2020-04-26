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
import { ProductSkuVO, ProductWithLinksVO, ProductOptionVO, AttributeVO, Pair } from './../../shared/model/index';
import { Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { ProductSelectComponent } from './../../shared/catalog/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-product-options',
  moduleId: module.id,
  templateUrl: 'product-options.component.html',
})

export class ProductOptionsComponent implements OnInit, OnChanges {

  @Input() showHelp:boolean = false;

  @Output() dataSelected: EventEmitter<ProductOptionVO> = new EventEmitter<ProductOptionVO>();

  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<ProductOptionVO>>> = new EventEmitter<FormValidationEvent<Array<ProductOptionVO>>>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private options:Array<ProductSkuVO> = null;

  private _masterObject:ProductWithLinksVO;

  //sorting
  private sortColumn:string = 'rank';
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;


  private _objectOptions:Array<ProductOptionVO>;
  private _optionFilter:string;
  private filteredObjectOptions:Array<ProductOptionVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  @ViewChild('addModalDialog')
  private addModalDialog:ModalComponent;

  private selectedAttribute:AttributeVO;
  private selectedRow:ProductOptionVO;
  private selectedOptionSku:ProductSkuVO;

  private optionToEdit:ProductOptionVO;

  @ViewChild('productSelectDialog')
  private productSelectDialog:ProductSelectComponent;


  /**
   * Construct option panel
   */
  constructor() {
    LogUtil.debug('ProductOptionsComponent constructed');

    this.optionToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterOptions();
    }, this.delayedFilteringMs);

  }

  @Input()
  set masterObject(master:ProductWithLinksVO) {
    this._masterObject = master;
    this.loadData();
  }

  @Input()
  set optionFilter(optionFilter:string) {
    this._optionFilter = optionFilter;
    this.delayedFiltering.delay();
  }

  @Input()
  set sortorder(sort:Pair<string, boolean>) {
    if (sort != null && (sort.first !== this.sortColumn || sort.second !== this.sortDesc)) {
      this.sortColumn = sort.first;
      this.sortDesc = sort.second;
      this.delayedFiltering.delay();
    }
  }

  get masterObject():ProductWithLinksVO {
    return this._masterObject;
  }

  ngOnInit() {
    LogUtil.debug('ProductOptionsComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ProductOptionsComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }


  onRowAdd() {
    LogUtil.debug('ProductOptionsComponent onRowAdd');
    this.addModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }


  protected onAttributeSelected(row:AttributeVO) {
    this.selectedAttribute = row;
  }

  protected onAttributeAddModalResult(modalresult: ModalResult) {
    LogUtil.debug('AttributeValuesComponent onAttributeAddModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.optionToEdit = {
        productoptionId: 0, rank: 0,
        productId: this.masterObject.productId,
        quantity: 1, mandatory: false, skuCode: null,
        attributeCode: this.selectedAttribute.code, optionSkuCodes: []
      };

      this.onRowEdit(this.optionToEdit);

    } else {
      this.selectedAttribute = null;
    }
  }


  protected onRowDelete(row:ProductOptionVO) {
    LogUtil.debug('ProductOptionsComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowEdit(row:ProductOptionVO) {
    LogUtil.debug('ProductOptionsComponent onRowEdit handler', row);
    this.validForSave = false;
    this.optionToEdit = Util.clone(row);
    this.editModalDialog.show();
  }

  protected onOptionSkuSelected(row:ProductSkuVO) {
    this.selectedOptionSku = row;
  }

  protected onSelectRow(row:ProductOptionVO) {
    LogUtil.debug('ProductOptionsComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  protected onDataChange(event:any) {

    this.validForSave = this.optionToEdit.attributeCode != null && this.optionToEdit.attributeCode != ''
      && this.optionToEdit.optionSkuCodes != null && this.optionToEdit.optionSkuCodes.length > 0;
    LogUtil.debug('ProductOptionsComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);

  }


  protected onSearchProduct() {
    if (this.optionToEdit != null && this.optionToEdit.productoptionId <= 0) {
      this.productSelectDialog.showDialog();
    }
  }



  protected onProductSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('ProductOptionsComponent onProductSelected', event);
    if (event.valid && this.optionToEdit != null && this.optionToEdit.productoptionId <= 0) {
      if (this.optionToEdit.optionSkuCodes.findIndex(val => val.first === event.source.code) === -1 &&
          this.options.findIndex(val => val.code === event.source.code) === -1) {
        this.optionToEdit.optionSkuCodes.push({first: event.source.code, second: event.source.name});
        LogUtil.debug('ProductOptionsComponent onProductSelected', this.optionToEdit);
        this.onDataChange(event);
      }
    }
  }

  protected onOptionSkuRemove(event:Pair<string, string>) {
    LogUtil.debug('ProductOptionsComponent onOptionSkuRemove', event);
    if (event != null && this.optionToEdit != null) {
      this.optionToEdit.optionSkuCodes = this.optionToEdit.optionSkuCodes.filter(val =>
         val.first !== event.first
      );
      LogUtil.debug('ProductOptionsComponent onOptionSkuRemove', this.optionToEdit);
      this.onDataChange(event);
    }
  }


  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductOptionsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.productoptionId;
      if (attrToDelete == 0) {
        let idx = this._objectOptions.findIndex(attrVo => {
          return attrVo.skuCode === this.selectedRow.skuCode && attrVo.attributeCode === this.selectedRow.attributeCode;
        });
        if (idx != -1) {
          this._objectOptions.splice(idx, 1);
        }
        LogUtil.debug('ProductOptionsComponent onDeleteConfirmationResult option ' + attrToDelete);
      } else {
        let idx = this._objectOptions.findIndex(attrVo => {
          return attrVo.productoptionId === attrToDelete;
        });
        if (idx != -1) {
          this._objectOptions.splice(idx, 1);
        }
        LogUtil.debug('ProductOptionsComponent onDeleteConfirmationResult option ' + attrToDelete);
      }
      this.filterOptions();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    } else {
      this.optionToEdit = null;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ProductOptionsComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.optionToEdit.productoptionId === 0) { // add new
        LogUtil.debug('ProductOptionsComponent onEditModalResult add new option', this._objectOptions);
        let idx = this._objectOptions.findIndex(attrVo => {
          return attrVo.skuCode === this.optionToEdit.skuCode && attrVo.attributeCode === this.optionToEdit.attributeCode;
        });
        if (idx != -1) {
          this._objectOptions[idx] = this.optionToEdit;
        } else {
          this._objectOptions.push(this.optionToEdit);
        }
      } else { // edit existing
        LogUtil.debug('ProductOptionsComponent onEditModalResult update existing', this._objectOptions);
        let idx = this._objectOptions.findIndex(attrVo =>  {
          return attrVo.productoptionId === this.optionToEdit.productoptionId;
        });
        this._objectOptions[idx] = this.optionToEdit;
      }
      this.selectedRow = this.optionToEdit;
      this.changed = true;
      this.filterOptions();
      this.processDataChangesEvent();
    } else {
      this.optionToEdit = null;
    }
  }

  protected resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onPageChanged(event:any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
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
        this.sortColumn = 'rank';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterOptions();
    this.sortSelected.emit({ first: this.sortColumn, second: this.sortDesc });
  }

  protected getSkuName(row:ProductOptionVO):string {
    if (row != null && row.skuCode != null && this.options != null) {
      let idx = this.options.findIndex(sku => {
        return sku.code == row.skuCode;
      });
      if (idx != -1) {
        return this.options[idx].name;
      }
    }
    return '';
  }

  private loadData() {
    if (this.masterObject) {

      this._objectOptions = this.masterObject.configurationOptions;
      this.options = this.masterObject.sku;

      LogUtil.debug('ProductOptionsComponent options', this._objectOptions);
      this.filterOptions();

    } else {

      this.filteredObjectOptions = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }


  private filterOptions() {
    let _filter = this._optionFilter ? this._optionFilter.toLowerCase() : null;
    let _filteredObjectOptions:Array<ProductOptionVO> = [];
    if (this._objectOptions) {
      if (_filter) {
        if (_filter.indexOf('#') === 0) {

          let _type = _filter.substr(1);

          _filteredObjectOptions = this._objectOptions.filter(val =>
            val.attributeCode.toLowerCase().indexOf(_type) !== -1
          );
        } else {
          _filteredObjectOptions = this._objectOptions.filter(val => {
              if (val.skuCode != null && val.skuCode.toLowerCase().indexOf(_filter) !== -1) {
                return true;
              }
              return val.optionSkuCodes.findIndex(val =>
                val.first.toLowerCase().indexOf(_filter) !== -1
              ) !== -1;
            }
          );
        }
        LogUtil.debug('ProductOptionsComponent filterOptions ' + _filter, _filteredObjectOptions);
      } else {
        _filteredObjectOptions = this._objectOptions.slice(0, this._objectOptions.length);
        LogUtil.debug('ProductOptionsComponent filterOptions no filter', _filteredObjectOptions);
      }
    }

    if (_filteredObjectOptions === null) {
      _filteredObjectOptions = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    _filteredObjectOptions.sort(_sort);

    this.filteredObjectOptions = _filteredObjectOptions;

    let _total = this.filteredObjectOptions.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


  private processDataChangesEvent() {

    LogUtil.debug('ProductOptionsComponent data changes', this.masterObject);
    if (this.masterObject && this._objectOptions) {

      LogUtil.debug('ProductOptionsComponent data changes update', this._objectOptions);

      this.dataChanged.emit({ source: this._objectOptions, valid: true });

    }

  }

}
