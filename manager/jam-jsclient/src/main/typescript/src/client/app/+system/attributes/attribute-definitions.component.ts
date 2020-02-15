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
import { AttributeService, UserEventBus, Util } from './../../shared/services/index';
import { ProductAttributeUsageComponent } from './../../shared/attributes/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { EtypeVO, AttributeGroupVO, AttributeVO, Pair, SearchResultVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-attribute-definitions',
  moduleId: module.id,
  templateUrl: 'attribute-definitions.component.html',
})

export class AttributeDefinitionsComponent implements OnInit, OnDestroy {

  private static ATTRIBUTES:string = 'attributes';
  private static ATTRIBUTE:string = 'attribute';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = AttributeDefinitionsComponent.ATTRIBUTES;

  private etypes:Array<EtypeVO> = [];

  private groups:Array<AttributeGroupVO> = [];

  private selectedGroups:Pair<AttributeGroupVO, boolean>[] = [];

  private attributes:SearchResultVO<AttributeVO>;
  private attributeFilter:string;
  private attributeFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedAttribute:AttributeVO;

  private attributeEdit:AttributeVO;

  private deleteValue:String;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('productAttributeUsages')
  private productAttributeUsages:ProductAttributeUsageComponent;

  private productAttributeCode:string;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  private userSub:any;

  constructor(private _attributeService:AttributeService) {
    LogUtil.debug('AttributeDefinitionsComponent constructed');
    this.attributes = this.newSearchResultInstance();
  }

  newAttributeInstance():AttributeVO {
    let etype = this.etypes.length > 0 ? this.etypes[0].businesstype : '';
    return {
      attributeId: 0, attributegroup: 'PRODUCT',
      code: '', name: '', description:null, displayNames: [],
      etype: etype,
      val:null, secure: false, mandatory:false, allowduplicate:false, allowfailover:false,
      regexp:null, validationFailedMessage:[],
      rank:500,
      choiceData:[],
      store:false, search:false, primary:false, navigation:false
    };
  }

  newSearchResultInstance():SearchResultVO<AttributeVO> {
    return {
      searchContext: {
        parameters: {
          filter: [],
          groups: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: 'code',
        sortDesc: true
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('AttributeDefinitionsComponent ngOnInit');

    this.onRefreshHandler();

    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.getAllEtypes();
    });

    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllAttributes();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('AttributeDefinitionsComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }


  protected onFilterChange(event:any) {
    this.attributes.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('AttributeDefinitionsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      LogUtil.debug('AttributeDefinitionsComponent refresh handler', this.etypes, this.groups);
      if (this.etypes == null || this.etypes.length == 0 || this.groups == null || this.groups.length == 0) {
        this.getAllEtypes();
      } else {
        this.getAllAttributes();
      }
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('AttributeDefinitionsComponent onPageSelected', page);
    this.attributes.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('AttributeDefinitionsComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributes.searchContext.sortBy = null;
      this.attributes.searchContext.sortDesc = false;
    } else {
      this.attributes.searchContext.sortBy = sort.first;
      this.attributes.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  protected onSearchAllGroups() {
    let _state:boolean = false;
    this.selectedGroups.forEach((_st:Pair<AttributeGroupVO, boolean>) => {
      _state = _state || _st.second;
    });
    this.selectedGroups.forEach((_st:Pair<AttributeGroupVO, boolean>) => {
      _st.second = !_state;
    });
    this.getAllAttributes();
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

  protected onRowInfoSelected() {
    if (this.selectedAttribute != null) {
      this.productAttributeUsages.showDialog();
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.attributeEdit != null) {

        LogUtil.debug('AttributeDefinitionsComponent Save handler attribute', this.attributeEdit);

        this.loading = true;
        let _sub:any = this._attributeService.saveAttribute(this.attributeEdit).subscribe(
            rez => {
              this.loading = false;
              _sub.unsubscribe();
              LogUtil.debug('AttributeDefinitionsComponent attribute added', rez);
              this.changed = false;
              this.selectedAttribute = rez;
              this.attributeEdit = null;
              this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
              this.getAllAttributes();
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

        this.loading = true;
        let _sub:any = this._attributeService.removeAttribute(this.selectedAttribute).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('AttributeDefinitionsComponent removeAttribute', this.selectedAttribute);
          this.selectedAttribute = null;
          this.attributeEdit = null;
          this.loading = false;
          this.getAllAttributes();
        });

      }
    }
  }

  protected onClearFilterAttr() {

    this.attributeFilter = '';
    this.getAllAttributes();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchAttrCode() {
    this.searchHelpShow = false;
    this.attributeFilter = '#';
  }


  private getAllEtypes() {

    LogUtil.debug('AttributeDefinitionsComponent getAllEtypes', this.etypes);

    this.loading = true;
    let _sub:any = this._attributeService.getAllEtypes().subscribe( alletypes => {
      LogUtil.debug('AttributeDefinitionsComponent getAllEtypes', alletypes);
      this.etypes = alletypes;
      this.loading = false;
      _sub.unsubscribe();
      this.getAllGroups();
    });
  }


  private getAllGroups() {

    LogUtil.debug('AttributeDefinitionsComponent getAllGroups', this.groups);

    this.loading = true;
    let _sub:any = this._attributeService.getAllGroups().subscribe( allgroups => {
      LogUtil.debug('AttributeDefinitionsComponent getAllGroups', allgroups);
      this.groups = allgroups;
      let _map:Pair<AttributeGroupVO, boolean>[] = [];
      this.groups.forEach(group => {
         _map.push({ first: group, second: true });
      });
      this.selectedGroups = _map;
      this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

  private getAllAttributes() {

    this.attributeFilterRequired = !this.forceShowAll && (this.attributeFilter == null || this.attributeFilter.length < 2);

    LogUtil.debug('AttributeDefinitionsComponent getAllAttributes' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.attributeFilterRequired) {
      this.loading = true;

      let grs:string[] = [];
      this.selectedGroups.forEach((_gr:Pair<AttributeGroupVO, boolean>) => {
        if (_gr.second) {
          grs.push(_gr.first.code);
        }
      });


      this.attributes.searchContext.parameters.filter = [ this.attributeFilter ];
      this.attributes.searchContext.parameters.groups = grs;
      this.attributes.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      let _sub:any = this._attributeService.getFilteredAttributes(this.attributes.searchContext).subscribe(allattributes => {
        LogUtil.debug('AttributeDefinitionsComponent getAllAttributes', allattributes);
        this.attributes = allattributes;
        this.selectedAttribute = null;
        this.attributeEdit = null;
        this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.attributes = this.newSearchResultInstance();
      this.selectedAttribute = null;
      this.attributeEdit = null;
      this.viewMode = AttributeDefinitionsComponent.ATTRIBUTES;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
