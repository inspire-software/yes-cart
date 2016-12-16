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
import { PaymentGatewayVO, PaymentGatewayParameterVO, Pair } from './../../../shared/model/index';
import { Util } from './../../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../../shared/modal/index';
import { FormValidationEvent, Futures, Future } from './../../../shared/event/index';
import { Config } from './../../../shared/config/env.config';
import { LogUtil } from './../../../shared/log/index';


@Component({
  selector: 'yc-parameter-values',
  moduleId: module.id,
  templateUrl: 'parameter-values.component.html',
})

export class ParameterValuesComponent implements OnInit, OnChanges {

  @Output() dataSelected: EventEmitter<PaymentGatewayParameterVO> = new EventEmitter<PaymentGatewayParameterVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<PaymentGatewayParameterVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<PaymentGatewayParameterVO, boolean>>>>();

  private _paymentGateway:PaymentGatewayVO;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;


  private _objectAttributes:Array<PaymentGatewayParameterVO>;
  private objectAttributesRemove:Array<number>;
  private objectAttributesEdit:Array<number>;
  private _attributeFilter:string;
  private filteredObjectAttributes:Array<PaymentGatewayParameterVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  private selectedRow:PaymentGatewayParameterVO;

  private attributeToEdit:PaymentGatewayParameterVO;
  private expandDefault:boolean = true;

  /**
   * Construct attribute panel
   */
  constructor() {
    LogUtil.debug('ParameterValuesComponent constructed');

    this.attributeToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set paymentGateway(paymentGateway:PaymentGatewayVO) {
    LogUtil.debug('ParameterValuesComponent changed', paymentGateway);
    this._paymentGateway = paymentGateway;
    if (this._paymentGateway != null) {
      this._objectAttributes = this._paymentGateway.parameters.slice(0, this._paymentGateway.parameters.length);
    } else {
      this._objectAttributes = null;
    }
    this.loadData();
  }

  get paymentGateway():PaymentGatewayVO {
    return this._paymentGateway;
  }

  @Input()
  set attributeFilter(attributeFilter:string) {
    this._attributeFilter = attributeFilter;
    this.delayedFiltering.delay();
  }

  ngOnInit() {
    LogUtil.debug('ParameterValuesComponent ngOnInit shop', this._paymentGateway);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ParameterValuesComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }

  onRowAdd() {
    this.onRowEdit(this.newParamInstance());
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  /**
   * Row delete handler.
   * @param row attribute to delete.
   */
  protected onRowDelete(row:PaymentGatewayParameterVO) {
    LogUtil.debug('ParameterValuesComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowEdit(row:PaymentGatewayParameterVO) {
    LogUtil.debug('ParameterValuesComponent onRowEdit handler', row);
    this.validForSave = false;
    this.attributeToEdit = Util.clone(row);
    this.editModalDialog.show();
  }

  newParamInstance():PaymentGatewayParameterVO {
    return { paymentGatewayParameterId: 0, description: '', label:'', name: '', value: '', pgLabel:null};
  }

  protected onSelectRow(row:PaymentGatewayParameterVO) {
    LogUtil.debug('ParameterValuesComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  protected onDataChange(event:any) {

    let val = this.attributeToEdit.value;
    let typ = 'Default';

    switch (typ) {
      case 'Integer':
        this.validForSave = /^[\-]?[0-9]+$/.test(val);
        break;
      case 'Float':
        this.validForSave = /^(([\-][0-9]+)|([0-9]*))[\.]?[0-9]+$/.test(val);
        break;
      case 'Phone':
        this.validForSave = /^[\+]?[\(\)0-9\- ]+$/.test(val);
        break;
      case 'Email':
        this.validForSave = /^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/.test(val);
        break;
      case 'Date':
        this.validForSave = /^[0-9]{4}\-([0][1-9]|[1][0-2])\-([0][1-9]|[1-2][0-9]|[3][0-1])( ([0][0-9]|[1][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9])?$/.test(val);
        break;
      case 'Any':
        this.validForSave = true;
        break;
      default:
        this.validForSave = val != null && !(/^\s*$/.test(val));
        break;
    }

    if (this.validForSave && this.attributeToEdit.paymentGatewayParameterId == 0) {
      let name = this.attributeToEdit.name;
      let label = this.attributeToEdit.label;
      this.validForSave = name != null && !(/^\s*$/.test(name)) && label != null && !(/^\s*$/.test(label));
    }

    LogUtil.debug('ParameterValuesComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ParameterValuesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.paymentGatewayParameterId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.label === this.selectedRow.label;
        });
        this._objectAttributes[idx].value = null;
        LogUtil.debug('ParameterValuesComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        LogUtil.debug('ParameterValuesComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.objectAttributesRemove.push(attrToDelete);
        this.objectAttributesEdit.push(attrToDelete);
      }
      this.filterAttributes();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ParameterValuesComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.paymentGatewayParameterId === 0) { // add new
        LogUtil.debug('ParameterValuesComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.label === this.attributeToEdit.label;} );
        if (idx == -1) {
          this._objectAttributes.push(this.attributeToEdit);
        } else {
          this._objectAttributes[idx] = this.attributeToEdit;
        }
      } else { // edit existing
        LogUtil.debug('ParameterValuesComponent onEditModalResult update existing', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.paymentGatewayParameterId === this.attributeToEdit.paymentGatewayParameterId;} );
        this._objectAttributes[idx] = this.attributeToEdit;
        this.objectAttributesEdit.push(this.attributeToEdit.paymentGatewayParameterId);
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  protected isRemovedAttribute(row:PaymentGatewayParameterVO):boolean {
    return this.objectAttributesRemove.indexOf(row.paymentGatewayParameterId) !== -1;
  }

  protected isEditedAttribute(row:PaymentGatewayParameterVO):boolean {
    return this.objectAttributesEdit.indexOf(row.paymentGatewayParameterId) !== -1;
  }

  protected isNewAttribute(row:PaymentGatewayParameterVO):boolean {
    return row.paymentGatewayParameterId == 0 && row.value != null;
  }


  protected getAttributeColor(row:PaymentGatewayParameterVO, removed:string, edited:string, added:string, prestine:string) {

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

  protected resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected getDisplayValue(row:PaymentGatewayParameterVO):string {
    if (row.value != null) {
      var _str:string = '' + row.value;
      if (_str  === 'true') {
        return '<i class="fa fa-check-circle"></i>';
      } else if (_str  === 'false') {
        return '<i class="fa fa-times-circle"></i>';
      } else if (_str.indexOf('<') !== -1) {
        return '<pre>' + _str + '</pre>';
      }
      return row.value;
    }
    return '&nbsp;';
  }

  protected onExpandDefault() {
    this.expandDefault = !this.expandDefault;
  }

  private loadData() {
    if (this._objectAttributes) {

      LogUtil.debug('ParameterValuesComponent attributes', this._objectAttributes);
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

  private filterAttributes() {
    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    if (_filter) {
      if (_filter === '###') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.value != null && val.value != '' && val.paymentGatewayParameterId > 0
        );
      } else if (_filter === '##0') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.value != null && val.value != ''
        );
      } else if (_filter === '#00') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.value != null && val.value != '' && val.paymentGatewayParameterId == 0
        );
      } else if (_filter === '#0#') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          this.isEditedAttribute(val) || (val.paymentGatewayParameterId == 0 && val.value != null && val.value != '')
        );
      } else {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.label.toLowerCase().indexOf(_filter) !== -1 ||
          val.name && val.name.toLowerCase().indexOf(_filter) !== -1 ||
          val.description && val.description.toLowerCase().indexOf(_filter) !== -1 ||
          val.value && val.value.toLowerCase().indexOf(_filter) !== -1
        );
      }
      LogUtil.debug('ParameterValuesComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else {
      this.filteredObjectAttributes = this._objectAttributes;
      LogUtil.debug('ParameterValuesComponent filterAttributes no filter', this.filteredObjectAttributes);
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

  private processDataChangesEvent() {

    LogUtil.debug('ParameterValuesComponent data changes', this._paymentGateway);
    if (this._paymentGateway && this._objectAttributes) {

      let _update = <Array<Pair<PaymentGatewayParameterVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        if (attr.paymentGatewayParameterId !== 0 || (attr.value !== null && /\S+.*\S+/.test(attr.value))) {
          _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
        }
      });

      LogUtil.debug('ParameterValuesComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }

}
