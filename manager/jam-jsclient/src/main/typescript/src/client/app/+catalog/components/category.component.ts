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
import {CategoryVO, AttrValueCategoryVO, Pair} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {I18nComponent} from './../../shared/i18n/index';
import {CategorySelectComponent} from './../../shared/catalog/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-category',
  moduleId: module.id,
  templateUrl: 'category.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, CategorySelectComponent, I18nComponent],
})

export class CategoryComponent implements OnInit, OnDestroy {

  _category:CategoryVO;
  _attributes:AttrValueCategoryVO[] = [];
  attributeFilter:string;

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

  constructor(fb: FormBuilder) {
    console.debug('CategoryComponent constructed');

    this.categoryForm = fb.group({
      'guid': ['', YcValidators.validCode],
      'parentName': ['', Validators.required],
      'description': [''],
      'rank': ['', YcValidators.requiredRank],
    });

    let that = this;
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
    this.categoryFormSub = this.categoryForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.categoryForm.valid;
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

}
