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
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { CategoryVO, AttrValueCategoryVO, ProductTypeInfoVO, Pair, CategoryNavigationPriceTiersVO, CategoryNavigationPriceTierVO, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Util } from './../../shared/services/index';
import { UiUtil } from './../../shared/ui/index';
import { CategoryMinSelectComponent, ProductTypeSelectComponent } from './../../shared/catalog/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { ModalComponent, ModalAction, ModalResult } from './../../shared/modal/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-category',
  moduleId: module.id,
  templateUrl: 'category.component.html',
})

export class CategoryComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<CategoryVO, Array<Pair<AttrValueCategoryVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<CategoryVO, Array<Pair<AttrValueCategoryVO, boolean>>>>>();

  private _category:CategoryVO;
  private _attributes:AttrValueCategoryVO[] = [];
  private attributeFilter:string;
  private attributeSort:Pair<string, boolean> = { first: 'name', second: false };

  private navigationByPriceTiers:CategoryNavigationPriceTiersVO;
  private navigationByPriceTiersAddCurrency:string;

  private _changes:Array<Pair<AttrValueCategoryVO, boolean>>;

  private selectedRow:AttrValueCategoryVO;

  private imageOnlyMode:boolean = false;

  private delayedChange:Future;

  private categoryForm:any;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('categoryParentSelectComponent')
  private categoryParentSelectComponent:CategoryMinSelectComponent;

  @ViewChild('categoryProductTypeSelectComponent')
  private categoryProductTypeSelectComponent:ProductTypeSelectComponent;

  @ViewChild('categoryPriceTiersModalDialog')
  private categoryPriceTiersModalDialog:ModalComponent;

  private searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('CategoryComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (uri == null || uri == '' || that._category == null || !that.categoryForm || (!that.categoryForm.dirty && that._category.categoryId > 0)) {
        return null;
      }

      let basic = YcValidators.validSeoUri255(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'category', subjectId: that._category.categoryId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._category == null|| !that.categoryForm || (!that.categoryForm.dirty && that._category.categoryId > 0)) {
        return null;
      }

      let basic = YcValidators.validCode36(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'category', subjectId: that._category.categoryId, field: 'guid', value: code };
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
      'disabled': [''],
      'availablefrom': [''],
      'availableto': [''],
      'uri': ['', validUri],
      'navigationByAttributes': [''],
      'navigationByPrice': [''],
      'productTypeName': [''],
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
    UiUtil.formBind(this, 'categoryForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'categoryForm');
  }

  formChange():void {
    LogUtil.debug('CategoryComponent formChange', this.categoryForm.valid, this._category);
    this.dataChanged.emit({ source: new Pair(this._category, this._changes), valid: (this._category.categoryId != 100 && this.categoryForm.valid) });
  }

  @Input()
  set category(category:CategoryVO) {

    UiUtil.formInitialise(this, 'categoryForm', '_category', category);
    this._changes = [];

  }

  get category():CategoryVO {
    return this._category;
  }

  onAvailableFrom(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.category.availablefrom = event.source;
    }
    UiUtil.formDataChange(this, 'categoryForm', 'availablefrom', event);
  }

  onAvailableTo(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.category.availableto = event.source;
    }
    UiUtil.formDataChange(this, 'categoryForm', 'availableto', event);
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'categoryForm', 'name', event);
  }

  onTitleDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'categoryForm', 'title', event);
  }

  onKeywordsDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'categoryForm', 'keywords', event);
  }

  onMetaDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'categoryForm', 'meta', event);
  }

  @Input()
  set attributes(attributes:AttrValueCategoryVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueCategoryVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('CategoryComponent attr data changed', this._category);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('CategoryComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CategoryComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('CategoryComponent tabSelected', tab);
  }

  protected onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
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

  protected onPageSelected(page:number) {
    LogUtil.debug('CategoryComponent onPageSelected', page);
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('CategoryComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  protected onSelectRow(row:AttrValueCategoryVO) {
    LogUtil.debug('CategoryComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onEditParent() {
    LogUtil.debug('CategoryComponent onEditParent handler');
    this.categoryParentSelectComponent.showDialog(this.category.parentId);
  }

  protected onCategoryParentSelected(event:FormValidationEvent<CategoryVO>) {
    LogUtil.debug('CategoryComponent onCategoryParentSelected handler', event);
    if (event.valid) {
      this.category.parentId = event.source.categoryId;
      this.category.parentName = event.source.name;
      this.delayedChange.delay();
    }
  }

  protected onClearProductType() {
    LogUtil.debug('CategoryComponent onClearProductType handler');
    if (this._category) {
      this._category.productTypeId = 0;
      this._category.productTypeName = null;
      this.delayedChange.delay();
    }
  }


  protected onEditProductType() {
    LogUtil.debug('CategoryComponent onEditProductType handler');
    this.categoryProductTypeSelectComponent.showDialog();
  }


  protected onCategoryProductTypeSelected(event:FormValidationEvent<ProductTypeInfoVO>) {
    LogUtil.debug('CategoryComponent onCategoryProductTypeSelected handler', event);
    if (event.valid) {
      this.category.productTypeId = event.source.producttypeId;
      this.category.productTypeName = event.source.name;
      this.delayedChange.delay();
    }
  }

  protected onEditPriceTiers() {
    LogUtil.debug('CategoryComponent onCategoryProductTypeSelected handler');
    if (this._category.navigationByPriceTiers != null && this._category.navigationByPriceTiers.tiers != null) {
      this.navigationByPriceTiers = Util.clone(this._category.navigationByPriceTiers);
    } else {
      this.navigationByPriceTiers = { tiers: [] };
    }
    this.categoryPriceTiersModalDialog.show();
  }

  protected onEditPriceTiersModalResult(modalresult: ModalResult) {
    LogUtil.debug('CategoryComponent onEditPriceTiersModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this._category.navigationByPriceTiers = this.navigationByPriceTiers;
      this.navigationByPriceTiers = null;
      this.categoryForm.updateValueAndValidity();
      this.delayedChange.delay();
    } else {
      this.navigationByPriceTiers = null;
    }
  }


  protected onNavPriceTierDelete(tier:Pair<string, Array<CategoryNavigationPriceTierVO>>, item:CategoryNavigationPriceTierVO) {
    LogUtil.debug('CategoryComponent onNavPriceTierDelete handler', tier, item);
    let idx = tier.second.indexOf(item);
    if (idx != -1) {
      tier.second.splice(idx, 1);
      tier.second = tier.second.slice(0, tier.second.length);
    }
  }

  protected onNavPriceTierAdd(tier:Pair<string, Array<CategoryNavigationPriceTierVO>>) {
    LogUtil.debug('CategoryComponent onNavPriceTierAdd handler', tier);
    tier.second.push({ from: 0, to: 99 });
  }

  protected onEditPriceTiersAddCurrency() {
    LogUtil.debug('CategoryComponent onEditPriceTiersAddCurrency handler');
    if (this.navigationByPriceTiersAddCurrency != null && this.navigationByPriceTiersAddCurrency.length == 3) {
      this.navigationByPriceTiers.tiers.push({ first: this.navigationByPriceTiersAddCurrency, second: [] });
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
