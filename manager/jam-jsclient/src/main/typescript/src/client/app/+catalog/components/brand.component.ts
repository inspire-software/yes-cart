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
import {BrandVO, AttrValueBrandVO, Pair} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-brand',
  moduleId: module.id,
  templateUrl: 'brand.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent],
})

export class BrandComponent implements OnInit, OnDestroy {

  _brand:BrandVO;
  _attributes:AttrValueBrandVO[] = [];
  attributeFilter:string;

  _changes:Array<Pair<AttrValueBrandVO, boolean>>;

  selectedRow:AttrValueBrandVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  brandForm:any;
  brandFormSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  constructor(fb: FormBuilder) {
    console.debug('BrandComponent constructed');

    this.brandForm = fb.group({
      'name': ['', YcValidators.requiredNonBlankTrimmed],
      'description': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.brandForm.controls) {
      this.brandForm.controls[key]['_pristine'] = true;
      this.brandForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.brandFormSub = this.brandForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.brandForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.brandFormSub) {
      console.debug('BrandComponent unbining form');
      this.brandFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('BrandComponent validating formGroup is valid: ' + this.validForSave, this._brand);
    this.dataChanged.emit({ source: new Pair(this._brand, this._changes), valid: this.validForSave });
  }

  @Input()
  set brand(brand:BrandVO) {
    this._brand = brand;
    this.changed = false;
    this.formReset();
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

  onMainDataChange(event:any) {
    console.debug('BrandComponent main data changed', this._brand);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('BrandComponent attr data changed', this._brand);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('BrandComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('BrandComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('BrandComponent tabSelected', tab);
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

  protected onSelectRow(row:AttrValueBrandVO) {
    console.debug('BrandComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

}
