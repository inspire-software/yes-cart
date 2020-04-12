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
import { ProductWithLinksVO, AttrValueProductVO, ProductAssociationVO, BrandVO, ProductTypeInfoVO, ProductSkuVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { ProductAssociationsComponent } from './index';
import { UiUtil } from './../../shared/ui/index';
import { BrandSelectComponent, ProductTypeSelectComponent } from './../../shared/catalog/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-product',
  moduleId: module.id,
  templateUrl: 'product.component.html',
})

export class ProductComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ProductWithLinksVO, Array<Pair<AttrValueProductVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ProductWithLinksVO, Array<Pair<AttrValueProductVO, boolean>>>>>();

  @Output() skuSelected: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  @Output() skuAddClick: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  @Output() skuEditClick: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  @Output() skuDeleteClick: EventEmitter<ProductSkuVO> = new EventEmitter<ProductSkuVO>();

  private _product:ProductWithLinksVO;
  private avPrototype:AttrValueProductVO;
  private _attributes:AttrValueProductVO[] = [];
  private attributeFilter:string;
  private attributeSort:Pair<string, boolean> = { first: 'name', second: false };
  private associationFilter:string;
  private associationSort:Pair<string, boolean> = { first: 'associationId', second: false };
  private skuSort:Pair<string, boolean> = { first: 'rank', second: false };

  private _changes:Array<Pair<AttrValueProductVO, boolean>>;

  private selectedRowAttribute:AttrValueProductVO;
  private selectedRowAssociation:ProductAssociationVO;

  private delayedChange:Future;

  private productForm:any;

  private skuFilter:string;

  private selectedSku:ProductSkuVO = null;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('associationsComponent')
  private associationsComponent:ProductAssociationsComponent;

  @ViewChild('brandsModalDialog')
  private brandsModalDialog:BrandSelectComponent;

  @ViewChild('productTypesModalDialog')
  private productTypesModalDialog:ProductTypeSelectComponent;

  private searchHelpShowAttribute:boolean = false;
  private searchHelpShowAssociation:boolean = false;

  private reloadCatalogue:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('ProductComponent constructed');


    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (uri == null || uri == '' || that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
        return null;
      }

      let basic = YcValidators.validSeoUri255(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validGuid = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
        return null;
      }

      let basic = YcValidators.validCode36(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let basic = YcValidators.requiredValidCode255(control);
      if (basic == null) {

        let code = control.value;
        if (that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
          return null;
        }

        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'code', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.productForm = fb.group({
      'guid': ['', validGuid],
      'code': ['', validCode],
      'manufacturerCode': ['', YcValidators.noWhitespace255],
      'manufacturerPartCode': ['', YcValidators.noWhitespace255],
      'supplierCode': ['', YcValidators.noWhitespace255],
      'supplierCatalogCode': ['', YcValidators.noWhitespace255],
      'pimCode': ['', YcValidators.noWhitespace255],
      'pimDisabled': [''],
      'pimOutdated': [''],
      'pimUpdated': [''],
      'tag': ['', YcValidators.nonBlankTrimmed],
      'brandName': ['', YcValidators.requiredNonBlankTrimmed],
      'productTypeName': ['', YcValidators.requiredNonBlankTrimmed],
      'configurable': [''],
      'description': [''],
      'uri': ['', validUri],
      'uitemplate': [''],
      'uisearchtemplate': [''],
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
    UiUtil.formBind(this, 'productForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'productForm');
  }

  formChange():void {
    LogUtil.debug('ProductComponent formChange', this.productForm.valid, this._product);
    this.dataChanged.emit({ source: new Pair(this._product, this._changes), valid: this.productForm.valid });
  }

  @Input()
  set product(product:ProductWithLinksVO) {

    UiUtil.formInitialise(this, 'productForm', '_product', product);
    this._changes = [];
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

  }

  get product():ProductWithLinksVO {
    return this._product;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'productForm', 'name', event);
  }

  onTitleDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'productForm', 'title', event);
  }

  onKeywordsDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'productForm', 'keywords', event);
  }

  onMetaDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'productForm', 'meta', event);
  }

  @Input()
  set attributes(attributes:AttrValueProductVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueProductVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('ProductComponent attr data changed', this._product);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  onCategoriesDataChanged(event:any) {
    LogUtil.debug('ProductComponent cat data changed', this._product);
    this._product.productCategories = event;
    this.delayedChange.delay();
  }

  onAssociationDataChanged(event:any) {
    LogUtil.debug('ProductComponent assoc data changed', this._product);
    this.delayedChange.delay();
  }



  ngOnInit() {
    LogUtil.debug('ProductComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ProductComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ProductComponent tabSelected', tab);
    this.reloadCatalogue = tab === 'Catalogue';
  }

  protected onRowAddAttribute() {
    this.attributeValuesComponent.onRowAdd();
  }


  protected onClearSkuFilter() {

    this.skuFilter = '';

  }

  protected onSkuSelected(data:ProductSkuVO) {
    LogUtil.debug('ProductComponent onSkuSelected', data);
    this.selectedSku = data;
    this.skuSelected.emit(data);
  }

  protected onRowAddSKU() {
    LogUtil.debug('ProductComponent onRowAddSKU', this.selectedSku);
    this.skuAddClick.emit(null);
  }

  protected onPageSelectedSku(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedSku', page);
  }

  protected onSortSelectedSku(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedSku', sort);
    if (sort == null) {
      this.skuSort = { first: 'rank', second: false };
    } else {
      this.skuSort = sort;
    }
  }


  protected onRowEditSelectedSKU() {
    LogUtil.debug('ProductComponent onRowEditSelectedSKU', this.selectedSku);
    if (this.selectedSku != null) {
      this.skuEditClick.emit(this.selectedSku);
    }
  }


  protected onRowCopyAttributeSKU(row:ProductSkuVO) {
    LogUtil.debug('ProductComponent onRowCopyAttributeSKU handler', row);
    this.skuAddClick.emit(row);
  }

  protected onRowCopySelectedSKU() {
    if (this.selectedSku != null) {
      this.onRowCopyAttributeSKU(this.selectedSku);
    }
  }


  protected onRowDeleteSelectedSKU() {
    LogUtil.debug('ProductComponent onRowDeleteSelectedSKU', this.selectedSku);
    if (this.selectedSku != null) {
      this.skuDeleteClick.emit(this.selectedSku);
    }
  }

  protected onRowDeleteSelectedAttribute() {
    if (this.selectedRowAttribute != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelectedAttribute() {
    if (this.selectedRowAttribute != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  protected onPageSelectedAttribute(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedAttribute', page);
  }

  protected onSortSelectedAttribute(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedAttribute', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  protected onSelectRowAttribute(row:AttrValueProductVO) {
    LogUtil.debug('ProductComponent onSelectRowAttribute handler', row);
    if (row == this.selectedRowAttribute) {
      this.selectedRowAttribute = null;
    } else {
      this.selectedRowAttribute = row;
    }
  }


  protected onRowAddAssociation() {
    this.associationsComponent.onRowAdd();
  }


  protected onRowDeleteSelectedAssociation() {
    if (this.selectedRowAssociation != null) {
      this.associationsComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelectedAssociation() {
    if (this.selectedRowAssociation != null) {
      this.associationsComponent.onRowEditSelected();
    }
  }

  protected onPageSelectedAssociation(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedAssociation', page);
  }

  protected onSortSelectedAssociation(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedAssociation', sort);
    if (sort == null) {
      this.associationSort = { first: 'associationId', second: false };
    } else {
      this.associationSort = sort;
    }
  }


  protected onSelectRowAssociation(row:ProductAssociationVO) {
    LogUtil.debug('ProductComponent onSelectRowAssociation handler', row);
    if (row == this.selectedRowAssociation) {
      this.selectedRowAssociation = null;
    } else {
      this.selectedRowAssociation = row;
    }
  }


  protected onEditBrand() {
    this.brandsModalDialog.showDialog();
  }

  protected onBrandSelected(event:FormValidationEvent<BrandVO>) {
    LogUtil.debug('ProductComponent onBrandSelected', event);
    if (event.valid) {
      this._product.brand = event.source;
      this.delayedChange.delay();
    }
  }

  protected onEditProductType() {
    this.productTypesModalDialog.showDialog();
  }


  protected onProductTypeSelected(event:FormValidationEvent<ProductTypeInfoVO>) {
    LogUtil.debug('ProductComponent onProductTypeSelected', event);
    if (event.valid) {
      this._product.productType = event.source;
      this.delayedChange.delay();
    }
  }

  protected onClearAttributeFilter() {

    this.attributeFilter = '';

  }

  protected onSearchHelpAttributeToggle() {
    this.searchHelpShowAttribute = !this.searchHelpShowAttribute;
  }

  protected onSearchValues() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '###';
  }

  protected onSearchValuesNew() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '##0';
  }

  protected onSearchValuesNewOnly() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '#00';
  }

  protected onSearchValuesChanges() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '#0#';
  }


  protected onClearAssociationFilter() {

    this.associationFilter = '';

  }

  protected onSearchHelpAssociationToggle() {
    this.searchHelpShowAssociation = !this.searchHelpShowAssociation;
  }

  protected onSearchAssociation() {
    this.searchHelpShowAssociation = false;
    this.associationFilter = '#cross';
  }

}
