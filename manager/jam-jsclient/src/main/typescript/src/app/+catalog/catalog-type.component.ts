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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CatalogService, UserEventBus, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductTypeInfoVO, ProductTypeVO, ProductTypeAttrVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-catalog-types',
  templateUrl: 'catalog-type.component.html',
})

export class CatalogTypeComponent implements OnInit, OnDestroy {

  private static TYPES:string = 'types';
  private static TYPE:string = 'type';

  public searchHelpShow:boolean = false;
  public forceShowAll:boolean = false;
  public viewMode:string = CatalogTypeComponent.TYPES;

  public types:SearchResultVO<ProductTypeInfoVO>;
  public typeFilter:string;
  public typeFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedType:ProductTypeInfoVO;

  public typeEditIsCopyOf:ProductTypeInfoVO;
  public typeEdit:ProductTypeVO;
  public typeEditAttributes:ProductTypeAttrVO[] = [];
  public typeAttributesUpdate:Array<Pair<ProductTypeAttrVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  public deleteValue:String;

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

  constructor(private _typeService:CatalogService) {
    LogUtil.debug('CatalogTypeComponent constructed');
    this.types = this.newSearchResultInstance();
  }

  newTypeInstance():ProductTypeVO {
    return { producttypeId: 0, guid: null, name: '', displayNames: [], description: null, uitemplate: null, uisearchtemplate: null, service: false, shippable: true, downloadable: false, digital:false, viewGroups: [] };
  }

  newSearchResultInstance():SearchResultVO<ProductTypeInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: [],
          statuses: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('CatalogTypeComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredTypes();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('CatalogTypeComponent ngOnDestroy');
  }


  onFilterChange(event:any) {
    this.types.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('CatalogTypeComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredTypes();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('CatalogTypeComponent onPageSelected', page);
    this.types.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('CatalogTypeComponent ononSortSelected', sort);
    if (sort == null) {
      this.types.searchContext.sortBy = null;
      this.types.searchContext.sortDesc = false;
    } else {
      this.types.searchContext.sortBy = sort.first;
      this.types.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  onTypeSelected(data:ProductTypeInfoVO) {
    LogUtil.debug('CatalogTypeComponent onTypeSelected', data);
    this.selectedType = data;
  }

  onTypeChanged(event:FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>) {
    LogUtil.debug('CatalogTypeComponent onTypeChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.typeEdit = event.source.first;
    this.typeAttributesUpdate = event.source.second;
  }

  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredTypes();
  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchCodeExact() {
    this.searchHelpShow = false;
    this.typeFilter = '!';
  }

  onSearchCode() {
    this.searchHelpShow = false;
    this.typeFilter = '#';
  }


  onBackToList() {
    LogUtil.debug('CatalogTypeComponent onBackToList handler');
    if (this.viewMode === CatalogTypeComponent.TYPE) {
      this.typeEditIsCopyOf = null;
      this.typeEdit = null;
      this.viewMode = CatalogTypeComponent.TYPES;
    }
  }

  onRowNew() {
    LogUtil.debug('CatalogTypeComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogTypeComponent.TYPES) {
      this.typeEditIsCopyOf = null;
      this.typeEdit = this.newTypeInstance();
      this.typeEditAttributes = [];
      this.viewMode = CatalogTypeComponent.TYPE;
    }
  }

  onRowDelete(row:any) {
    LogUtil.debug('CatalogTypeComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedType != null) {
      this.onRowDelete(this.selectedType);
    }
  }


  onRowEditType(row:ProductTypeInfoVO) {
    LogUtil.debug('CatalogTypeComponent onRowEditType handler', row);
    let typeId = row.producttypeId;
    this.loading = true;
    this._typeService.getProductTypeById(typeId).subscribe(typ => {
      this.typeEditIsCopyOf = null;
      this.typeEdit = typ;
      this.typeEditAttributes = [];
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      this.viewMode = CatalogTypeComponent.TYPE;
      if (this.typeEdit.producttypeId > 0) {
        this.loading = true;
        this._typeService.getProductTypeAttributes(this.typeEdit.producttypeId).subscribe(attrs => {
          this.typeEditAttributes = attrs;
          this.loading = false;
        });
      }

    });
  }

  onRowEditSelected() {
    if (this.selectedType != null) {
      this.onRowEditType(this.selectedType);
    }
  }


  onRowCopyProductType(row:ProductTypeInfoVO) {
    LogUtil.debug('CatalogTypeComponent onRowCopyProductType handler', row);
    let _edit = this.newTypeInstance();
    Util.copyValues(row, _edit);
    _edit.producttypeId = 0;
    _edit.viewGroups = [];

    this.typeEditIsCopyOf = row;
    this.typeEdit = _edit;
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogTypeComponent.TYPE;
  }

  onRowCopySelected() {
    if (this.selectedType != null) {
      this.onRowCopyProductType(this.selectedType);
    }
  }

  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.typeEdit != null) {

        LogUtil.debug('CatalogTypeComponent Save handler type', this.typeEdit);

        this.loading = true;

        if (!(this.typeEdit.producttypeId > 0) && this.typeEditIsCopyOf != null) {

          this._typeService.copyProductType(this.typeEditIsCopyOf, this.typeEdit).subscribe(
            rez => {
              LogUtil.debug('CatalogTypeComponent type changed', rez);
              this.loading = false;
              this.changed = false;
              this.selectedType = rez;
              this.typeEdit = null;
              this.typeEditIsCopyOf = null;
              this.loading = false;
              this.viewMode = CatalogTypeComponent.TYPES;
              this.getFilteredTypes();
            }
          );

        } else {

          this._typeService.saveProductType(this.typeEdit).subscribe(
            rez => {
              let pk = this.typeEdit.producttypeId;
              LogUtil.debug('CatalogTypeComponent type changed', rez);
              this.changed = false;
              this.selectedType = rez;
              this.typeEdit = null;
              this.typeEditIsCopyOf = null;
              this.loading = false;
              this.viewMode = CatalogTypeComponent.TYPES;

              if (pk > 0 && this.typeAttributesUpdate != null && this.typeAttributesUpdate.length > 0) {

                this.loading = true;
                this._typeService.saveProductTypeAttributes(this.typeAttributesUpdate).subscribe(rez => {
                  LogUtil.debug('CatalogTypeComponent type attributes updated', rez);
                  this.typeAttributesUpdate = null;
                  this.loading = false;
                  this.getFilteredTypes();
                });
              } else {
                this.getFilteredTypes();
              }
            }
          );
        }
      }

    }

  }

  onDiscardEventHandler() {
    LogUtil.debug('CatalogTypeComponent discard handler');
    if (this.viewMode === CatalogTypeComponent.TYPE) {
      if (this.selectedType != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CatalogTypeComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedType != null) {
        LogUtil.debug('CatalogTypeComponent onDeleteConfirmationResult', this.selectedType);

        this.loading = true;
        this._typeService.removeProductType(this.selectedType).subscribe(res => {
          LogUtil.debug('CatalogTypeComponent removeType', this.selectedType);
          this.selectedType = null;
          this.typeEdit = null;
          this.loading = false;
          this.getFilteredTypes();
        });
      }
    }
  }

  onClearFilter() {

    this.typeFilter = '';
    this.getFilteredTypes();

  }

  private getFilteredTypes() {
    this.typeFilterRequired = !this.forceShowAll && (this.typeFilter == null || this.typeFilter.length < 2);

    LogUtil.debug('CatalogTypeComponent getFilteredTypes' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.typeFilterRequired) {
      this.loading = true;

      this.types.searchContext.parameters.filter = [ this.typeFilter ];
      this.types.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      this._typeService.getFilteredProductTypes(this.types.searchContext).subscribe( alltypes => {
        LogUtil.debug('CatalogTypeComponent getFilteredTypes', alltypes);
        this.types = alltypes;
        this.selectedType = null;
        this.typeEdit = null;
        this.viewMode = CatalogTypeComponent.TYPES;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
      });
    } else {
      this.types = this.newSearchResultInstance();
      this.selectedType = null;
      this.typeEdit = null;
      this.typeEditAttributes = null;
      this.viewMode = CatalogTypeComponent.TYPES;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
