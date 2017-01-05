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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { AttributeService, Util } from './../../shared/services/index';
import { ProductAttributeUsageComponent } from './../../shared/attributes/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { EtypeVO, AttributeGroupVO, AttributeVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-attribute-definitions',
  moduleId: module.id,
  templateUrl: 'attribute-definitions.component.html',
})

export class AttributeDefinitionsComponent implements OnInit, OnDestroy {

  private static GROUPS:string = 'groups';
  private static ATTRIBUTES:string = 'attributes';
  private static ATTRIBUTE:string = 'attribute';

  private forceShowAll:boolean = false;
  private viewMode:string = AttributeDefinitionsComponent.GROUPS;

  private etypes:Array<EtypeVO> = [];

  private groups:Array<AttributeGroupVO> = [];
  private groupFilter:string;

  private selectedGroup:AttributeGroupVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private attributes:Array<AttributeVO> = [];
  private attributeFilter:string;
  private attributeFilterRequired:boolean = true;
  private attributeFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedAttribute:AttributeVO;

  private attributeEdit:AttributeVO;

  private deleteValue:String;

  @ViewChild('productAttributeUsages')
  private productAttributeUsages:ProductAttributeUsageComponent;

  private searchHelpShow:boolean = false;

  private productAttributeCode:string;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _attributeService:AttributeService) {
    LogUtil.debug('AttributeDefinitionsComponent constructed');
  }

  newAttributeInstance():AttributeVO {
    let groupId = this.selectedGroup != null ? this.selectedGroup.attributegroupId : 0;
    let etype = this.etypes.length > 0 ? this.etypes[0] : { etypeId: 0, javatype: '', businesstype: ''};
    return {
      attributeId: 0, attributegroupId: groupId,
      code: '', name: '', description:null, displayNames: [],
      etypeId: etype.etypeId, etypeName:etype.businesstype,
      val:null, mandatory:false, allowduplicate:false, allowfailover:false,
      regexp:null, validationFailedMessage:[],
      rank:500,
      choiceData:[],
      store:false, search:false, primary:false, navigation:false
    };
  }

  ngOnInit() {
    LogUtil.debug('AttributeDefinitionsComponent ngOnInit');
    this.getAllEtypes();
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllAttributes();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('AttributeDefinitionsComponent ngOnDestroy');
  }

  protected onAttributeFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('AttributeDefinitionsComponent refresh handler');
    if (this.viewMode === AttributeDefinitionsComponent.GROUPS ||
        this.selectedGroup == null) {
      this.getAllGroups();
    } else {
      this.getAllAttributes();
    }
  }

  protected onGroupSelected(data:AttributeGroupVO) {
    LogUtil.debug('AttributeDefinitionsComponent onGroupSelected', data);
    this.selectedGroup = data;
    this.attributeFilter = '';
  }

  protected onAttributeSelected(data:AttributeVO) {
    LogUtil.debug('AttributeDefinitionsComponent onAttributeSelected', data);
    this.selectedAttribute = data;
    this.productAttributeCode = this.selectedAttribute ? this.selectedAttribute.code : null;
  }

  protected onAttributeChanged(event:FormValidationEvent<AttributeVO>) {
    LogUtil.debug('AttributeDefinitionsComponent onAttributeChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    //this.attributeEdit = event.source;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getAllAttributes();
  }

  protected onBackToList() {
    LogUtil.debug('AttributeDefinitionsComponent onBackToList handler');
    if (this.viewMode === AttributeDefinitionsComponent.ATTRIBUTE) {
      this.attributeEdit = null;
      this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
    } else if (this.viewMode === AttributeDefinitionsComponent.ATTRIBUTES) {
      this.attributeEdit = null;
      this.selectedAttribute = null;
      this.forceShowAll = false;
      this.viewMode = AttributeDefinitionsComponent.GROUPS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('AttributeDefinitionsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === AttributeDefinitionsComponent.ATTRIBUTES) {
      this.attributeEdit = this.newAttributeInstance();
      this.viewMode = AttributeDefinitionsComponent.ATTRIBUTE;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('AttributeDefinitionsComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedAttribute != null) {
      this.onRowDelete(this.selectedAttribute);
    } else if (this.selectedGroup != null) {
      this.onRowDelete(this.selectedGroup);
    }
  }

  protected onRowEditAttribute(row:AttributeVO) {
    LogUtil.debug('AttributeDefinitionsComponent onRowEditAttribute handler', row);
    this.attributeEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = AttributeDefinitionsComponent.ATTRIBUTE;
  }

  protected onRowEditSelected() {
    if (this.selectedAttribute != null) {
      this.onRowEditAttribute(this.selectedAttribute);
    }
  }

  protected onRowCopyAttribute(row:AttributeVO) {
    LogUtil.debug('AttributeDefinitionsComponent onRowCopyAttribute handler', row);
    this.attributeEdit = Util.clone(row);
    this.attributeEdit.attributeId = 0;
    this.changed = false;
    this.validForSave = false;
    this.viewMode = AttributeDefinitionsComponent.ATTRIBUTE;
  }

  protected onRowCopySelected() {
    if (this.selectedAttribute != null) {
      this.onRowCopyAttribute(this.selectedAttribute);
    }
  }


  protected onRowList(row:AttributeGroupVO) {
    LogUtil.debug('AttributeDefinitionsComponent onRowList handler', row);
    this.getAllAttributes();
  }


  protected onRowListSelected() {
    if (this.selectedGroup != null) {
      this.onRowList(this.selectedGroup);
    }
  }

  protected onRowInfoSelected() {
    if (this.selectedAttribute != null) {
      this.productAttributeUsages.showDialog();
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.attributeEdit != null) {

        LogUtil.debug('AttributeDefinitionsComponent Save handler attribute', this.attributeEdit);

        var _sub:any = this._attributeService.saveAttribute(this.attributeEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              if (this.attributeEdit.attributeId > 0) {
                let idx = this.attributes.findIndex(rez => rez.attributeId == this.attributeEdit.attributeId);
                if (idx !== -1) {
                  this.attributes[idx] = rez;
                  this.attributes = this.attributes.slice(0, this.attributes.length); // reset to propagate changes
                  LogUtil.debug('AttributeDefinitionsComponent attribute changed', rez);
                }
                this.changed = false;
                this.selectedAttribute = rez;
                this.attributeEdit = null;
                this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
              } else {
                this.attributeFilter = rez.name;
                LogUtil.debug('AttributeDefinitionsComponent attribute added', rez);
                this.getAllAttributes();
              }
          }
        );
      }
    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('AttributeDefinitionsComponent discard handler');
    if (this.viewMode === AttributeDefinitionsComponent.ATTRIBUTE) {
      if (this.selectedAttribute != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AttributeDefinitionsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedAttribute != null) {
        LogUtil.debug('AttributeDefinitionsComponent onDeleteConfirmationResult', this.selectedAttribute);

        var _sub:any = this._attributeService.removeAttribute(this.selectedAttribute).subscribe(res => {
          LogUtil.debug('AttributeDefinitionsComponent removeAttribute', this.selectedAttribute);
          let idx = this.attributes.indexOf(this.selectedAttribute);
          this.attributes.splice(idx, 1);
          this.attributes = this.attributes.slice(0, this.attributes.length); // reset to propagate changes
          this.selectedAttribute = null;
          this.attributeEdit = null;
          this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
          _sub.unsubscribe();
        });

      }
    }
  }

  protected onClearFilterGrp() {

    this.groupFilter = '';

  }

  protected onClearFilterAttr() {

    this.attributeFilter = '';
    this.onAttributeFilterChange({});
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchAttrCode() {
    this.searchHelpShow = false;
    this.attributeFilter = '#';
  }


  private getAllEtypes() {
    var _sub:any = this._attributeService.getAllEtypes().subscribe( alletypes => {
      LogUtil.debug('AttributeDefinitionsComponent getAllEtypes', alletypes);
      this.etypes = alletypes;
      _sub.unsubscribe();
    });
  }


  private getAllGroups() {
    this.loading = true;
    var _sub:any = this._attributeService.getAllGroups().subscribe( allgroups => {
      LogUtil.debug('AttributeDefinitionsComponent getAllGroups', allgroups);
      this.groups = allgroups;
      this.selectedGroup = null;
      this.viewMode = AttributeDefinitionsComponent.GROUPS;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

  private getAllAttributes() {

    this.attributeFilterRequired = !this.forceShowAll && (this.attributeFilter == null || this.attributeFilter.length < 2);

    LogUtil.debug('AttributeDefinitionsComponent getAllAttributes' + (this.forceShowAll ? ' forcefully': ''), this.selectedGroup);

    if (this.selectedGroup != null && !this.attributeFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._attributeService.getFilteredAttributes(this.selectedGroup.code, this.attributeFilter, max).subscribe(allattributes => {
        LogUtil.debug('AttributeDefinitionsComponent getAllAttributes', allattributes);
        this.attributes = allattributes;
        this.selectedAttribute = null;
        this.attributeEdit = null;
        this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
        this.changed = false;
        this.validForSave = false;
        this.attributeFilterCapped = this.attributes.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.attributes = [];
      this.selectedAttribute = null;
      this.attributeEdit = null;
      this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
      this.changed = false;
      this.validForSave = false;
      this.attributeFilterCapped = false;
    }
  }

}
