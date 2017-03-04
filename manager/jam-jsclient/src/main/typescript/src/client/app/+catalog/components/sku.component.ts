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
import { Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ProductSkuVO, AttrValueProductSkuVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-sku',
  moduleId: module.id,
  templateUrl: 'sku.component.html',
})

export class SKUComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductSkuVO, Array<Pair<AttrValueProductSkuVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductSkuVO, Array<Pair<AttrValueProductSkuVO, boolean>>>>>();

  private _sku:ProductSkuVO;
  private avPrototype:AttrValueProductSkuVO;
  private _attributes:AttrValueProductSkuVO[] = [];
  private attributeFilter:string;

  private _changes:Array<Pair<AttrValueProductSkuVO, boolean>>;

  private selectedRow:AttrValueProductSkuVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private skuForm:any;
  private skuFormSub:any; // tslint:disable-line:no-unused-variable

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  private searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('SKUComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (uri == null || uri == '' || that._sku == null || !that.skuForm || (!that.skuForm.dirty && that._sku.skuId > 0)) {
        return null;
      }

      let basic = YcValidators.validSeoUri(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'sku', subjectId: that._sku.skuId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validGuid = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._sku == null || !that.skuForm || (!that.skuForm.dirty && that._sku.skuId > 0)) {
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

      let basic = YcValidators.requiredValidCode(control);
      if (basic == null) {

        let code = control.value;
        if (that._sku == null || !that.skuForm || (!that.skuForm.dirty && that._sku.skuId > 0)) {
          return null;
        }

        var req:ValidationRequestVO = { subject: 'sku', subjectId: that._sku.skuId, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.skuForm = fb.group({
      'guid': ['', validGuid],
      'code': ['', validCode],
      'manufacturerCode': ['', YcValidators.noWhitespace],
      'manufacturerPartCode': ['', YcValidators.noWhitespace],
      'supplierCode': ['', YcValidators.noWhitespace],
      'supplierCatalogCode': ['', YcValidators.noWhitespace],
      'barCode': ['', YcValidators.noWhitespace],
      'rank': ['', YcValidators.requiredRank],
      'description': [''],
      'uri': ['', validUri],
      'name': [''],
      'title': [''],
      'keywords': [''],
      'meta': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'skuForm', 'skuFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'skuFormSub');
  }

  formChange():void {
    LogUtil.debug('SKUComponent formChange', this.skuForm.valid, this._sku);
    this.dataChanged.emit({ source: new Pair(this._sku, this._changes), valid: this.skuForm.valid });
  }

  @Input()
  set sku(sku:ProductSkuVO) {

    UiUtil.formInitialise(this, 'initialising', 'skuForm', '_sku', sku);
    this._changes = [];
    if (this._sku != null) {
      this.avPrototype = {
        attrvalueId: 0,
        val: null,
        displayVals: [],
        valBase64Data: null,
        attribute: null,
        skuId: this._sku.skuId
      };
    } else {
      this.avPrototype = null;
    }

  }

  get sku():ProductSkuVO {
    return this._sku;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'skuForm', 'name', event);
  }

  onTitleDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'skuForm', 'title', event);
  }

  onKeywordsDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'skuForm', 'keywords', event);
  }

  onMetaDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'skuForm', 'meta', event);
  }

  @Input()
  set attributes(attributes:AttrValueProductSkuVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueProductSkuVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('SKUComponent attr data changed', this._sku);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('SKUComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('SKUComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('SKUComponent tabSelected', tab);
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
    LogUtil.debug('SKUComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
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


}
