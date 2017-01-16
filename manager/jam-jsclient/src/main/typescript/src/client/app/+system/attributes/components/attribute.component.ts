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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../../shared/validation/validators';
import { EtypeVO, AttributeVO, ValidationRequestVO } from './../../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../../shared/event/index';
import { UiUtil } from './../../../shared/ui/index';
import { LogUtil } from './../../../shared/log/index';

@Component({
  selector: 'yc-attribute',
  moduleId: module.id,
  templateUrl: 'attribute.component.html',
})

export class AttributeComponent implements OnInit, OnDestroy {

  @Input() etypes:Array<EtypeVO> = [];

  @Output() dataChanged: EventEmitter<FormValidationEvent<AttributeVO>> = new EventEmitter<FormValidationEvent<AttributeVO>>();

  private _attribute:AttributeVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private attributeForm:any;
  private attributeFormSub:any; // tslint:disable-line:no-unused-variable

  constructor(fb: FormBuilder) {
    LogUtil.debug('AttributeComponent constructed');

    let that = this;

    let validCode = function(control:any):any {

      let basic = Validators.required(control);
      if (basic == null) {

        let code = control.value;
        if (that._attribute == null || !that.attributeForm || (!that.attributeForm.dirty && that._attribute.attributeId > 0)) {
          return null;
        }

        basic = YcValidators.validSeoUri(control);
        if (basic == null) {
          var req:ValidationRequestVO = {
            subject: 'attribute',
            subjectId: that._attribute.attributeId,
            field: 'code',
            value: code
          };
          return YcValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };


    this.attributeForm = fb.group({
      'code': ['', validCode],
      'etypeId': ['', YcValidators.requiredPk],
      'rank': ['', YcValidators.requiredRank],
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
      'name': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);
  }

  formBind():void {
    UiUtil.formBind(this, 'attributeForm', 'attributeFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'attributeFormSub');
  }

  formChange():void {
    LogUtil.debug('AttributeComponent formChange', this.attributeForm.valid, this._attribute);
    this.dataChanged.emit({ source: this._attribute, valid: this.attributeForm.valid });
  }

  @Input()
  set attribute(attribute:AttributeVO) {

    UiUtil.formInitialise(this, 'initialising', 'attributeForm', '_attribute', attribute, attribute != null && attribute.attributeId > 0, ['code']);

  }

  get attribute():AttributeVO {
    return this._attribute;
  }

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'attributeForm', 'name', event);
  }

  onRegExpDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'attributeForm', 'regexp', event);
  }

  onValDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'attributeForm', 'val', event);
  }

  onDataChangePrimary(event:any) {
    LogUtil.debug('AttributeComponent data changed', this._attribute);
    if (this._attribute.primary && !this._attribute.search) {
      setTimeout(() => {
        this._attribute.search = true;
      }, 100);
    }
  }

  onDataChangeSearch(event:any) {
    LogUtil.debug('AttributeComponent data changed', this._attribute);
    if (this._attribute.primary && !this._attribute.search) {
      setTimeout(() => {
        this._attribute.primary = false;
      }, 100);
    }
  }

  ngOnInit() {
    LogUtil.debug('AttributeComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('AttributeComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('AttributeComponent tabSelected', tab);
  }



}
