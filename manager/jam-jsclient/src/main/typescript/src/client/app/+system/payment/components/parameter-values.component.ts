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
import {PaginationComponent} from './../../../shared/pagination/index';
import {PaymentGatewayVO, PaymentGatewayParameterVO, Pair} from './../../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../../shared/services/index';
import {ModalComponent, ModalResult, ModalAction} from './../../../shared/modal/index';
import {YcValidators} from './../../../shared/validation/validators';
import {FormValidationEvent, Futures, Future} from './../../../shared/event/index';
import {Config} from './../../../shared/config/env.config';


@Component({
  selector: 'yc-parameter-values',
  moduleId: module.id,
  templateUrl: 'parameter-values.component.html',
  directives: [PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent]
})

export class ParameterValuesComponent implements OnInit, OnChanges {

  private _paymentGateway:PaymentGatewayVO;

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;


  _objectAttributes:Array<PaymentGatewayParameterVO>;
  objectAttributesRemove:Array<number>;
  _attributeFilter:string;
  filteredObjectAttributes:Array<PaymentGatewayParameterVO>;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  selectedRow:PaymentGatewayParameterVO;

  attributeToEdit:PaymentGatewayParameterVO;
  expandDefault:boolean = true;

  @Output() dataSelected: EventEmitter<PaymentGatewayParameterVO> = new EventEmitter<PaymentGatewayParameterVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<PaymentGatewayParameterVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<PaymentGatewayParameterVO, boolean>>>>();

  /**
   * Construct attribute panel
   */
  constructor() {
    console.debug('ParameterValuesComponent constructed');

    this.attributeToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set paymentGateway(paymentGateway:PaymentGatewayVO) {
    console.debug('ParameterValuesComponent changed', paymentGateway);
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

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ParameterValuesComponent ngOnInit shop', this._paymentGateway);
  }

  ngOnChanges(changes:any) {
    console.debug('ParameterValuesComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }

  private loadData() {
    if (this._objectAttributes) {

      console.debug('ParameterValuesComponent attributes', this._objectAttributes);
      this.objectAttributesRemove = [];
      this.filterAttributes();

    } else {

      this.objectAttributesRemove = null;
      this.filteredObjectAttributes = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }

  /**
   * Row delete handler.
   * @param row attribute to delete.
   */
  protected onRowDelete(row:PaymentGatewayParameterVO) {
    console.debug('ParameterValuesComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:PaymentGatewayParameterVO) {
    console.debug('ParameterValuesComponent onRowEdit handler', row);
    this.validForSave = false;
    this.attributeToEdit = Util.clone(row);
    this.editModalDialog.show();
  }

  newParamInstance():PaymentGatewayParameterVO {
    return { paymentGatewayParameterId: 0, description: '', label:'', name: '', value: '', pgLabel:null};
  }

  public onRowAdd() {
    this.onRowEdit(this.newParamInstance());
  }

  public onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onSelectRow(row:PaymentGatewayParameterVO) {
    console.debug('ParameterValuesComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  onDataChange(event:any) {

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

    console.debug('ParameterValuesComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  private processDataChangesEvent() {

    console.debug('ParameterValuesComponent data changes', this._paymentGateway);
    if (this._paymentGateway && this._objectAttributes) {

      let _update = <Array<Pair<PaymentGatewayParameterVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        if (attr.paymentGatewayParameterId !== 0 || (attr.value !== null && /\S+.*\S+/.test(attr.value))) {
          _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
        }
      });

      console.debug('ParameterValuesComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ParameterValuesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.paymentGatewayParameterId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.label === this.selectedRow.label;
        });
        this._objectAttributes[idx].value = null;
        console.debug('ParameterValuesComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        console.debug('ParameterValuesComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.objectAttributesRemove.push(attrToDelete);
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
    console.debug('ParameterValuesComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.paymentGatewayParameterId === 0) { // add new
        console.debug('ParameterValuesComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.label === this.attributeToEdit.label;} );
        if (idx == -1) {
          this._objectAttributes.push(this.attributeToEdit);
        } else {
          this._objectAttributes[idx] = this.attributeToEdit;
        }
      } else { // edit existing
        console.debug('ParameterValuesComponent onEditModalResult update existing', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.paymentGatewayParameterId === this.attributeToEdit.paymentGatewayParameterId;} );
        this._objectAttributes[idx] = this.attributeToEdit;
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  private filterAttributes() {
    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    if (_filter) {
      this.filteredObjectAttributes = this._objectAttributes.filter(val =>
        val.label.toLowerCase().indexOf(_filter) !== -1 ||
        val.name && val.name.toLowerCase().indexOf(_filter) !== -1 ||
        val.description && val.description.toLowerCase().indexOf(_filter) !== -1 ||
        val.value && val.value.toLowerCase().indexOf(_filter) !== -1
      );
      console.debug('ParameterValuesComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else {
      this.filteredObjectAttributes = this._objectAttributes;
      console.debug('ParameterValuesComponent filterAttributes no filter', this.filteredObjectAttributes);
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

  isRemovedAttribute(row:PaymentGatewayParameterVO):boolean {
    return this.objectAttributesRemove.indexOf(row.paymentGatewayParameterId) !== -1;
  }

  isNewAttribute(row:PaymentGatewayParameterVO):boolean {
    return row.paymentGatewayParameterId == 0 && row.value != null;
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

  public getDisplayValue(row:PaymentGatewayParameterVO):string {
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

  onExpandDefault() {
    this.expandDefault = !this.expandDefault;
  }

}
