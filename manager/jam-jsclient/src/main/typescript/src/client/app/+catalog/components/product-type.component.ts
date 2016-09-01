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
import {ProductTypeVO, ProductTypeAttrVO, ProductTypeViewGroupVO, Pair, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {ProductTypeGroupComponent} from './product-type-group.component';
import {ProductTypeAttributeComponent} from './product-type-attribute.component';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-product-type',
  moduleId: module.id,
  templateUrl: 'product-type.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, ProductTypeAttributeComponent, ProductTypeGroupComponent],
})

export class ProductTypeComponent implements OnInit, OnDestroy {

  _productType:ProductTypeVO;
  _attributes:ProductTypeAttrVO[] = [];
  attributeFilter:string;

  _changes:Array<Pair<ProductTypeAttrVO, boolean>>;

  attributeSelectedRow:ProductTypeAttrVO;
  groupSelectedRow:ProductTypeViewGroupVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  productTypeForm:any;
  productTypeFormSub:any;

  @ViewChild('attributesComponent')
  attributesComponent:ProductTypeAttributeComponent;
  @ViewChild('groupsComponent')
  groupsComponent:ProductTypeGroupComponent;

  private searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    console.debug('ProductTypeComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._productType == null) {
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

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.productTypeForm.controls) {
      this.productTypeForm.controls[key]['_pristine'] = true;
      this.productTypeForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.productTypeFormSub = this.productTypeForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.productTypeForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.productTypeFormSub) {
      console.debug('ProductTypeComponent unbining form');
      this.productTypeFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('ProductTypeComponent validating formGroup is valid: ' + this.validForSave, this._productType);
    this.dataChanged.emit({ source: new Pair(this._productType, this._changes), valid: this.validForSave });
  }

  @Input()
  set productType(productType:ProductTypeVO) {
    this._productType = productType;
    this.changed = false;
    this.formReset();
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

  onMainDataChange(event:any) {
    console.debug('ProductTypeComponent main data changed', this._productType);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('ProductTypeComponent attr data changed', this._productType);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  onGroupDataChanged(event:any) {
    console.debug('ProductTypeComponent attr data changed', this._productType);
    this.changed = true;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('ProductTypeComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ProductTypeComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('ProductTypeComponent tabSelected', tab);
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
    console.debug('ProductTypeComponent onSelectRow handler', row);
    if (row == this.attributeSelectedRow) {
      this.attributeSelectedRow = null;
    } else {
      this.attributeSelectedRow = row;
    }
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
    console.debug('ProductTypeComponent onSelectRow handler', row);
    if (row == this.groupSelectedRow) {
      this.groupSelectedRow = null;
    } else {
      this.groupSelectedRow = row;
    }
  }

}
