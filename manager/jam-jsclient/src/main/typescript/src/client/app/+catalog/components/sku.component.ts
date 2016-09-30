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
import {Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {ProductSkuVO, AttrValueProductSkuVO, Pair, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {I18nComponent} from './../../shared/i18n/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-sku',
  moduleId: module.id,
  templateUrl: 'sku.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, I18nComponent],
})

export class SKUComponent implements OnInit, OnDestroy {

  _sku:ProductSkuVO;
  avPrototype:AttrValueProductSkuVO;
  _attributes:AttrValueProductSkuVO[] = [];
  attributeFilter:string;

  _changes:Array<Pair<AttrValueProductSkuVO, boolean>>;

  selectedRow:AttrValueProductSkuVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductSkuVO, Array<Pair<AttrValueProductSkuVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductSkuVO, Array<Pair<AttrValueProductSkuVO, boolean>>>>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  skuForm:any;
  skuFormSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  private searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    console.debug('SKUComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (!that.changed || uri == null || uri == '' || that._sku == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'sku', subjectId: that._sku.skuId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validGuid = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._sku == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'sku', subjectId: that._sku.skuId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._sku == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'sku', subjectId: that._sku.skuId, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.skuForm = fb.group({
      'guid': ['', validGuid],
      'code': ['', validCode],
      'manufacturerCode': ['', YcValidators.noWhitespace],
      'barCode': ['', YcValidators.noWhitespace],
      'rank': ['', YcValidators.requiredRank],
      'description': [''],
      'uri': ['', validUri],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.skuForm.controls) {
      this.skuForm.controls[key]['_pristine'] = true;
      this.skuForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.skuFormSub = this.skuForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.skuForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.skuFormSub) {
      console.debug('SKUComponent unbining form');
      this.skuFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('SKUComponent validating formGroup is valid: ' + this.validForSave, this._sku);
    this.dataChanged.emit({ source: new Pair(this._sku, this._changes), valid: this.validForSave });
  }

  @Input()
  set sku(sku:ProductSkuVO) {
    this._sku = sku;
    if (this._sku != null) {
      this.avPrototype = {
        attrvalueId: 0,
        val: null,
        displayVals: [],
        valBase64Data: null,
        attribute: null,
        skuId: this._sku.skuId
      }
    } else {
      this.avPrototype = null;
    }
    this.changed = false;
    this.formReset();
  }

  get sku():ProductSkuVO {
    return this._sku;
  }

  @Input()
  set attributes(attributes:AttrValueProductSkuVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueProductSkuVO[] {
    return this._attributes;
  }

  onMainDataChange(event:any) {
    console.debug('SKUComponent main data changed', this._sku);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('SKUComponent attr data changed', this._sku);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('SKUComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('SKUComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('SKUComponent tabSelected', tab);
  }

  protected onRowAdd() {
    this.attributeValuesComponent.onRowAdd();
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

  protected onSelectRow(row:AttrValueProductSkuVO) {
    console.debug('SKUComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
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


}
