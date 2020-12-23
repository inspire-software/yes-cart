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
import { Component, OnInit, OnChanges, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { AttrValueVO, AttributeVO, Pair } from './../model/index';
import { ImpexService, I18nEventBus, Util } from './../services/index';
import { UiUtil } from './../ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { FormValidationEvent, Futures, Future } from './../event/index';
import { Config } from './../../../environments/environment';

import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-attribute-values',
  templateUrl: 'attribute-values.component.html',
})

export class AttributeValuesComponent implements OnInit, OnChanges {

  @Input() masterObject:any;
  @Input() avPrototype:AttrValueVO;

  @Input() masterObjectType:string;

  @Input() showHelp:boolean = false;

  @Output() dataSelected: EventEmitter<AttrValueVO> = new EventEmitter<AttrValueVO>();

  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<Pair<AttrValueVO, boolean>>>> = new EventEmitter<FormValidationEvent<Array<Pair<AttrValueVO, boolean>>>>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  //sorting
  public sortColumn:string = 'name';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;


  private _objectAttributes:Array<AttrValueVO>;
  private objectAttributesRemove:Array<number>;
  private objectAttributesEdit:Array<number>;
  private _imageOnlyMode:boolean;
  private _attributeFilter:string;
  public filteredObjectAttributes:Array<AttrValueVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  @ViewChild('addModalDialog')
  private addModalDialog:ModalComponent;

  public selectedRow:AttrValueVO;
  private selectedAttribute:AttributeVO;

  public attributeToEdit:AttrValueVO;
  public attributeToEditImagePreviewAvailable:boolean = true;
  public attributeToEditImagePreview:string = '';

  public booleanEditor:boolean = false;
  public miniTextEditor:boolean = false;
  public textEditor:boolean = false;
  public textAreaEditor:boolean = false;
  public selectEditor:boolean = false;
  public selectEditorValues:Pair<string, string>[] = null;
  public localisableEditor:boolean = false;
  public imageEditor:boolean = false;
  public fileEditor:boolean = false;
  public lockedEditor:boolean = false;

  public loading:boolean = false;
  public loadingFilter:boolean = false;

  /**
   * Construct attribute panel
   */
  constructor(private _impexService:ImpexService) {
    LogUtil.debug('AttributeValuesComponent constructed');

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

  @Input()
  set sortorder(sort:Pair<string, boolean>) {
    if (sort != null && (sort.first !== this.sortColumn || sort.second !== this.sortDesc)) {
      this.sortColumn = sort.first;
      this.sortDesc = sort.second;
      this.delayedFiltering.delay();
    }
  }

  @Input()
  set imageOnlyMode(value: boolean) {
    this._imageOnlyMode = value;
    this.delayedFiltering.delay();
  }

  get imageOnlyMode(): boolean {
    return this._imageOnlyMode;
  }

  set attributeToEditBoolean(val:boolean) {
    this.attributeToEdit.val = '' + val;
  }

  get attributeToEditBoolean():boolean {
    // Must use get/set because there is string to boolean conversion happens somewhere in binding,
    // so: ngTrueValue="'true'" ngFalseValue="'false'" does not work
    return this.attributeToEdit && ('' + this.attributeToEdit.val === 'true');
  }

  ngOnInit() {
    LogUtil.debug('AttributeValuesComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('AttributeValuesComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }


  onRowAdd() {
    if (this.avPrototype != null) {
      this.addModalDialog.show();
    }
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  onRowDelete(row:AttrValueVO) {
    LogUtil.debug('AttributeValuesComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  onRowEdit(row:AttrValueVO) {
    LogUtil.debug('AttributeValuesComponent onRowEdit handler', row);
    this.validForSave = false;
    this.attributeToEdit = Util.clone(row);
    this.detectAttributeEditor(this.attributeToEdit);
    this.processImageView(this.attributeToEdit);
    this.editModalDialog.show();
  }

  onAttributeSelected(row:AttributeVO) {
    this.selectedAttribute = row;
  }

  onAttributeAddModalResult(modalresult: ModalResult) {
    LogUtil.debug('AttributeValuesComponent onAttributeAddModalResult modal result is ', modalresult);
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

  onSelectRow(row:AttrValueVO) {
    LogUtil.debug('AttributeValuesComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  onDataChange(event:any) {

    let val = this.attributeToEdit.val;
    let typ = this.attributeToEdit.attribute.etype;
    let customRegEx = this.attributeToEdit.attribute.regexp;

    if (customRegEx) {
      let regex = new RegExp(customRegEx);
      this.validForSave = regex.test(val);
    } else {
      switch (typ) {
        case 'Integer':
        case 'Timestamp':
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
        case 'DateTime':
          this.validForSave = /^[0-9]{4}\-([0][1-9]|[1][0-2])\-([0][1-9]|[1-2][0-9]|[3][0-1])( ([0][0-9]|[1][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9])?$/.test(val);
          break;
        case 'Date':
          this.validForSave = /^[0-9]{4}\-([0][1-9]|[1][0-2])\-([0][1-9]|[1-2][0-9]|[3][0-1])?$/.test(val);
          break;
        case 'Any':
          this.validForSave = true;
          break;
        default:
          this.validForSave = val != null && /\S+(.*\S)*/.test(val);
          break;
      }
    }

    LogUtil.debug('AttributeValuesComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AttributeValuesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.attrvalueId;
      if (attrToDelete === 0) {
        let idx = this._objectAttributes.findIndex(attrVo => {
          return attrVo.attribute.code === this.selectedRow.attribute.code;
        });
        this._objectAttributes[idx].val = null;
        LogUtil.debug('AttributeValuesComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        LogUtil.debug('AttributeValuesComponent onDeleteConfirmationResult attribute ' + attrToDelete);
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

  onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('AttributeValuesComponent onEditModalResult modal result is ', modalresult);
    this.detectAttributeEditor(null);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.attributeToEdit.attrvalueId === 0) { // add new
        LogUtil.debug('AttributeValuesComponent onEditModalResult add new attribute', this._objectAttributes);
        let idx = this._objectAttributes.findIndex(attrVo =>  {return attrVo.attribute.code === this.attributeToEdit.attribute.code;} );
        if (idx != -1) {
          this._objectAttributes[idx] = this.attributeToEdit;
        } else {
          this._objectAttributes.push(this.attributeToEdit);
        }
      } else { // edit existing
        LogUtil.debug('AttributeValuesComponent onEditModalResult update existing', this._objectAttributes);
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
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'name';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterAttributes();
    this.sortSelected.emit({ first: this.sortColumn, second: this.sortDesc });
  }

  getSearchFlags(row:AttributeVO) {
    if (this.masterObjectType === 'product') {
      let flags = '';
      if (row.store) {
        flags += '<i class="fa fa-save"></i>&nbsp;';
      }
      if (row.search) {
        if (row.primary) {
          flags += '<i class="fa fa-search-plus"></i>&nbsp;';
        } else {
          flags += '<i class="fa fa-search"></i>&nbsp;';
        }
      }
      if (row.navigation) {
        flags += '<i class="fa fa-list-alt"></i>&nbsp;';
      }
      return flags;
    }
    return '&nbsp;';
  }

  getDisplayValue(row:AttrValueVO):string {
    if (row.val != null) {
      if (row.attribute.etype === 'SecureString') {
        return '*****';
      }
      if (row.attribute.etype === 'Boolean') {
        if (('' + row.val)  === 'true') {
          return '<i class="fa fa-check-circle"></i>';
        } else {
          return '<i class="fa fa-times-circle"></i>';
        }
      } else if (row.attribute.etype === 'HTML' || row.attribute.etype === 'Properties') {
        return '<pre>' + row.val + '</pre>';
      } else if (row.attribute.etype === 'Image' && row.valBase64Data) {
        return '<img class="av-image-thumb" src="' + row.valBase64Data + '" alt="' + row.val + '"/>';
      }
      return row.val;
    }
    return '&nbsp;';
  }

  detectAttributeEditor(av:AttrValueVO) {

    this.booleanEditor = false;
    this.miniTextEditor = false;
    this.textEditor = false;
    this.textAreaEditor = false;
    this.selectEditor = false;
    this.selectEditorValues = null;
    this.localisableEditor = false;
    this.imageEditor = false;
    this.fileEditor = false;
    this.lockedEditor = false;

    if (av != null) {

      switch (av.attribute.etype) {
        case 'String':
        case 'SecureString':
          this.localisableEditor = true;
          break;
        case 'Boolean':
          this.booleanEditor = true;
          break;
        case 'Image':
          this.imageEditor = true;
          break;
        case 'File':
        case 'SystemFile':
          this.fileEditor = true;
          break;
        case 'CommaSeparatedList':
          let lang = I18nEventBus.getI18nEventBus().current();
          let _choice = UiUtil.toChoicePairs(av.attribute.choiceData, lang);
          if (_choice != null) {
            this.selectEditor = true;
            this.selectEditorValues = _choice;
          } else {
            this.textAreaEditor = true;
          }
          break;
        case 'HTML':
        case 'Any':
        case 'Properties':
          this.textAreaEditor = true;
          break;
        case 'Float':
        case 'Integer':
        case 'Date':
        case 'DateTime':
        case 'Timestamp':
          this.miniTextEditor = true;
          break;
        case 'Locked':
          this.lockedEditor = true;
          break;
        default:
          this.textEditor = true;
          break;
      }
    }
  }

  processImageView(av:AttrValueVO) {
    if (av.attribute.etype === 'Image') {
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

  isImageUploadDisabled():boolean {
    let input:any = document.getElementById('avmodaluploadimage');
    return input == null || input.disabled;
  }

  onImageClickRelay() {
    LogUtil.debug('AttributeValuesComponent image upload relay button click');
    document.getElementById('avmodaluploadimage').click();
  }

  onImageFileSelected(event:any) {
    let srcElement:any = event.target || event.srcElement;
    let image:any = srcElement.files[0];
    if (image != null) {
      let imageName:string = image.name;
      LogUtil.debug('AttributeValuesComponent image file selected', imageName);
      let reader:FileReader = new FileReader();

      this.loading = true;
      let that = this;

      reader.onloadend = function(e:any) {
        LogUtil.debug('AttributeValuesComponent image file loaded', e.target.result);
        that.attributeToEdit.val = imageName;
        that.attributeToEdit.valBase64Data = e.target.result;
        that.processImageView(that.attributeToEdit);
        that.changed = true;
        that.validForSave = true;
        that.loading = false;
        srcElement.value = '';
      };
      reader.readAsDataURL(image);
    }
  }


  isFileUploadDisabled():boolean {
    let input:any = document.getElementById('avmodaluploadfile');
    return input == null || input.disabled;
  }

  onFileClickRelay() {
    LogUtil.debug('AttributeValuesComponent file upload relay button click');
    document.getElementById('avmodaluploadfile').click();
  }


  onMediaFileSelected(event:any) {
    let srcElement:any = event.target || event.srcElement;
    let file:any = srcElement.files[0];
    if (file != null) {
      LogUtil.debug('AttributeValuesComponent media file selected', file.name);
      let reader:FileReader = new FileReader();

      this.loading = true;
      let that = this;

      reader.onloadend = function(e:any) {
        LogUtil.debug('AttributeValuesComponent media file loaded', e.target.result);
        that.attributeToEdit.val = file.name;
        that.attributeToEdit.valBase64Data = e.target.result;
        that.changed = true;
        that.validForSave = true;
        that.loading = false;
        srcElement.value = '';
      };
      reader.readAsDataURL(file);
    }
  }

  getAttributeName(attrVal:AttrValueVO):string {

    if (attrVal == null || attrVal.attribute == null) {
      return '';
    }

    let attr = attrVal.attribute;
    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = attr.displayNames;
    let def = attr.name != null ? attr.name : attr.code;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  downloadFile(fileVault:string, masterObjectType:string, fileName:string):void {
    let path = 'filevault/' + fileVault + '/' + masterObjectType + '?fileName=' + fileName + '&nocache=' + Math.random();
    this._impexService.downloadFile(path, fileName).subscribe(res => {
      LogUtil.debug('AttributeValuesComponent download', path, res);
    })
  }


  private loadData() {
    if (this.masterObject && this._objectAttributes) {

      LogUtil.debug('AttributeValuesComponent attributes', this._objectAttributes);
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


  private filterAttributes() {

    this.loadingFilter = true;

    let _filter = this._attributeFilter ? this._attributeFilter.toLowerCase() : null;
    let _filteredObjectAttributes:Array<AttrValueVO> = [];

    if (this._objectAttributes) {
      if (_filter) {
        if (_filter === '###') { // all existing values (check for blank is suppressed because sometimes we have empty values from imports)
          _filteredObjectAttributes = this._objectAttributes.filter(val =>
            /* val.val != null && val.val != '' && */ val.attrvalueId > 0
          );
        } else if (_filter === '##0') { // non-empty values
          _filteredObjectAttributes = this._objectAttributes.filter(val =>
            val.val != null && val.val != ''
          );
        } else if (_filter === '#00') { // non-empty new values
          _filteredObjectAttributes = this._objectAttributes.filter(val =>
            val.val != null && val.val != '' && val.attrvalueId == 0
          );
        } else if (_filter === '#0#') { // non-empty inherited
          _filteredObjectAttributes = this._objectAttributes.filter(val =>
            this.isEditedAttribute(val) || (val.attrvalueId == 0 && val.val != null && val.val != '' && val.val.indexOf('* ') !== 0)
          );
        } else {
          _filteredObjectAttributes = this._objectAttributes.filter(val =>
            val.attribute.code.toLowerCase().indexOf(_filter) !== -1 ||
            val.attribute.name.toLowerCase().indexOf(_filter) !== -1 ||
            val.attribute.description && val.attribute.description.toLowerCase().indexOf(_filter) !== -1 ||
            val.val && val.val.toLowerCase().indexOf(_filter) !== -1 ||
            this.getAttributeName(val).toLowerCase().indexOf(_filter) !== -1
          );
        }
        LogUtil.debug('AttributeValuesComponent filterAttributes ' + _filter, _filteredObjectAttributes);
      } else {
        _filteredObjectAttributes = this._objectAttributes.slice(0, this._objectAttributes.length);
        LogUtil.debug('AttributeValuesComponent filterAttributes no filter', _filteredObjectAttributes);
      }
    }

    if (_filteredObjectAttributes === null) {
      _filteredObjectAttributes = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    if (_sortProp === 'name') {
      _filteredObjectAttributes.sort((a, b) => {
        let _a1 = this.getAttributeName(a).toLowerCase();
        let _b1 = this.getAttributeName(b).toLowerCase();
        return (_a1 > _b1 ? 1 : -1) * _sortOrder;
      });
    } else {
      _filteredObjectAttributes.sort((a, b) => {
        let _a1:any = a.attribute;
        let _b1:any = b.attribute;
        return (_a1[_sortProp] > _b1[_sortProp] ? 1 : -1) * _sortOrder;
      });
    }

    if (this._imageOnlyMode) {
      this.filteredObjectAttributes = _filteredObjectAttributes.filter(val =>
        val.attribute.etype == 'Image'
      );
    } else {
      this.filteredObjectAttributes = _filteredObjectAttributes;
    }

    let _total = this.filteredObjectAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }

    this.loadingFilter = false;

  }


  private processDataChangesEvent() {

    LogUtil.debug('AttributeValuesComponent data changes', this.masterObject);
    if (this.masterObject && this._objectAttributes) {

      let _update = <Array<Pair<AttrValueVO, boolean>>>[];
      this._objectAttributes.forEach(attr => {
        if ((attr.attrvalueId !== 0 && this.isEditedAttribute(attr)) || (attr.attrvalueId === 0 && attr.val !== null && /\S+(.*\S)*/.test(attr.val) && !(/\* .+/.test(attr.val)))) {
          _update.push(new Pair(attr, this.isRemovedAttribute(attr)));
        }
      });

      LogUtil.debug('AttributeValuesComponent data changes update', _update);

      this.dataChanged.emit({ source: _update, valid: this.validForSave });

    }

  }

}
