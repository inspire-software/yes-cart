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
import { CatalogService } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductTypeInfoVO, ProductTypeVO, ProductTypeAttrVO, Pair } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-catalog-types',
  moduleId: module.id,
  templateUrl: 'catalog-type.component.html',
})

export class CatalogTypeComponent implements OnInit, OnDestroy {

  private static TYPES:string = 'types';
  private static TYPE:string = 'type';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = CatalogTypeComponent.TYPES;

  private types:Array<ProductTypeInfoVO> = [];
  private typeFilter:string;
  private typeFilterRequired:boolean = true;
  private typeFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedType:ProductTypeInfoVO;

  private typeEdit:ProductTypeVO;
  private typeEditAttributes:ProductTypeAttrVO[] = [];
  private typeAttributesUpdate:Array<Pair<ProductTypeAttrVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _typeService:CatalogService) {
    LogUtil.debug('CatalogTypeComponent constructed');
  }

  newTypeInstance():ProductTypeVO {
    return { producttypeId: 0, guid: null, name: '', description: null, uitemplate: null, uisearchtemplate: null, ensemble: false, shippable: true, downloadable: false, digital:false, viewGroups: [] };
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


  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('CatalogTypeComponent refresh handler');
    this.getFilteredTypes();
  }

  protected onTypeSelected(data:ProductTypeInfoVO) {
    LogUtil.debug('CatalogTypeComponent onTypeSelected', data);
    this.selectedType = data;
  }

  protected onTypeChanged(event:FormValidationEvent<Pair<ProductTypeVO, Array<Pair<ProductTypeAttrVO, boolean>>>>) {
    LogUtil.debug('CatalogTypeComponent onTypeChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.typeEdit = event.source.first;
    this.typeAttributesUpdate = event.source.second;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredTypes();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchCodeExact() {
    this.searchHelpShow = false;
    this.typeFilter = '!';
  }

  protected onSearchCode() {
    this.searchHelpShow = false;
    this.typeFilter = '#';
  }


  protected onBackToList() {
    LogUtil.debug('CatalogTypeComponent onBackToList handler');
    if (this.viewMode === CatalogTypeComponent.TYPE) {
      this.typeEdit = null;
      this.viewMode = CatalogTypeComponent.TYPES;
    }
  }

  protected onRowNew() {
    LogUtil.debug('CatalogTypeComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogTypeComponent.TYPES) {
      this.typeEdit = this.newTypeInstance();
      this.typeEditAttributes = [];
      this.viewMode = CatalogTypeComponent.TYPE;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('CatalogTypeComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedType != null) {
      this.onRowDelete(this.selectedType);
    }
  }


  protected onRowEditType(row:ProductTypeInfoVO) {
    LogUtil.debug('CatalogTypeComponent onRowEditType handler', row);
    let typeId = row.producttypeId;
    var _sub:any = this._typeService.getProductTypeById(typeId).subscribe(typ => {
      _sub.unsubscribe();
      this.typeEdit = typ;
      this.typeEditAttributes = [];
      this.changed = false;
      this.validForSave = false;
      this.viewMode = CatalogTypeComponent.TYPE;
      if (this.typeEdit.producttypeId > 0) {
        var _sub2:any = this._typeService.getProductTypeAttributes(this.typeEdit.producttypeId).subscribe(attrs => {
          _sub2.unsubscribe();
          this.typeEditAttributes = attrs;
        });
      }

    });
  }

  protected onRowEditSelected() {
    if (this.selectedType != null) {
      this.onRowEditType(this.selectedType);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.typeEdit != null) {

        LogUtil.debug('CatalogTypeComponent Save handler type', this.typeEdit);

        var _sub:any = this._typeService.saveProductType(this.typeEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.typeEdit.producttypeId;
              LogUtil.debug('CatalogTypeComponent type changed', rez);
              this.changed = false;
              this.selectedType = rez;
              this.typeEdit = null;
              this.viewMode = CatalogTypeComponent.TYPES;

              if (pk > 0 && this.typeAttributesUpdate != null && this.typeAttributesUpdate.length > 0) {

                var _sub2:any = this._typeService.saveProductTypeAttributes(this.typeAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogTypeComponent type attributes updated', rez);
                  this.typeAttributesUpdate = null;
                  this.getFilteredTypes();
                });
              } else {
                this.typeFilter = rez.name;
                this.getFilteredTypes();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('CatalogTypeComponent discard handler');
    if (this.viewMode === CatalogTypeComponent.TYPE) {
      if (this.selectedType != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CatalogTypeComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedType != null) {
        LogUtil.debug('CatalogTypeComponent onDeleteConfirmationResult', this.selectedType);

        var _sub:any = this._typeService.removeProductType(this.selectedType).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('CatalogTypeComponent removeType', this.selectedType);
          this.selectedType = null;
          this.typeEdit = null;
          this.getFilteredTypes();
        });
      }
    }
  }

  private getFilteredTypes() {
    this.typeFilterRequired = !this.forceShowAll && (this.typeFilter == null || this.typeFilter.length < 2);

    LogUtil.debug('CatalogTypeComponent getFilteredTypes' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.typeFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._typeService.getFilteredProductTypes(this.typeFilter, max).subscribe( alltypes => {
        LogUtil.debug('CatalogTypeComponent getFilteredTypes', alltypes);
        this.types = alltypes;
        this.selectedType = null;
        this.typeEdit = null;
        this.viewMode = CatalogTypeComponent.TYPES;
        this.changed = false;
        this.validForSave = false;
        this.typeFilterCapped = this.types.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.types = [];
      this.selectedType = null;
      this.typeEdit = null;
      this.typeEditAttributes = null;
      this.viewMode = CatalogTypeComponent.TYPES;
      this.changed = false;
      this.validForSave = false;
      this.typeFilterCapped = false;
    }
  }


}
