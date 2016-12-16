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
import { ProductTypeVO, ProductTypeAttrVO, ProductTypeViewGroupVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { ProductTypeGroupComponent } from './product-type-group.component';
import { ProductTypeAttributeComponent } from './product-type-attribute.component';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-product-type',
  moduleId: module.id,
  templateUrl: 'product-type.component.html',
})

export class ProductTypeComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>>();

  private _productType:ProductTypeVO;
  private _attributes:ProductTypeAttrVO[] = [];
  private groupFilter:string;
  private attributeFilter:string;

  private _changes:Array<Pair<ProductTypeAttrVO, boolean>>;

  private attributeSelectedRow:ProductTypeAttrVO;
  private groupSelectedRow:ProductTypeViewGroupVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private productTypeForm:any;
  private productTypeFormSub:any; // tslint:disable-line:no-unused-variable

  @ViewChild('attributesComponent')
  private attributesComponent:ProductTypeAttributeComponent;
  @ViewChild('groupsComponent')
  private groupsComponent:ProductTypeGroupComponent;

  private searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('ProductTypeComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._productType == null || !that.productTypeForm || (!that.productTypeForm.dirty && that._productType.producttypeId > 0)) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'producttype', subjectId: that._productType.producttypeId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };


    this.productTypeForm = fb.group({
      'guid': ['', validCode],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'description': [''],
      'uitemplate': ['', YcValidators.noWhitespace],
      'uisearchtemplate': ['', YcValidators.noWhitespace],
      'ensemble': [''],
      'shippable': [''],
      'downloadable': [''],
      'digital': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'productTypeForm', 'productTypeFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'productTypeFormSub');
  }

  formChange():void {
    LogUtil.debug('ProductTypeComponent formChange', this.productTypeForm.valid, this._productType);
    this.dataChanged.emit({ source: new Pair(this._productType, this._changes), valid: this.productTypeForm.valid });
  }

  @Input()
  set productType(productType:ProductTypeVO) {

    UiUtil.formInitialise(this, 'initialising', 'productTypeForm', '_productType', productType);
    this._changes = [];

  }

  get productType():ProductTypeVO {
    return this._productType;
  }

  @Input()
  set attributes(attributes:ProductTypeAttrVO[]) {
    this._attributes = attributes;
  }

  get attributes():ProductTypeAttrVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('ProductTypeComponent attr data changed', this._productType);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  onGroupDataChanged(event:any) {
    LogUtil.debug('ProductTypeComponent attr data changed', this._productType);
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('ProductTypeComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ProductTypeComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ProductTypeComponent tabSelected', tab);
  }

  protected onAttributeRowAdd() {
    this.attributesComponent.onRowAdd();
  }

  protected onAttributeRowDeleteSelected() {
    if (this.attributeSelectedRow != null) {
      this.attributesComponent.onRowDeleteSelected();
    }
  }

  protected onAttributeRowEditSelected() {
    if (this.attributeSelectedRow != null) {
      this.attributesComponent.onRowEditSelected();
    }
  }

  protected onAttributeSelectRow(row:ProductTypeAttrVO) {
    LogUtil.debug('ProductTypeComponent onSelectRow handler', row);
    if (row == this.attributeSelectedRow) {
      this.attributeSelectedRow = null;
    } else {
      this.attributeSelectedRow = row;
    }
  }


  protected onClearFilter() {

    this.attributeFilter = '';

  }

  protected onClearFilterGroup() {

    this.groupFilter = '';

  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onAttributeSearchIndexFilter() {
    this.attributeFilter = '#SI';
    this.searchHelpShow = false;
  }

  protected onAttributeVisibleFilter() {
    this.attributeFilter = '+V';
    this.searchHelpShow = false;
  }

  protected onAttributeInvisibleFilter() {
    this.attributeFilter = '-V';
    this.searchHelpShow = false;
  }

  protected onAttributeChangesFilter() {
    this.attributeFilter = '###';
    this.searchHelpShow = false;
  }

  protected onGroupRowAdd() {
    this.groupsComponent.onRowAdd();
  }

  protected onGroupRowDeleteSelected() {
    if (this.groupSelectedRow != null) {
      this.groupsComponent.onRowDeleteSelected();
    }
  }

  protected onGroupRowEditSelected() {
    if (this.groupSelectedRow != null) {
      this.groupsComponent.onRowEditSelected();
    }
  }


  protected onGroupSelectRow(row:ProductTypeViewGroupVO) {
    LogUtil.debug('ProductTypeComponent onSelectRow handler', row);
    if (row == this.groupSelectedRow) {
      this.groupSelectedRow = null;
    } else {
      this.groupSelectedRow = row;
    }
  }

}
