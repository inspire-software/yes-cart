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
import { ProductTypeVO, ProductTypeAttrVO, ProductTypeViewGroupVO, AttributeVO } from './../../shared/model/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { I18nEventBus, Util } from './../../shared/services/index';
import { UiUtil } from './../../shared/ui/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'cw-product-type-group',
  templateUrl: 'product-type-group.component.html',
})

export class ProductTypeGroupComponent implements OnInit, OnChanges {

  @Input() masterObject:ProductTypeVO;

  @Output() dataSelected: EventEmitter<ProductTypeViewGroupVO> = new EventEmitter<ProductTypeViewGroupVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<ProductTypeVO>> = new EventEmitter<FormValidationEvent<ProductTypeVO>>();

  public groupFilter:string;
  private _objectAttributes:Array<ProductTypeAttrVO>;
  private _objectAttributesMap:any;
  public attributeAssignedFilter:string;
  public attributeFilter:string;
  public filteredObjectGroups:Array<ProductTypeViewGroupVO>;
  public removedObjectGroups:Array<ProductTypeViewGroupVO>;
  private delayedGroupFiltering:Future;
  private delayedAttributeFiltering:Future;
  private delayedAttributeAssignedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  public selectedRow:ProductTypeViewGroupVO;
  public groupToEdit:ProductTypeViewGroupVO;

  public selectedRowAvailable:ProductTypeAttrVO[];
  public selectedRowAvailableFiltered:ProductTypeAttrVO[];
  public selectedRowAssigned:ProductTypeAttrVO[];
  public selectedRowAssignedFiltered:ProductTypeAttrVO[];
  public selectedRowAssignedExtra:string[];

  /**
   * Construct attribute panel
   */
  constructor() {
    LogUtil.debug('ProductTypeGroupComponent constructed');

    this.groupToEdit = null;
    let that = this;
    this.delayedGroupFiltering = Futures.perpetual(function() {
      that.filterGroups();
    }, this.delayedFilteringMs);
    this.delayedAttributeFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);
    this.delayedAttributeAssignedFiltering = Futures.perpetual(function() {
      that.filterAssignedAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set objectAttributes(objectAttributes:Array<ProductTypeAttrVO>) {
    this._objectAttributes = objectAttributes;
    this.loadAttributeData();
  }

  get objectAttributes():Array<ProductTypeAttrVO> {
    return this._objectAttributes;
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ProductTypeGroupComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ProductTypeGroupComponent ngOnChanges', changes);
    this.delayedGroupFiltering.delay();
  }

  onSelectRow(row:ProductTypeViewGroupVO) {
    LogUtil.debug('ProductTypeGroupComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
      this.mapAttributesInGroup();
    }
    this.dataSelected.emit(this.selectedRow);
  }

  getAttrFlags(row:ProductTypeAttrVO) {
    let flags = '&nbsp;';
    if (row.visible) {
      flags += '<i class="fa fa-eye"></i>&nbsp;';
    }
    if (row.similarity) {
      flags += '<i class="fa fa-copy"></i>&nbsp;';
    }
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
      if (row.navigationType === 'R') {
        flags += '<i class="fa fa-sliders"></i>&nbsp;(' + row.navigationType + ')&nbsp;' + (row.navigationTemplate != null ? row.navigationTemplate : '');
      } else {
        flags += '<i class="fa fa-list-alt"></i>&nbsp;(' + row.navigationType + ')&nbsp;' + (row.navigationTemplate != null ? row.navigationTemplate : '');
      }
    }
    return flags;
  }

  onAssignedAttrClick(row:ProductTypeAttrVO) {
    LogUtil.debug('ProductTypeGroupComponent onAssignedAttrClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row.attribute.code);
    if (idx != -1) {
      this.selectedRow.attrCodeList.splice(idx, 1);
      let idx2 = this.selectedRowAssigned.indexOf(row);
      this.selectedRowAssigned.splice(idx2, 1);
      this.selectedRowAssigned = this.selectedRowAssigned.slice(0, this.selectedRowAssigned.length);
      this.selectedRowAvailable.push(row);
      this.selectedRowAvailable.sort((a, b) => {
        let rank:number = a.rank - b.rank;
        if (rank == 0) {
          return a.attribute.name > b.attribute.name ? 1 : -1;
        }
        return rank;
      });

      this.changed = true;
      this.filterAttributes();
      this.filterAssignedAttributes();
      this.processDataChangesEvent();
    }
  }

  onAssignedAttrCodeClick(row:string) {
    LogUtil.debug('ProductTypeGroupComponent onAssignedAttrCodeClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row);
    if (idx != -1) {
      this.selectedRow.attrCodeList.splice(idx, 1);
      let idx2 = this.selectedRowAssignedExtra.indexOf(row);
      this.selectedRowAssignedExtra.splice(idx2, 1);
      this.selectedRowAssignedExtra = this.selectedRowAssignedExtra.slice(0, this.selectedRowAssignedExtra.length);
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  onAvailableAttrClick(row:ProductTypeAttrVO) {
    LogUtil.debug('ProductTypeGroupComponent onAvailableAttrClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row.attribute.code);
    if (idx == -1) {
      this.selectedRow.attrCodeList.push(row.attribute.code);
      let idx2 = this.selectedRowAvailable.indexOf(row);
      this.selectedRowAvailable.splice(idx2, 1);
      this.selectedRowAssigned.push(row);
      this.selectedRowAssigned.sort((a, b) => {
        let rank:number = a.rank - b.rank;
        if (rank == 0) {
          return a.attribute.name > b.attribute.name ? 1 : -1;
        }
        return rank;
      });
      this.changed = true;
      this.filterAttributes();
      this.filterAssignedAttributes();
      this.processDataChangesEvent();
    }
  }

  onAttributeFilterChange() {
    this.delayedAttributeFiltering.delay();
  }

  onClearFilter() {
    this.attributeFilter = '';
    this.delayedAttributeFiltering.delay();
  }

  onAttributeAssignedFilterChange() {
    this.delayedAttributeAssignedFiltering.delay();
  }

  onClearAssignedFilter() {
    this.attributeAssignedFilter = '';
    this.delayedAttributeAssignedFiltering.delay();
  }

  onGroupFilterChange() {
    this.delayedGroupFiltering.delay();
  }

  onClearGroupFilter() {
    this.groupFilter = '';
    this.delayedGroupFiltering.delay();
  }



  public onRowAdd() {
    LogUtil.debug('ProductTypeGroupComponent onRowAdd handler');
    this.groupToEdit = {
      prodTypeAttributeViewGroupId: 0,
      producttypeId: this.masterObject.producttypeId,
      attrCodeList: [], rank: 500, name: '', displayNames: []
    };
    this.changed = false;
    this.validForSave = false;
    this.editModalDialog.show();

  }


  onRowDelete(row:ProductTypeViewGroupVO) {
    LogUtil.debug('ProductTypeGroupComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  onRowEdit(row:ProductTypeViewGroupVO) {
    LogUtil.debug('ProductTypeGroupComponent onRowEdit handler', row);
    this.groupToEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.editModalDialog.show();
  }

  public onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }


  onDataChange(event:any) {

    this.changed = true;
    this.validForSave = !isNaN(this.groupToEdit.rank) && this.groupToEdit.name != null && /\S+.*\S+/.test(this.groupToEdit.name);

    LogUtil.debug('ProductTypeGroupComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  getCount(arr:string[]):number {
    if (arr != null) {
      return arr.length;
    }
    return 0;
  }


  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeGroupComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.prodTypeAttributeViewGroupId;
      if (attrToDelete === 0) {
        let idx = this.masterObject.viewGroups.findIndex(attrVo => {
          return attrVo.name === this.selectedRow.name;
        });
        this.masterObject.viewGroups.splice(idx, 1);
        LogUtil.debug('ProductTypeGroupComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        LogUtil.debug('ProductTypeGroupComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        let idx = this.masterObject.viewGroups.findIndex(attrVo => {
          return attrVo.prodTypeAttributeViewGroupId === attrToDelete;
        });
        this.masterObject.viewGroups.splice(idx, 1);
        this.removedObjectGroups.push(this.selectedRow);
      }
      this.filterGroups();
      this.onSelectRow(this.selectedRow); // deselect
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeGroupComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.groupToEdit.prodTypeAttributeViewGroupId === 0) { // add new
        LogUtil.debug('ProductTypeGroupComponent onEditModalResult add new attribute', this.masterObject.viewGroups);
        this.masterObject.viewGroups.push(this.groupToEdit);
      } else { // edit existing
        LogUtil.debug('ProductTypeGroupComponent onEditModalResult update existing', this.masterObject.viewGroups);
        let idx = this.masterObject.viewGroups.findIndex(attrVo => {
          return attrVo.prodTypeAttributeViewGroupId === this.groupToEdit.prodTypeAttributeViewGroupId;
        });
        this.masterObject.viewGroups[idx] = this.groupToEdit;
        this.masterObject.viewGroups = this.masterObject.viewGroups.slice(0, this.masterObject.viewGroups.length);
      }
      this.onSelectRow(this.groupToEdit);
      this.changed = true;
      this.filterGroups();
      this.processDataChangesEvent();
    } else {
      this.groupToEdit = null;
    }
  }

  getGroupName(group:ProductTypeViewGroupVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = group.displayNames;
    let def = group.name;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  getAttributeName(attr:AttributeVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = attr.displayNames;
    let def = attr.name != null ? attr.name : attr.code;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  private processDataChangesEvent() {

    LogUtil.debug('ProductTypeAttributeComponent data changes', this.masterObject);
    if (this.masterObject) {

      this.dataChanged.emit({ source: this.masterObject, valid: this.validForSave });

    }

  }


  private loadAttributeData() {
    if (this.masterObject && this._objectAttributes) {
      this._objectAttributesMap = {};
      this._objectAttributes.forEach(attr => {
        this._objectAttributesMap[attr.attribute.code] = attr;
      });
    }
    this.removedObjectGroups = [];
  }

  private filterGroups() {
    if (this.masterObject) {

      let groups:ProductTypeViewGroupVO[] = [];
      if (this.groupFilter) {
        let _filter = this.groupFilter.toLowerCase();
        groups = this.masterObject.viewGroups.filter(group =>
          (group.name && group.name.toLowerCase().indexOf(_filter) != -1) ||
          (group.attrCodeList && group.attrCodeList.indexOf(_filter) != -1)
        );
      } else {
        groups = this.masterObject.viewGroups;
      }

      groups.sort(function(a, b) {
        let rank:number = a.rank - b.rank;
        if (rank == 0) {
          return a.name > b.name ? 1 : -1;
        }
        return rank;
      });

      this.filteredObjectGroups = groups;
    }
  }


  private mapAttributesInGroup() {

    if (this.selectedRow.attrCodeList == null) {
      this.selectedRow.attrCodeList = [];
    }

    let _sort = function(a:ProductTypeAttrVO, b:ProductTypeAttrVO) {
      let rank:number = a.rank - b.rank;
      if (rank == 0) {
        return a.attribute.name > b.attribute.name ? 1 : -1;
      }
      return rank;
    };

    let assignedCodes = this.selectedRow.attrCodeList;

    let _selectedRowAssigned = this._objectAttributes.filter(attr =>
      assignedCodes.indexOf(attr.attribute.code) != -1
    );
    _selectedRowAssigned.sort(_sort);
    this.selectedRowAssigned = _selectedRowAssigned;

    let _selectedRowAvailable = this._objectAttributes.filter(attr =>
      assignedCodes.indexOf(attr.attribute.code) == -1
    );
    _selectedRowAvailable.sort(_sort);
    this.selectedRowAvailable = _selectedRowAvailable;

    this.selectedRowAssignedExtra = [];
    assignedCodes.forEach(code => {
      let idx = this.selectedRowAssigned.findIndex(attr =>
        attr.attribute.code === code
      );
      if (idx == -1) {
        this.selectedRowAssignedExtra.push(code);
      }
    });

    this.filterAttributes();
    this.filterAssignedAttributes();

  }

  private filterAttributes() {

    if (this.attributeFilter) {

      let _filter = this.attributeFilter.toLowerCase();

      this.selectedRowAvailableFiltered = this.selectedRowAvailable.filter(attr =>
        attr.attribute.code.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.name.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.description && attr.attribute.description.toLowerCase().indexOf(_filter) != -1
      );

    } else {

      this.selectedRowAvailableFiltered = this.selectedRowAvailable;

    }

  }

  private filterAssignedAttributes() {

    if (this.attributeAssignedFilter) {

      let _filter = this.attributeAssignedFilter.toLowerCase();

      this.selectedRowAssignedFiltered = this.selectedRowAssigned.filter(attr =>
        attr.attribute.code.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.name.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.description && attr.attribute.description.toLowerCase().indexOf(_filter) != -1
      );

    } else {

      this.selectedRowAssignedFiltered = this.selectedRowAssigned;

    }

  }


}
