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
import {Component, OnInit, OnDestroy, OnChanges, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf, NgFor, CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../pagination/index';
import {AttrValueVO, AttributeVO, Pair} from './../model/index';
import {ShopService, ShopEventBus, Util} from './../services/index';
import {DataControlComponent} from './../sidebar/index';
import {I18nComponent} from './../i18n/index';
import {ModalComponent, ModalResult, ModalAction} from './../modal/index';
import {ProductAttributeSelectComponent} from './product-attribute-select.component';
import {YcValidators} from './../validation/validators';
import {FormValidationEvent, Futures, Future} from './../event/index';
import {Config} from './../config/env.config';


@Component({
  selector: 'yc-attribute-values',
  moduleId: module.id,
  templateUrl: 'attribute-values.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, I18nComponent, ProductAttributeSelectComponent]
})

export class AttributeValuesComponent implements OnInit, OnChanges {

  @Input() masterObject:any;
  @Input() avPrototype:AttrValueVO;

  @Input() showHelp:boolean = false;

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;


  _objectAttributes:Array<AttrValueVO>;
  objectAttributesRemove:Array<number>;
  objectAttributesEdit:Array<number>;
  _attributeFilter:string;
  filteredObjectAttributes:Array<AttrValueVO>;
  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  @ViewChild('addModalDialog')
  addModalDialog:ModalComponent;

  selectedRow:AttrValueVO;
  selectedAttribute:AttributeVO;

  attributeToEdit:AttrValueVO;
  attributeToEditImagePreviewAvailable:boolean = true;
  attributeToEditImagePreview:string = '';

  @Output() dataSelected: EventEmitter<AttrValueVO> = new EventEmitter<AttrValueVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<AttrValueVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<AttrValueVO, boolean>>>>();

  /**
   * Construct attribute panel
   */
  constructor() {
    console.debug('AttributeValuesComponent constructed');

    this.attributeToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set objectAttributes(objectAttributes:Array<AttrValueVO>) {
    this._objectAttributes = objectAttributes;
    this.loadData();
  }

  get objectAttributes():Array<AttrValueVO> {
    return this._objectAttributes;
  }

  @Input()
  set attributeFilter(attributeFilter:string) {
    this._attributeFilter = attributeFilter;
    this.delayedFiltering.delay();
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('AttributeValuesComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    console.debug('AttributeValuesComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }

  private loadData() {
    if (this.masterObject && this._objectAttributes) {

      console.debug('AttributeValuesComponent attributes', this._objectAttributes);
      this.objectAttributesRemove = [];
      this.objectAttributesEdit = [];
      this.filterAttributes();

    } else {

      this.objectAttributesRemove = null;
      this.objectAttributesEdit = null;
      this.filteredObjectAttributes = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }

  /**
   * Row delete handler.
   * @param row attribute to delete.
   */
  protected onRowDelete(row:AttrValueVO) {
    console.debug('AttributeValuesComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:AttrValueVO) {
    console.debug('AttributeValuesComponent onRowEdit handler', row);
    this.validForSave = false;
    this.attributeToEdit = Util.clone(row);
    this.processImageView(this.attributeToEdit);
    this.editModalDialog.show();
  }

  public onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  public onRowAdd() {
    if (this.avPrototype != null) {
      this.addModalDialog.show();
    }
  }

  protected onAttributeSelected(row:AttributeVO) {
    this.selectedAttribute = row;
  }

  protected onAttributeAddModalResult(modalresult: ModalResult) {
    console.debug('AttributeValuesComponent onAttributeAddModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.selectedAttribute.code;} );
      if (idx != -1) {
        this.onRowEdit(this._objectAttributes[idx]);
      } else {
        let av = Util.clone(this.avPrototype);
        av.attribute = this.selectedAttribute;
        this.onRowEdit(av);
      }
    } else {
      this.selectedAttribute = null;
    }
  }

  protected onSelectRow(row:AttrValueVO) {
    console.debug('AttributeValuesComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  onDataChange(event:any) {

    let val = this.attributeToEdit.val;
    let typ = this.attributeToEdit.attribute.etypeName;
    let customRegEx = this.attributeToEdit.attribute.regexp;

    if (customRegEx) {
      let regex = new RegExp(customRegEx);
      this.validForSave = regex.test(val);
    } else {
      switch (typ) {
        case 'Integer':
          this.validForSave = /^[\-]?[0-9]+$/.test(val);
          break;
        case 'Float':
          this.validForSave = /^(([\-][0-9]+)|([0-9]*))[\.]?[0-9]+$/.test(val);
          break;
        case 'Phone':
          this.validForSave = /^[\+]?[\(\)0-9\- ]+$/.test(val);
          break;
        case 'Email':
          this.validForSave = /^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/.test(val);
          break;
        case 'Date':
          this.validForSave = /^[0-9]{4}\-([0][1-9]|[1][0-2])\-([0][1-9]|[1-2][0-9]|[3][0-1])( ([0][0-9]|[1][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9])?$/.test(val);
          break;
        case 'Any':
          this.validForSave = true;
          break;
        default:
          this.validForSave = val != null && /\S+(.*\S)*/.test(val);
          break;
      }
    }

    console.debug('AttributeValuesComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  private processDataChangesEvent() {

    console.debug('AttributeValuesComponent data changes', this.masterObject);
    if (this.masterObject && this._objectAttributes) {

      let _update = <Array<Pair<AttrValueVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        if ((attr.attrvalueId !== 0 && this.isEditedAttribute(attr)) || (attr.attrvalueId === 0 && attr.val !== null && /\S+(.*\S)*/.test(attr.val) && !(/\* .+/.test(attr.val)))) {
          _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
        }
      });

      console.debug('AttributeValuesComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('AttributeValuesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.attrvalueId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.attribute.code === this.selectedRow.attribute.code;
        });
        this._objectAttributes[idx].val = null;
        console.debug('AttributeValuesComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        console.debug('AttributeValuesComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        this.objectAttributesRemove.push(attrToDelete);
        this.objectAttributesEdit.push(attrToDelete);
      }
      this.filterAttributes();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    console.debug('AttributeValuesComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.attrvalueId === 0) { // add new
        console.debug('AttributeValuesComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.attributeToEdit.attribute.code;} );
        if (idx != -1) {
          this._objectAttributes[idx] = this.attributeToEdit;
        } else {
          this._objectAttributes.push(this.attributeToEdit);
        }
      } else { // edit existing
        console.debug('AttributeValuesComponent onEditModalResult update existing', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attrvalueId === this.attributeToEdit.attrvalueId;} );
        this._objectAttributes[idx] = this.attributeToEdit;
        this.objectAttributesEdit.push(this.attributeToEdit.attrvalueId);
      }
      this.selectedRow = this.attributeToEdit;
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    } else {
      this.attributeToEdit = null;
    }
  }

  private filterAttributes() {
    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    if (_filter) {
      if (_filter === '###') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.val != null && val.val != '' && val.attrvalueId > 0
        );
      } else if (_filter === '##0') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.val != null && val.val != ''
        );
      } else if (_filter === '#00') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.val != null && val.val != '' && val.attrvalueId == 0
        );
      } else if (_filter === '#0#') {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          this.isEditedAttribute(val) || (val.attrvalueId == 0 && val.val != null && val.val != '' && val.val.indexOf('* ') !== 0)
        );
      } else {
        this.filteredObjectAttributes = this._objectAttributes.filter(val =>
          val.attribute.code.toLowerCase().indexOf(_filter) !== -1 ||
          val.attribute.name.toLowerCase().indexOf(_filter) !== -1 ||
          val.attribute.description && val.attribute.description.toLowerCase().indexOf(_filter) !== -1 ||
          val.val && val.val.toLowerCase().indexOf(_filter) !== -1
        );
      }
      console.debug('AttributeValuesComponent filterAttributes ' +  _filter, this.filteredObjectAttributes);
    } else {
      this.filteredObjectAttributes = this._objectAttributes;
      console.debug('AttributeValuesComponent filterAttributes no filter', this.filteredObjectAttributes);
    }

    if (this.filteredObjectAttributes === null) {
      this.filteredObjectAttributes = [];
    }

    let _total = this.filteredObjectAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  isRemovedAttribute(row:AttrValueVO):boolean {
    return this.objectAttributesRemove.indexOf(row.attrvalueId) !== -1;
  }

  isEditedAttribute(row:AttrValueVO):boolean {
    return this.objectAttributesEdit.indexOf(row.attrvalueId) !== -1;
  }

  isNewAttribute(row:AttrValueVO):boolean {
    return row.attrvalueId == 0 && row.val != null && row.val != '' && row.val.indexOf('* ') !== 0;
  }

  isInheritedAttribute(row:AttrValueVO):boolean {
    return row.attrvalueId == 0 && row.val != null && row.val != '' && row.val.indexOf('* ') === 0;
  }

  getAttributeColor(row:AttrValueVO, removed:string, edited:string, added:string, inherited:string, prestine:string) {

    if (this.isRemovedAttribute(row)) {
      return removed;
    }

    if (this.isEditedAttribute(row)) {
      return edited;
    }

    if (this.isNewAttribute(row)) {
      return added;
    }

    if (this.isInheritedAttribute(row)) {
      return inherited;
    }

    return prestine;
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  public getDisplayValue(row:AttrValueVO):string {
    if (row.val != null) {
      if (row.attribute.etypeName === 'Boolean') {
        if (('' + row.val)  === 'true') {
          return '<i class="fa fa-check-circle"></i>';
        } else {
          return '<i class="fa fa-times-circle"></i>';
        }
      } else if (row.attribute.etypeName === 'HTML') {
        return '<pre>' + row.val + '</pre>';
      } else if (row.attribute.etypeName === 'Image' && row.valBase64Data) {
        return '<img class="av-image-thumb" src="' + row.valBase64Data + '" alt="' + row.val + '"/>';
      }
      return row.val;
    }
    return '&nbsp;';
  }

  isBooleanEditor():boolean {
    return this.attributeToEdit && this.attributeToEdit.attribute.etypeName === 'Boolean';
  }

  isImageEditor():boolean {
    return this.attributeToEdit && this.attributeToEdit.attribute.etypeName === 'Image';
  }

  isLocalisableEditor():boolean {
    return this.attributeToEdit && this.attributeToEdit.attribute.etypeName === 'String';
  }

  isTextAreaEditor():boolean {
    return this.attributeToEdit &&
      (this.attributeToEdit.attribute.etypeName === 'CommaSeparatedList' || this.attributeToEdit.attribute.etypeName === 'HTML' || this.attributeToEdit.attribute.etypeName === 'Any');
  }

  isMiniTextEditor():boolean {
    return this.attributeToEdit &&
      (this.attributeToEdit.attribute.etypeName === 'Float' || this.attributeToEdit.attribute.etypeName === 'Integer'
      || this.attributeToEdit.attribute.etypeName === 'Date');
  }

  isTextEditor():boolean {
    return this.attributeToEdit && !this.isBooleanEditor() && !this.isImageEditor() && !this.isLocalisableEditor() && !this.isTextAreaEditor() && !this.isMiniTextEditor();
  }

  get attributeToEditBoolean():boolean {
    // Must use get/set because there is string to boolean conversion happens somewhere in binding,
    // so: ngTrueValue="'true'" ngFalseValue="'false'" does not work
    return this.attributeToEdit && ('' + this.attributeToEdit.val === 'true');
  }

  set attributeToEditBoolean(val:boolean) {
    this.attributeToEdit.val = '' + val;
  }

  processImageView(av:AttrValueVO) {
    if (av.attribute.etypeName === 'Image') {
      if (av.val != null) {
        this.attributeToEditImagePreviewAvailable = av.valBase64Data != null;
        if (this.attributeToEditImagePreviewAvailable) {
          this.attributeToEditImagePreview = '<img src="' + av.valBase64Data + '"/>';
        } else {
          this.attributeToEditImagePreview = '&nbsp;';
        }
      } else {
        this.attributeToEditImagePreviewAvailable = true;
        this.attributeToEditImagePreview = '&nbsp;';
      }
    }
  }

  isFileUploadDisabled():boolean {
    var input:any = document.getElementById('avmodaluploadimage');
    return input == null || input.disabled;
  }

  onFileClickRelay() {
    console.debug("AttributeValuesComponent file upload relay button click");
    document.getElementById('avmodaluploadimage').click();
  }

  onImageFileSelected(event:any) {
    var srcElement:any = event.srcElement;
    var image:any = srcElement.files[0];
    console.debug("AttributeValuesComponent image file selected", image.name);
    var reader:FileReader = new FileReader();

    let that = this;

    reader.onloadend = function(e:any) {
      console.debug("AttributeValuesComponent image file loaded", e.target.result);
      that.attributeToEdit.val = image.name;
      that.attributeToEdit.valBase64Data = e.target.result;
      that.processImageView(that.attributeToEdit);
      that.changed = true;
      that.validForSave = true;
      srcElement.value = '';
    };
    reader.readAsDataURL(image);
  }

}
