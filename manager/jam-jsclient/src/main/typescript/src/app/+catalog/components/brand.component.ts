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
import { BrandVO, AttrValueBrandVO, Pair } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'cw-brand',
  templateUrl: 'brand.component.html',
})

export class BrandComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>>();

  private _brand:BrandVO;
  private _attributes:AttrValueBrandVO[] = [];
  public attributeFilter:string;
  public attributeSort:Pair<string, boolean> = { first: 'name', second: false };

  private _changes:Array<Pair<AttrValueBrandVO, boolean>>;

  public selectedRow:AttrValueBrandVO;

  public imageOnlyMode:boolean = false;

  public delayedChange:Future;

  public brandForm:any;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  public searchHelpShow:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('BrandComponent constructed');

    this.brandForm = fb.group({
      'name': ['', CustomValidators.requiredNonBlankTrimmed255],
      'description': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formBind():void {
    UiUtil.formBind(this, 'brandForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'brandForm');
  }

  formChange():void {
    LogUtil.debug('BrandComponent formChange', this.brandForm.valid, this._brand);
    this.dataChanged.emit({ source: new Pair(this._brand, this._changes), valid: this.brandForm.valid });
  }

  @Input()
  set brand(brand:BrandVO) {

    UiUtil.formInitialise(this, 'brandForm', '_brand', brand);
    this._changes = [];

  }

  get brand():BrandVO {
    return this._brand;
  }

  @Input()
  set attributes(attributes:AttrValueBrandVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueBrandVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('BrandComponent attr data changed', this._brand);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('BrandComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('BrandComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('BrandComponent tabSelected', tab);
  }

  onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('BrandComponent onPageSelected', page);
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('BrandComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  onSelectRow(row:AttrValueBrandVO) {
    LogUtil.debug('BrandComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  onClearFilter() {

    this.attributeFilter = '';

  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }


}
