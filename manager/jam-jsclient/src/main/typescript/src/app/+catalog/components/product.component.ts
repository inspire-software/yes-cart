/*
 * Copyright 2009 Inspire-Software.com
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
import { CustomValidators } from './../../shared/validation/validators';
import { ProductWithLinksVO, AttrValueProductVO, ProductOptionVO, ProductAssociationVO, BrandVO, ProductTypeInfoVO, ProductSkuVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { ProductAssociationsComponent, ProductOptionsComponent } from './index';
import { UiUtil } from './../../shared/ui/index';
import { BrandSelectComponent, ProductTypeSelectComponent } from './../../shared/catalog/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-product',
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
  public attributeFilter:string;
  public attributeSort:Pair<string, boolean> = { first: 'name', second: false };
  public associationFilter:string;
  public associationSort:Pair<string, boolean> = { first: 'associationId', second: false };
  public optionsFilter:string;
  public optionsSort:Pair<string, boolean> = { first: 'rank', second: false };
  public skuSort:Pair<string, boolean> = { first: 'rank', second: false };

  private _changes:Array<Pair<AttrValueProductVO, boolean>>;

  public selectedRowAttribute:AttrValueProductVO;
  public selectedRowAssociation:ProductAssociationVO;
  public selectedRowOption:ProductOptionVO;

  public imageOnlyMode:boolean = false;

  public delayedChange:Future;

  public productForm:any;

  public skuFilter:string;

  public selectedSku:ProductSkuVO = null;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('associationsComponent')
  private associationsComponent:ProductAssociationsComponent;

  @ViewChild('optionsComponent')
  private optionsComponent:ProductOptionsComponent;

  @ViewChild('brandsModalDialog')
  private brandsModalDialog:BrandSelectComponent;

  @ViewChild('productTypesModalDialog')
  private productTypesModalDialog:ProductTypeSelectComponent;

  public searchHelpShowAttribute:boolean = false;
  public searchHelpShowAssociation:boolean = false;
  public searchHelpShowOptions:boolean = false;

  public reloadCatalogue:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('ProductComponent constructed');


    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (uri == null || uri == '' || that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
        return null;
      }

      let basic = CustomValidators.validSeoUri255(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'uri', value: uri };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validGuid = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
        return null;
      }

      let basic = CustomValidators.validCode36(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'guid', value: code };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let basic = CustomValidators.requiredValidCode255(control);
      if (basic == null) {

        let code = control.value;
        if (that._product == null || !that.productForm || (!that.productForm.dirty && that._product.productId > 0)) {
          return null;
        }

        let req:ValidationRequestVO = { subject: 'product', subjectId: that._product.productId, field: 'code', value: code };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.productForm = fb.group({
      'guid': ['', validGuid],
      'code': ['', validCode],
      'manufacturerCode': ['', CustomValidators.noWhitespace255],
      'manufacturerPartCode': ['', CustomValidators.noWhitespace255],
      'supplierCode': ['', CustomValidators.noWhitespace255],
      'supplierCatalogCode': ['', CustomValidators.noWhitespace255],
      'pimCode': ['', CustomValidators.noWhitespace255],
      'pimDisabled': [''],
      'pimOutdated': [''],
      'pimUpdated': [''],
      'tag': ['', CustomValidators.nonBlankTrimmed],
      'brand': fb.group({
        'name': ['', CustomValidators.requiredNonBlankTrimmed]
      }),
      'productType': fb.group({
        'name': ['', CustomValidators.requiredNonBlankTrimmed]
      }),
      'notSoldSeparately': [''],
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

  onOptionsDataChanged(event:any) {
    LogUtil.debug('ProductComponent options data changed', this._product);
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

  onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
  }

  onRowAddAttribute() {
    this.attributeValuesComponent.onRowAdd();
  }


  onClearSkuFilter() {

    this.skuFilter = '';

  }

  onSkuSelected(data:ProductSkuVO) {
    LogUtil.debug('ProductComponent onSkuSelected', data);
    this.selectedSku = data;
    this.skuSelected.emit(data);
  }

  onRowAddSKU() {
    LogUtil.debug('ProductComponent onRowAddSKU', this.selectedSku);
    this.skuAddClick.emit(null);
  }

  onPageSelectedSku(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedSku', page);
  }

  onSortSelectedSku(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedSku', sort);
    if (sort == null) {
      this.skuSort = { first: 'rank', second: false };
    } else {
      this.skuSort = sort;
    }
  }


  onRowEditSelectedSKU() {
    LogUtil.debug('ProductComponent onRowEditSelectedSKU', this.selectedSku);
    if (this.selectedSku != null) {
      this.skuEditClick.emit(this.selectedSku);
    }
  }


  onRowCopyAttributeSKU(row:ProductSkuVO) {
    LogUtil.debug('ProductComponent onRowCopyAttributeSKU handler', row);
    this.skuAddClick.emit(row);
  }

  onRowCopySelectedSKU() {
    if (this.selectedSku != null) {
      this.onRowCopyAttributeSKU(this.selectedSku);
    }
  }


  onRowDeleteSelectedSKU() {
    LogUtil.debug('ProductComponent onRowDeleteSelectedSKU', this.selectedSku);
    if (this.selectedSku != null) {
      this.skuDeleteClick.emit(this.selectedSku);
    }
  }

  onRowDeleteSelectedAttribute() {
    if (this.selectedRowAttribute != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelectedAttribute() {
    if (this.selectedRowAttribute != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  onPageSelectedAttribute(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedAttribute', page);
  }

  onSortSelectedAttribute(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedAttribute', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  onSelectRowAttribute(row:AttrValueProductVO) {
    LogUtil.debug('ProductComponent onSelectRowAttribute handler', row);
    if (row == this.selectedRowAttribute) {
      this.selectedRowAttribute = null;
    } else {
      this.selectedRowAttribute = row;
    }
  }


  onRowAddOption() {
    this.optionsComponent.onRowAdd();
  }


  onRowDeleteSelectedOption() {
    if (this.selectedRowOption != null) {
      this.optionsComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelectedOption() {
    if (this.selectedRowOption != null) {
      this.optionsComponent.onRowEditSelected();
    }
  }

  onPageSelectedOption(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedOption', page);
  }



  onSortSelectedOption(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedOption', sort);
    if (sort == null) {
      this.optionsSort = { first: 'rank', second: false };
    } else {
      this.optionsSort = sort;
    }
  }


  onSelectRowOption(row:ProductOptionVO) {
    LogUtil.debug('ProductComponent onSelectRowOption handler', row);
    if (row == this.selectedRowOption) {
      this.selectedRowOption = null;
    } else {
      this.selectedRowOption = row;
    }
  }





  onRowAddAssociation() {
    this.associationsComponent.onRowAdd();
  }


  onRowDeleteSelectedAssociation() {
    if (this.selectedRowAssociation != null) {
      this.associationsComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelectedAssociation() {
    if (this.selectedRowAssociation != null) {
      this.associationsComponent.onRowEditSelected();
    }
  }

  onPageSelectedAssociation(page:number) {
    LogUtil.debug('ProductComponent onPageSelectedAssociation', page);
  }

  onSortSelectedAssociation(sort:Pair<string, boolean>) {
    LogUtil.debug('ProductComponent onSortSelectedAssociation', sort);
    if (sort == null) {
      this.associationSort = { first: 'associationId', second: false };
    } else {
      this.associationSort = sort;
    }
  }


  onSelectRowAssociation(row:ProductAssociationVO) {
    LogUtil.debug('ProductComponent onSelectRowAssociation handler', row);
    if (row == this.selectedRowAssociation) {
      this.selectedRowAssociation = null;
    } else {
      this.selectedRowAssociation = row;
    }
  }

  onEditBrand() {
    this.brandsModalDialog.showDialog();
  }

  onBrandSelected(event:FormValidationEvent<BrandVO>) {
    LogUtil.debug('ProductComponent onBrandSelected', event);
    if (event.valid) {
      this._product.brand = event.source;
      this.productForm.controls['brand'].reset(event.source, { onlySelf: true, emitEvent: true });
      this.delayedChange.delay();
    }
  }

  onEditProductType() {
    this.productTypesModalDialog.showDialog();
  }


  onProductTypeSelected(event:FormValidationEvent<ProductTypeInfoVO>) {
    LogUtil.debug('ProductComponent onProductTypeSelected', event);
    if (event.valid) {
      this._product.productType = event.source;
      this.productForm.controls['productType'].reset(event.source, { onlySelf: true, emitEvent: true });
      this.delayedChange.delay();
    }
  }

  onClearAttributeFilter() {

    this.attributeFilter = '';

  }

  onSearchHelpAttributeToggle() {
    this.searchHelpShowAttribute = !this.searchHelpShowAttribute;
  }

  onSearchHelpOptionToggle() {
    this.searchHelpShowAttribute = !this.searchHelpShowAttribute;
  }

  onSearchValues() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShowAttribute = false;
    this.attributeFilter = '#0#';
  }


  onClearAssociationFilter() {

    this.associationFilter = '';

  }

  onSearchHelpAssociationToggle() {
    this.searchHelpShowAssociation = !this.searchHelpShowAssociation;
  }

  onSearchAssociation() {
    this.searchHelpShowAssociation = false;
    this.associationFilter = '#cross';
  }

  onSearchOption() {
    this.searchHelpShowOptions = false;
    this.optionsFilter = '#';
  }


  onClearOptionsFilter() {

    this.optionsFilter = '';

  }


}
