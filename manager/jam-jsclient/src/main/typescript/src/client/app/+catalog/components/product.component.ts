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
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {ProductWithLinksVO, AttrValueProductVO, BrandVO, ProductTypeInfoVO, ProductSkuVO, Pair, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {UiUtil} from './../../shared/ui/index';
import {I18nComponent} from './../../shared/i18n/index';
import {ModalComponent, ModalAction, ModalResult} from './../../shared/modal/index';
import {BrandSelectComponent, ProductTypeSelectComponent} from './../../shared/catalog/index';
import {SKUsComponent} from './skus.component';
import {ProductCategoryComponent} from './product-categories.component';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-product',
  moduleId: module.id,
  templateUrl: 'product.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, BrandSelectComponent, ProductTypeSelectComponent, ModalComponent, I18nComponent, SKUsComponent, ProductCategoryComponent],
})

export class ProductComponent implements OnInit, OnDestroy {

  _product:ProductWithLinksVO;
  avPrototype:AttrValueProductVO;
  _attributes:AttrValueProductVO[] = [];
  attributeFilter:string;

  _changes:Array<Pair<AttrValueProductVO, boolean>>;

  selectedBrand:BrandVO;
  selectedProductType:ProductTypeInfoVO;
  selectedRow:AttrValueProductVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductWithLinksVO, Array<Pair<AttrValueProductVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductWithLinksVO, Array<Pair<AttrValueProductVO, boolean>>>>>();
  @Output() dataSelected: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  productForm:any;
  productFormSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('brandsModalDialog')
  brandsModalDialog:ModalComponent;

  @ViewChild('productTypesModalDialog')
  productTypesModalDialog:ModalComponent;

  private searchHelpShow:boolean = false;

  private reloadCatalogue:boolean = false;

  constructor(fb: FormBuilder) {
    console.debug('ProductComponent constructed');


    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (!that.changed || uri == null || uri == '' || that._product == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validGuid = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._product == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._product == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.productForm = fb.group({
      'guid': ['', validGuid],
      'code': ['', validCode],
      'manufacturerCode': ['', YcValidators.noWhitespace],
      'pimCode': ['', YcValidators.noWhitespace],
      'tag': ['', YcValidators.nonBlankTrimmed],
      'availablefrom': ['', YcValidators.validDate],
      'availableto': ['', YcValidators.validDate],
      'availability': ['', YcValidators.requiredPositiveNumber],
      'brandName': ['', YcValidators.requiredNonBlankTrimmed],
      'productTypeName': ['', YcValidators.requiredNonBlankTrimmed],
      'description': [''],
      'featured': [''],
      'minOrderQuantity': ['', YcValidators.positiveWholeNumber],
      'maxOrderQuantity': ['', YcValidators.positiveWholeNumber],
      'stepOrderQuantity': ['', YcValidators.positiveWholeNumber],
      'uri': ['', validUri],
      'uitemplate': [''],
      'uisearchtemplate': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.productForm.controls) {
      this.productForm.controls[key]['_pristine'] = true;
      this.productForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.productFormSub = this.productForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.productForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.productFormSub) {
      console.debug('ProductComponent unbining form');
      this.productFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('ProductComponent validating formGroup is valid: ' + this.validForSave, this._product);
    this.dataChanged.emit({ source: new Pair(this._product, this._changes), valid: this.validForSave });
  }

  @Input()
  set product(product:ProductWithLinksVO) {
    this._product = product;
    if (this._product != null) {
      this.avPrototype = {
        attrvalueId: 0,
        val: null,
        displayVals: [],
        valBase64Data: null,
        attribute: null,
        productId: this._product.productId
      };
    } else {
      this.avPrototype = null;
    }
    this.changed = false;
    this.formReset();
  }

  get product():ProductWithLinksVO {
    return this._product;
  }

  @Input()
  set attributes(attributes:AttrValueProductVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueProductVO[] {
    return this._attributes;
  }


  get availableto():string {
    return UiUtil.dateInputGetterProxy(this._product, 'availableto');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this._product, 'availableto', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this._product, 'availablefrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this._product, 'availablefrom', availablefrom);
  }


  onMainDataChange(event:any) {
    console.debug('ProductComponent main data changed', this._product);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('ProductComponent attr data changed', this._product);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  onCategoriesDataChanged(event:any) {
    console.debug('ProductComponent cat data changed', this._product);
    this.changed = true;
    this._product.productCategories = event;
    this.delayedChange.delay();
  }


  ngOnInit() {
    console.debug('ProductComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ProductComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('ProductComponent tabSelected', tab);

    this.reloadCatalogue = tab === 'Catalogue';

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

  protected onSelectRow(row:AttrValueProductVO) {
    console.debug('ProductComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onRowAdd() {
    this.attributeValuesComponent.onRowAdd();
  }

  protected onEditBrand() {
    this.selectedBrand = null;
    this.brandsModalDialog.show();
  }

  protected onBrandSelected(brand:BrandVO) {
    console.debug('ProductComponent onBrandSelected', brand);
    this.selectedBrand = brand;
  }


  protected onEditBrandModalResult(modalresult: ModalResult) {
    console.debug('ProductComponent onEditBrandModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this._product.brand = this.selectedBrand;
      this.changed = true;
      this.productForm.updateValueAndValidity();
    } else {
      this.selectedBrand = null;
    }
  }


  protected onEditProductType() {
    this.selectedProductType = null;
    this.productTypesModalDialog.show();
  }


  protected onProductTypeSelected(productType:ProductTypeInfoVO) {
    console.debug('ProductComponent onProductTypeSelected', productType);
    this.selectedProductType = productType;
  }


  protected onEditProductTypeModalResult(modalresult: ModalResult) {
    console.debug('ProductComponent onEditBrandModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this._product.productType = this.selectedProductType;
      this.changed = true;
      this.productForm.updateValueAndValidity();
    } else {
      this.selectedProductType = null;
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

  onSkuSelected(data:ProductSkuVO) {
    console.debug('ProductComponent onSkuSelected', data);
    this.dataSelected.emit(data);
  }

}
