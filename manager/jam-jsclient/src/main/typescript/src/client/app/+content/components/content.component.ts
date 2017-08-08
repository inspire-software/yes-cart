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
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, ContentVO, ContentWithBodyVO, AttrValueContentVO, ContentBodyVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { WindowMessageEventBus } from './../../shared/services/index';
import { UiUtil } from './../../shared/ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { ContentSelectComponent } from './../../shared/content/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { LogUtil } from './../../shared/log/index';
import { LRUCache } from './../../shared/model/internal/cache.model';

@Component({
  selector: 'yc-content',
  moduleId: module.id,
  templateUrl: 'content.component.html',
})

export class ContentComponent implements OnInit, OnDestroy {

  @Input() shop:ShopVO = null;

  @Input() shopPreviewUrl:string = '/';
  @Input() shopPreviewCss:string = '/';

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<ContentVO, Array<Pair<AttrValueContentVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<ContentVO, Array<Pair<AttrValueContentVO, boolean>>>>>();

  private _content:ContentWithBodyVO;
  private _attributes:AttrValueContentVO[] = [];
  private attributeFilter:string;

  private _changes:Array<Pair<AttrValueContentVO, boolean>>;

  private selectedRow:AttrValueContentVO;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private contentForm:any;
  private contentFormSub:any; // tslint:disable-line:no-unused-variable

  private winSub:any;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('contentParentSelectComponent')
  private contentParentSelectComponent:ContentSelectComponent;

  @ViewChild('rawEditConfirmationModalDialog')
  private rawEditConfirmationModalDialog:ModalComponent;

  private bodyEdit:ContentBodyVO;
  private bodyEditChange:string;

  private searchHelpShow:boolean = false;

  private cache:LRUCache = new LRUCache(10);

  constructor(fb: FormBuilder) {
    LogUtil.debug('ContentComponent constructed');

    let that = this;

    let validUri = function(control:any):any {

      let uri = control.value;
      if (uri == null || uri == '' || that._content == null || !that.contentForm || (!that.contentForm.dirty && that._content.contentId > 0)) {
        return null;
      }

      let basic = YcValidators.validSeoUri(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'content', subjectId: that._content.contentId, field: 'uri', value: uri };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    let validCode = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that._content == null || !that.contentForm || (!that.contentForm.dirty && that._content.contentId > 0)) {
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
      'name': [''],
      'title': [''],
      'keywords': [''],
      'meta': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.winSub = WindowMessageEventBus.getWindowMessageEventBus().messageUpdated$.subscribe(content => {
      if (this._content != null && this._content.contentBodies != null) {
        let _update:any = content.data;

        if (_update.loaded) { // pop up ready

          LogUtil.debug('ContentComponent onCMSEdit window is ready', _update.loaded);

          let windowAndMsg:any = this.cache.getValue(_update.loaded);
          if (windowAndMsg != null) {
            LogUtil.debug('ContentComponent onCMSEdit posting message', windowAndMsg.msg);
            windowAndMsg.window.postMessage(windowAndMsg.msg, '*');
          }
        } else { // actual update message
          let _body:ContentBodyVO = this._content.contentBodies.find(body => {
            return body.lang == _update.lang;
          });
          LogUtil.debug('ContentComponent update', _body, _update);
          if (_body != null) {
            _body.text = _update.text;
            this.formChange();
          }
        }
      }
    });

  }

  formBind():void {
    UiUtil.formBind(this, 'contentForm', 'contentFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'contentFormSub');
  }

  formChange():void {
    LogUtil.debug('ContentComponent formChange', this.contentForm.valid, this._content);
    this.dataChanged.emit({ source: new Pair(this._content, this._changes), valid: this.contentForm.valid });
  }

  @Input()
  set content(content:ContentWithBodyVO) {

    UiUtil.formInitialise(this, 'initialising', 'contentForm', '_content', content);
    this._changes = [];

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

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'contentForm', 'name', event);
  }

  onTitleDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'contentForm', 'title', event);
  }

  onKeywordsDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'contentForm', 'keywords', event);
  }

  onMetaDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'contentForm', 'meta', event);
  }

  @Input()
  set attributes(attributes:AttrValueContentVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueContentVO[] {
    return this._attributes;
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('ContentComponent attr data changed', this._content);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('ContentComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ContentComponent ngOnDestroy');
    this.formUnbind();
    this.winSub.unsubscribe();
  }

  tabSelected(tab:any) {
    LogUtil.debug('ContentComponent tabSelected', tab);
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
    LogUtil.debug('ContentComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onEditParent() {
    LogUtil.debug('ContentComponent onEditParent handler');
    this.contentParentSelectComponent.showDialog(this.content.parentId);
  }

  protected onContentParentSelected(event:FormValidationEvent<ContentVO>) {
    LogUtil.debug('ContentComponent onContentParentSelected handler', event);
    if (event.valid) {
      this.content.parentId = event.source.contentId;
      this.content.parentName = event.source.name;
      this.delayedChange.delay();
    }
  }

  protected onCMSEdit(body:ContentBodyVO) {

    let _shop = this.shop;
    let _docBase = this.shopPreviewUrl;
    let _css = this.shopPreviewCss;

    let msg = {
      shop: _shop,
      docBase: _docBase,
      previewCss: _css,
      content: body
    };

    let id = 'cms' + Math.random();
    let myWindow = window.open('/yes-manager/resources/assets/editor/tinymce/editor.html?lang=' + body.lang + '&id=' + id, 'CMS', 'width=800,height=660');

    LogUtil.debug('ContentComponent onCMSEdit', msg);

    this.cache.putValue(id, {
      window: myWindow,
      msg: msg
    }, 5000);

    //myWindow.addEventListener('message', function(event) {
    //  myWindow['editorInit'](event);
    //});

    //setTimeout(function() {
    //  myWindow.postMessage(msg, '*');
    //}, 300);


  }

  protected onCMSEditRaw(body:ContentBodyVO) {

    this.bodyEdit = body;
    this.bodyEditChange = body != null ? body.text : '';
    this.rawEditConfirmationModalDialog.show();

  }


  protected onEditConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ContentComponent onEditConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.bodyEdit != null) {
        LogUtil.debug('ContentComponent onDeleteConfirmationResult', this.bodyEdit);
        this.bodyEdit.text = this.bodyEditChange;
        this.bodyEditChange = null;
        this.bodyEdit = null;
        this.formChange();
      }
    }
  }


  protected getCMSPreview(body:ContentBodyVO) {

    if (body.text && this.shopPreviewUrl !== null && this.shopPreviewUrl !== '/') {

      let _previewBase = this.shopPreviewUrl;
      let _preview = body.text;
      // replace relative images
      _preview = _preview.replace(/<img([^>]*)\ssrc=(['"])\/{0,1}([^'"]*)\2([^>]*)/gi, '<img$1 data-preview="enhanced" src=$2' + _previewBase + '$3$2$4');
      // replace relative urls
      _preview = _preview.replace(/href=(['"])\/{0,1}([^'"]*)\1/gi, 'href=$1' + _previewBase + '$2$1  data-preview="enhanced" target="_blank"');

      LogUtil.debug('ContentComponent getCMSPreview ', _preview);

      return _preview;

    }

    return body.text;
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
