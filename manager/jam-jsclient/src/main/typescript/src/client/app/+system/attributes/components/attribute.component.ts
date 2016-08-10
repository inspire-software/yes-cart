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
import {FormValidationEvent} from './../../../shared/event/index';
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

  validForSave:boolean = false;

  attributeForm:any;

  constructor(fb: FormBuilder) {
    console.debug('AttributeComponent constructed');

    this.attributeForm = fb.group({
      'code': ['', Validators.compose([Validators.required, Validators.pattern('[A-Za-z0-9\-]+')])],
      'etypeId': ['', Validators.required],
      'rank': ['', Validators.compose([Validators.required, Validators.pattern('[0-9]+')])],
      'description': ['', Validators.pattern('\S+.*\S+')],
      'val': ['', Validators.pattern('\S+.*\S+')],
      'mandatory': [''],
      'allowduplicate': [''],
      'allowfailover': [''],
      'regexp': [''],
      'store': [''],
      'search': [''],
      'primary': [''],
      'navigation': [''],
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.attributeForm.controls) {
      this.attributeForm.controls[key]['_pristine'] = true;
      this.attributeForm.controls[key]['_touched'] = false;
    }
  }

  @Input()
  set attribute(attribute:AttributeVO) {
    this._attribute = attribute;
    this.formReset();
  }

  get attribute():AttributeVO {
    return this._attribute;
  }

  onDataChange(event:any) {
    var _sub:any = this.attributeForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.attributeForm.valid && (this._attribute.name != null) && (/\S+.*\S+/.test(this._attribute.name));
      console.debug('AttributeComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
      this.dataChanged.emit({ source: this._attribute, valid: this.validForSave });
    });
    console.debug('AttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onI18nDataChange() {
    this.validForSave = this.attributeForm.valid && (this._attribute.name != null) && (/\S+.*\S+/.test(this._attribute.name));
    console.debug('SlaComponent form changed i18n and ' + (this.validForSave ? 'is valid' : 'is NOT valid'));
    this.dataChanged.emit({ source: this._attribute, valid: this.validForSave });
  }

  ngOnInit() {
    console.debug('AttributeComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('AttributeComponent ngOnDestroy');
  }

  tabSelected(tab:any) {
    console.debug('AttributeComponent tabSelected', tab);
  }



}
