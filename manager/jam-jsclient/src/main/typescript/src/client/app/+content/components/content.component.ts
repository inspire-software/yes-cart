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
import {ShopVO, ContentVO, ContentWithBodyVO, AttrValueContentVO, ContentBodyVO, Pair, ValidationRequestVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {WindowMessageEventBus} from './../../shared/services/index';
import {UiUtil} from './../../shared/ui/index';
import {I18nComponent} from './../../shared/i18n/index';
import {ContentSelectComponent} from './../../shared/content/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {ModalComponent} from './../../shared/modal/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';


@Component({
  selector: 'yc-content',
  moduleId: module.id,
  templateUrl: 'content.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, AttributeValuesComponent, ContentSelectComponent, I18nComponent, ModalComponent],
})

export class ContentComponent implements OnInit, OnDestroy {

  @Input()
  shop:ShopVO = null;

  _content:ContentWithBodyVO;
  _attributes:AttrValueContentVO[] = [];
  attributeFilter:string;

  _changes:Array<Pair<AttrValueContentVO, boolean>>;

  selectedRow:AttrValueContentVO;

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ContentVO, Array<Pair<AttrValueContentVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ContentVO, Array<Pair<AttrValueContentVO, boolean>>>>>();

  changed:boolean = false;
  validForSave:boolean = false;
  delayedChange:Future;

  contentForm:any;
  contentFormSub:any;

  winSub:any;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('contentParentSelectComponent')
  contentParentSelectComponent:ContentSelectComponent;

  constructor(fb: FormBuilder) {
    console.debug('ContentComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (!that.changed || uri == null || uri == '' || that._content == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'content', subjectId: that._content.contentId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changed || code == null || code == '' || that._content == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'content', subjectId: that._content.contentId, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.contentForm = fb.group({
      'guid': ['', validCode],
      'parentName': ['', Validators.required],
      'description': [''],
      'rank': ['', YcValidators.requiredRank],
      'uitemplate': ['', YcValidators.nonBlankTrimmed],
      'availablefrom': ['', YcValidators.validDate],
      'availableto': ['', YcValidators.validDate],
      'uri': ['', validUri],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.winSub = WindowMessageEventBus.getWindowMessageEventBus().messageUpdated$.subscribe(content => {
      if (this._content != null && this._content.contentBodies != null) {
        let _update:any = content.data;
        let _body:ContentBodyVO = this._content.contentBodies.find(body => {
          return body.lang == _update.lang;
        });
        console.debug('ContentComponent update', _body, _update);
        if (_body != null) {
          _body.text = _update.text;
          this.formChange();
        }
      }
    });

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.contentForm.controls) {
      this.contentForm.controls[key]['_pristine'] = true;
      this.contentForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.contentFormSub = this.contentForm.statusChanges.subscribe((data:any) => {
      this.validForSave = this.contentForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
  }


  formUnbind():void {
    if (this.contentFormSub) {
      console.debug('ContentComponent unbining form');
      this.contentFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('ContentComponent validating formGroup is valid: ' + this.validForSave, this._content);
    this.dataChanged.emit({ source: new Pair(this._content, this._changes), valid: this.validForSave });
  }

  @Input()
  set content(content:ContentWithBodyVO) {
    this._content = content;
    this.changed = false;
    this.formReset();
  }

  get content():ContentWithBodyVO {
    return this._content;
  }

  get availableto():string {
    return UiUtil.dateInputGetterProxy(this._content, 'availableto');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this._content, 'availableto', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this._content, 'availablefrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this._content, 'availablefrom', availablefrom);
  }

  @Input()
  set attributes(attributes:AttrValueContentVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueContentVO[] {
    return this._attributes;
  }

  onMainDataChange(event:any) {
    console.debug('ContentComponent main data changed', this._content);
    this.changed = true;
  }

  onAttributeDataChanged(event:any) {
    console.debug('ContentComponent attr data changed', this._content);
    this.changed = true;
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    console.debug('ContentComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ContentComponent ngOnDestroy');
    this.formUnbind();
    this.winSub.unsubscribe();
  }

  tabSelected(tab:any) {
    console.debug('ContentComponent tabSelected', tab);
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

  protected onSelectRow(row:AttrValueContentVO) {
    console.debug('ContentComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onEditParent() {
    console.debug('ContentComponent onEditParent handler');
    this.contentParentSelectComponent.showDialog();
  }

  protected onContentParentSelected(event:FormValidationEvent<ContentVO>) {
    console.debug('ContentComponent onContentParentSelected handler', event);
    if (event.valid) {
      this.content.parentId = event.source.contentId;
      this.content.parentName = event.source.name;
      this.changed = true;
    }
  }

  protected onCMSEdit(body:ContentBodyVO) {

    let myWindow = window.open('/yes-manager/resources/assets/editor/tinymce/editor.html', 'CMS', 'width=800,height=660');
    myWindow.onload = function() {

      let msg = body;
      myWindow.postMessage(msg, '*');

    };

  }

  protected getCMSPreview(body:ContentBodyVO) {
    return body.text;
  }

}
