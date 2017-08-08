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
import { CatalogService, Util } from './../shared/services/index';
import { CategorySelectComponent } from './../shared/catalog/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { CategoryVO, AttrValueCategoryVO, Pair } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-catalog-category',
  moduleId: module.id,
  templateUrl: 'catalog-category.component.html',
})

export class CatalogCategoryComponent implements OnInit, OnDestroy {

  private static CATEGORIES:string = 'categories';
  private static CATEGORY:string = 'category';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = CatalogCategoryComponent.CATEGORIES;

  private categories:Array<CategoryVO> = [];
  private categoryFilter:string;
  private categoryFilterRequired:boolean = true;
  private categoryFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedCategory:CategoryVO;

  private categoryEdit:CategoryVO;
  private categoryEditAttributes:AttrValueCategoryVO[] = [];
  private categoryAttributesUpdate:Array<Pair<AttrValueCategoryVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  @ViewChild('categorySelectComponent')
  private categorySelectComponent:CategorySelectComponent;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _categoryService:CatalogService) {
    LogUtil.debug('CatalogCategoryComponent constructed');
  }

  newCategoryInstance():CategoryVO {
    return {
      categoryId: 0,
      parentId: 0, parentName: null,
      linkToId: 0, linkToName: null,
      rank: 500,
      productTypeId: 0, productTypeName: null,
      name: '', guid: null, displayNames: [], description: null,
      uitemplate: null,
      availablefrom: null, availableto: null,
      uri: null, title: null, metakeywords: null, metadescription: null, displayTitles: [], displayMetakeywords: [], displayMetadescriptions: [],
      navigationByAttributes: false, navigationByBrand: false, navigationByPrice: false, navigationByPriceTiers: { tiers: [] }, children: []
    };
  }

  ngOnInit() {
    LogUtil.debug('CatalogCategoryComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCategories();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('CatalogCategoryComponent ngOnDestroy');
  }


  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('CatalogCategoryComponent refresh handler');
    this.getFilteredCategories();
  }

  protected onCategorySelected(data:CategoryVO) {
    LogUtil.debug('CatalogCategoryComponent onCategorySelected', data);
    this.selectedCategory = data;
  }

  protected onCategoryChanged(event:FormValidationEvent<Pair<CategoryVO, Array<Pair<AttrValueCategoryVO, boolean>>>>) {
    LogUtil.debug('CatalogCategoryComponent onCategoryChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.categoryEdit = event.source.first;
    this.categoryAttributesUpdate = event.source.second;
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }


  protected onSearchParent() {
    this.categoryFilter = '^';
    this.searchHelpShow = false;
  }

  protected onSearchURI() {
    this.categoryFilter = '@';
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCategories();
  }

  protected onBackToList() {
    LogUtil.debug('CatalogCategoryComponent onBackToList handler');
    if (this.viewMode === CatalogCategoryComponent.CATEGORY) {
      this.categoryEdit = null;
      this.viewMode = CatalogCategoryComponent.CATEGORIES;
    }
  }

  protected onViewTree() {
    LogUtil.debug('CatalogCategoryComponent onViewTree handler', this.selectedCategory);
    this.categorySelectComponent.showDialog(this.selectedCategory != null ? this.selectedCategory.categoryId : 0);
  }

  protected onCatalogTreeDataSelected(event:FormValidationEvent<CategoryVO>) {
    LogUtil.debug('CatalogCategoryComponent onCatalogTreeDataSelected handler', event);
    if (event.valid) {
      this.categoryFilter = '^' + event.source.guid;
      this.getFilteredCategories();
    }
  }

  protected onRowNew() {
    LogUtil.debug('CatalogCategoryComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogCategoryComponent.CATEGORIES) {
      this.categoryEdit = this.newCategoryInstance();
      this.categoryEditAttributes = [];
      this.viewMode = CatalogCategoryComponent.CATEGORY;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('CatalogCategoryComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCategory != null) {
      this.onRowDelete(this.selectedCategory);
    }
  }


  protected onRowEditCategory(row:CategoryVO) {
    LogUtil.debug('CatalogCategoryComponent onRowEditCategory handler', row);
    this.categoryEdit = Util.clone(row);
    this.categoryEditAttributes = [];
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogCategoryComponent.CATEGORY;
    if (this.categoryEdit.categoryId > 0) {
      var _sub:any = this._categoryService.getCategoryAttributes(this.categoryEdit.categoryId).subscribe(attrs => {
        this.categoryEditAttributes = attrs;
        _sub.unsubscribe();
      });
    }
  }

  protected onRowEditSelected() {
    if (this.selectedCategory != null) {
      this.onRowEditCategory(this.selectedCategory);
    }
  }


  protected onRowLinkSelected() {
    LogUtil.debug('CatalogCategoryComponent onRowLinkSelected handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogCategoryComponent.CATEGORIES) {
      this.categoryEdit = this.newCategoryInstance();
      this.categoryEdit.linkToId = this.selectedCategory.categoryId;
      this.categoryEdit.linkToName = this.selectedCategory.name;
      this.categoryEditAttributes = [];
      this.viewMode = CatalogCategoryComponent.CATEGORY;
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.categoryEdit != null) {

        LogUtil.debug('CatalogCategoryComponent Save handler category', this.categoryEdit);

        var _sub:any = this._categoryService.saveCategory(this.categoryEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.categoryEdit.categoryId;
              LogUtil.debug('CatalogCategoryComponent category changed', rez);
              this.changed = false;
              this.selectedCategory = rez;
              this.categoryEdit = null;
              this.viewMode = CatalogCategoryComponent.CATEGORIES;

              if (pk > 0 && this.categoryAttributesUpdate != null && this.categoryAttributesUpdate.length > 0) {

                var _sub2:any = this._categoryService.saveCategoryAttributes(this.categoryAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogCategoryComponent category attributes updated', rez);
                  this.categoryAttributesUpdate = null;
                  this.getFilteredCategories();
                });
              } else {
                if (this.categoryFilter == null || this.categoryFilter == '') {
                  this.categoryFilter = rez.guid;
                }
                this.getFilteredCategories();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('CatalogCategoryComponent discard handler');
    if (this.viewMode === CatalogCategoryComponent.CATEGORY) {
      if (this.selectedCategory != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CatalogCategoryComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCategory != null) {
        LogUtil.debug('CatalogCategoryComponent onDeleteConfirmationResult', this.selectedCategory);

        var _sub:any = this._categoryService.removeCategory(this.selectedCategory).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('CatalogCategoryComponent removeCategory', this.selectedCategory);
          this.selectedCategory = null;
          this.categoryEdit = null;
          this.getFilteredCategories();
        });
      }
    }
  }

  protected onClearFilter() {

    this.categoryFilter = '';
    this.getFilteredCategories();

  }

  private getFilteredCategories() {
    this.categoryFilterRequired = !this.forceShowAll && (this.categoryFilter == null || this.categoryFilter.length < 2);

    LogUtil.debug('CatalogCategoryComponent getFilteredCategories' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.categoryFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._categoryService.getFilteredCategories(this.categoryFilter, max).subscribe( allcategories => {
        LogUtil.debug('CatalogCategoryComponent getFilteredCategories', allcategories);
        this.categories = allcategories;
        this.selectedCategory = null;
        this.categoryEdit = null;
        this.viewMode = CatalogCategoryComponent.CATEGORIES;
        this.changed = false;
        this.validForSave = false;
        this.categoryFilterCapped = this.categories.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.categories = [];
      this.selectedCategory = null;
      this.categoryEdit = null;
      this.categoryEditAttributes = null;
      this.viewMode = CatalogCategoryComponent.CATEGORIES;
      this.changed = false;
      this.validForSave = false;
      this.categoryFilterCapped = false;
    }
  }

}
