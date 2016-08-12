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
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {EtypeVO, AttributeVO} from './../../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../../shared/event/index';
import {I18nComponent} from './../../../shared/i18n/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-attribute',
  moduleId: module.id,
  templateUrl: 'attribute.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, I18nComponent],
})

export class AttributeComponent implements OnInit, OnDestroy {

  @Input()
  etypes:Array<EtypeVO> = [];

  _attribute:AttributeVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<AttributeVO>> = new EventEmitter<FormValidationEvent<AttributeVO>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  attributeForm:any;
  attributeFormSub:any;

  constructor(fb: FormBuilder) {
    console.debug('AttributeComponent constructed');

    this.attributeForm = fb.group({
      'code': ['', Validators.compose([Validators.required, Validators.pattern('[A-Za-z0-9\-_]+')])],
      'etypeId': ['', Validators.compose([Validators.required, Validators.pattern('[1-9][0-9]*')])],
      'rank': ['', Validators.compose([Validators.required, Validators.pattern('[0-9]+')])],
      'description': [''],
      'val': [''],
      'mandatory': [''],
      'allowduplicate': [''],
      'allowfailover': [''],
      'regexp': [''],
      'store': [''],
      'search': [''],
      'primary': [''],
      'navigation': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.attributeForm.controls) {
      this.attributeForm.controls[key]['_pristine'] = true;
      this.attributeForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.attributeFormSub = this.attributeForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.attributeForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.attributeFormSub) {
      console.debug('AttributeComponent unbining form');
      this.attributeFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('AttributeComponent validating formGroup is valid: ' + this.validForSave, this._attribute);
    this.dataChanged.emit({ source: this._attribute, valid: this.validForSave });
  }

  @Input()
  set attribute(attribute:AttributeVO) {
    this._attribute = attribute;
    this.changed = false;
    this.formReset();
  }

  get attribute():AttributeVO {
    return this._attribute;
  }

  onDataChange(event:any) {
    console.debug('AttributeComponent data changed', this._attribute);
    this.changed = true;
  }

  onDataChangePrimary(event:any) {
    console.debug('AttributeComponent data changed', this._attribute);
    if (this._attribute.primary && !this._attribute.search) {
      this._attribute.search = true;
    }
    this.changed = true;
  }

  onDataChangeSearch(event:any) {
    console.debug('AttributeComponent data changed', this._attribute);
    if (this._attribute.primary && !this._attribute.search) {
      this._attribute.primary = false;
    }
    this.changed = true;
  }

  ngOnInit() {
    console.debug('AttributeComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('AttributeComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('AttributeComponent tabSelected', tab);
  }



}
