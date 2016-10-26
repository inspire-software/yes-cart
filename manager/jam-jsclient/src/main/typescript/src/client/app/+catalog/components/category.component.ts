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
import {CategoryVO, AttrValueCategoryVO, ProductTypeInfoVO, Pair, CategoryNavigationPriceTiersVO, CategoryNavigationPriceTierVO, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {Util} from './../../shared/services/index';
import {UiUtil} from './../../shared/ui/index';
import {I18nComponent} from './../../shared/i18n/index';
import {CategorySelectComponent} from './../../shared/catalog/index';
import {ProductTypeSelectComponent} from './../../shared/product/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {ModalComponent, ModalAction, ModalResult} from './../../shared/modal/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-category',
  moduleId: module.id,
  templateUrl: 'category.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, CategorySelectComponent, ProductTypeSelectComponent, I18nComponent, ModalComponent],
})

export class CategoryComponent implements OnInit, OnDestroy {

  _category:CategoryVO;
  _attributes:AttrValueCategoryVO[] = [];
  attributeFilter:string;

  navigationByPriceTiers:CategoryNavigationPriceTiersVO;
  navigationByPriceTiersAddCurrency:string;

  _changes:Array<Pair<AttrValueCategoryVO, boolean>>;

  selectedRow:AttrValueCategoryVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<CategoryVO, Array<Pair<AttrValueCategoryVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<CategoryVO, Array<Pair<AttrValueCategoryVO, boolean>>>>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  categoryForm:any;
  categoryFormSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('categoryParentSelectComponent')
  categoryParentSelectComponent:CategorySelectComponent;

  @ViewChild('categoryProductTypeSelectComponent')
  categoryProductTypeSelectComponent:ProductTypeSelectComponent;

  @ViewChild('categoryPriceTiersModalDialog')
  categoryPriceTiersModalDialog:ModalComponent;

  constructor(fb: FormBuilder) {
    console.debug('CategoryComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (!that.changed || uri == null || uri == '' || that._category == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'category', subjectId: that._category.categoryId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._category == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'category', subjectId: that._category.categoryId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.categoryForm = fb.group({
      'guid': ['', validCode],
      'parentName': ['', Validators.required],
      'linkToName': [''],
      'description': [''],
      'rank': ['', YcValidators.requiredRank],
      'uitemplate': ['', YcValidators.nonBlankTrimmed],
      'availablefrom': ['', YcValidators.validDate],
      'availableto': ['', YcValidators.validDate],
      'uri': ['', validUri],
      'navigationByAttributes': [''],
      'navigationByBrand': [''],
      'navigationByPrice': [''],
      'productTypeName': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.categoryForm.controls) {
      this.categoryForm.controls[key]['_pristine'] = true;
      this.categoryForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.categoryFormSub = this.categoryForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this._category.categoryId != 100 && this.categoryForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.categoryFormSub) {
      console.debug('CategoryComponent unbining form');
      this.categoryFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('CategoryComponent validating formGroup is valid: ' + this.validForSave, this._category);
    this.dataChanged.emit({ source: new Pair(this._category, this._changes), valid: this.validForSave });
  }

  @Input()
  set category(category:CategoryVO) {
    this._category = category;
    this.changed = false;
    this.formReset();
  }

  get category():CategoryVO {
    return this._category;
  }

  get availableto():string {
    return UiUtil.dateInputGetterProxy(this._category, 'availableto');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this._category, 'availableto', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this._category, 'availablefrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this._category, 'availablefrom', availablefrom);
  }

  @Input()
  set attributes(attributes:AttrValueCategoryVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueCategoryVO[] {
    return this._attributes;
  }

  onMainDataChange(event:any) {
    console.debug('CategoryComponent main data changed', this._category);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('CategoryComponent attr data changed', this._category);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('CategoryComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('CategoryComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('CategoryComponent tabSelected', tab);
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

  protected onSelectRow(row:AttrValueCategoryVO) {
    console.debug('CategoryComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onEditParent() {
    console.debug('CategoryComponent onEditParent handler');
    this.categoryParentSelectComponent.showDialog();
  }

  protected onCategoryParentSelected(event:FormValidationEvent<CategoryVO>) {
    console.debug('CategoryComponent onCategoryParentSelected handler', event);
    if (event.valid) {
      this.category.parentId = event.source.categoryId;
      this.category.parentName = event.source.name;
      this.changed = true;
    }
  }

  protected onClearProductType() {
    console.debug('CategoryComponent onClearProductType handler');
    if (this._category) {
      this._category.productTypeId = 0;
      this._category.productTypeName = null;
    }
  }


  protected onEditProductType() {
    console.debug('CategoryComponent onEditProductType handler');
    this.categoryProductTypeSelectComponent.showDialog();
  }


  protected onCategoryProductTypeSelected(event:FormValidationEvent<ProductTypeInfoVO>) {
    console.debug('CategoryComponent onCategoryProductTypeSelected handler', event);
    if (event.valid) {
      this.category.productTypeId = event.source.producttypeId;
      this.category.productTypeName = event.source.name;
      this.changed = true;
    }
  }

  protected onEditPriceTiers() {
    console.debug('CategoryComponent onCategoryProductTypeSelected handler');
    if (this._category.navigationByPriceTiers != null && this._category.navigationByPriceTiers.tiers != null) {
      this.navigationByPriceTiers = Util.clone(this._category.navigationByPriceTiers);
    } else {
      this.navigationByPriceTiers = { tiers: [] };
    }
    this.categoryPriceTiersModalDialog.show();
  }

  protected onEditPriceTiersModalResult(modalresult: ModalResult) {
    console.debug('CategoryComponent onEditPriceTiersModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this._category.navigationByPriceTiers = this.navigationByPriceTiers;
      this.navigationByPriceTiers = null;
      this.changed = true;
      this.categoryForm.updateValueAndValidity();
    } else {
      this.navigationByPriceTiers = null;
    }
  }


  protected onNavPriceTierDelete(tier:Pair<string, Array<CategoryNavigationPriceTierVO>>, item:CategoryNavigationPriceTierVO) {
    console.debug('CategoryComponent onNavPriceTierDelete handler', tier, item);
    let idx = tier.second.indexOf(item);
    if (idx != -1) {
      tier.second.splice(idx, 1);
      tier.second = tier.second.slice(0, tier.second.length);
    }
  }

  protected onNavPriceTierAdd(tier:Pair<string, Array<CategoryNavigationPriceTierVO>>) {
    console.debug('CategoryComponent onNavPriceTierAdd handler', tier);
    tier.second.push({ from: 0, to: 99 });
  }

  protected onEditPriceTiersAddCurrency() {
    console.debug('CategoryComponent onEditPriceTiersAddCurrency handler');
    if (this.navigationByPriceTiersAddCurrency != null && this.navigationByPriceTiersAddCurrency.length == 3) {
      this.navigationByPriceTiers.tiers.push({ first: this.navigationByPriceTiersAddCurrency, second: [] });
    }
  }

}
